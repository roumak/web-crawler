package org.rc.webcrawler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public class WebCrawler {

    private final BlockingQueue<String> queue;
    private final BlockingQueue<String> tempQueue = new LinkedBlockingQueue<>();

    private final Cache cache;
    private final OutputStrategy outputStrategy;
    private final WebPageHandler webPageHandler;
    private URLNormalizer normalizer;

    private final ExecutorService executor;
    private final int poolSize;

    private final ReentrantLock lock = new ReentrantLock();

    public WebCrawler() {
        this(128);
    }

    public WebCrawler(int poolSize) {
        this.queue = new LinkedBlockingQueue<>();
        this.outputStrategy = new ConsoleOutputStrategy();
        this.webPageHandler = new WebPageHandler(10, TimeUnit.SECONDS);
        this.cache = new InMemoryConcurrentCache();
        this.poolSize = poolSize;
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public WebCrawler(BlockingQueue<String> queue,
                      Cache cache,
                      OutputStrategy outputStrategy,
                      WebPageHandler webPageHandler,
                      int threadPool) {

        this.queue = queue;
        this.outputStrategy = outputStrategy;
        this.webPageHandler = webPageHandler;
        this.cache = cache;
        this.poolSize = threadPool;
        this.executor = Executors.newFixedThreadPool(threadPool);
    }

    public void startCrawling(String seedUrl, long timeout, TimeUnit timeUnit) {
        if (!seedUrl.startsWith("http")) {
            throw new IllegalArgumentException("invalid url, correct example: https://monzo.com");
        }
        normalizer = new URLNormalizer(seedUrl);
        webPageHandler.setNormalizer(normalizer);
        queue.offer(normalizer.normalize(seedUrl));

        System.out.printf("Initiating web crawling, seedUrl=%s, poolSize=%d%n", seedUrl, poolSize);
        executor.submit(() -> repopulate(timeout, timeUnit));

        // start crawling
        startCrawling(timeUnit.toMillis(timeout));
    }

    private void startCrawling(long timeout) {
        long start = System.currentTimeMillis();
        start();
        System.out.printf("WebCrawler -- main thread exit, process completed in [%d] sec, but was waiting for additional [%d] sec to see if new URL appears %n",
                (System.currentTimeMillis() - timeout - start) / 1000,
                timeout / 1000);
    }

    private void start() {
        try {
            while (!executor.isTerminated()) {
                int count = Math.min(queue.size(), poolSize);
                for (int i = 0; i < count; i++) {
                    String url = queue.take();

                    var completableFutureLinks =
                            CompletableFuture.supplyAsync(() -> fetchAndFilter(url, subUrl -> subUrl.startsWith("/")), executor);
                    completableFutureLinks.thenAcceptAsync(links -> output(url, links));
                    completableFutureLinks.thenAccept( this::saveToTempQueue);
                    //pageCompletableFuture.thenAccept(optionalResponse -> optionalResponse.ifPresent(doAction));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Set<String> fetchAndFilter(String url, Predicate<String> urlFilter) {
        return webPageHandler.fetch.apply(url)
                .flatMap(page -> webPageHandler.filter.apply(page, urlFilter))
                .orElse(new HashSet<>());
    }

    private void output(String currUrl, Set<String> links) {
       outputStrategy.output(
                         currUrl
                        + " -> "
                        + links.toString());
    }


    private void saveToTempQueue(Set<String> links) {
        links.forEach(each -> {
            String normalizedUrl = normalizer.normalize(each);
            // possible multiple batch of links may overlap each other here
            // that's why a lock is introduced
            lock.lock();
            if (!cache.contain(normalizedUrl)) {
                cache.put(normalizedUrl);
                tempQueue.offer(normalizedUrl);
            }
            lock.unlock();
        });
    }

    private void repopulate(long timeout, TimeUnit timeUnit) {
        try {
            boolean var = true;
            while (var) {
                String url = tempQueue.poll(timeout, timeUnit);
                if (url == null) {
                    var = false;
                    executor.shutdown();
                } else {
                    queue.offer(url);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
