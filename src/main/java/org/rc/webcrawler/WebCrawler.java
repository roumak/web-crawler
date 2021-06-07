package org.rc.webcrawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WebCrawler {

    private volatile BlockingQueue<String> queue;
    private volatile BlockingQueue<String> tempQueue = new LinkedBlockingQueue<>();

    private volatile Cache cache;
    private final OutputStrategy outputStrategy;
    private final Fetcher fetcher;
    private URLNormalizer normalizer;

    private final ForkJoinPool executor;
    private final ForkJoinPool cpuExecutor = new ForkJoinPool(4);
    private final int poolSize;

    public WebCrawler() {
        this(200);
    }

    public WebCrawler(int poolSize) {
        this.queue = new LinkedBlockingQueue<>();
        this.outputStrategy = new ConsoleOutputStrategy();
        this.fetcher = new Fetcher(10, TimeUnit.SECONDS);
        this.cache = new InMemoryCache();
        this.poolSize = poolSize;
        this.executor = new ForkJoinPool(poolSize);
    }

    public WebCrawler(String id,
                      BlockingQueue<String> queue,
                      Cache cache,
                      OutputStrategy outputStrategy,
                      Fetcher fetcher,
                      int threadPool) {

        this.queue = queue;
        this.outputStrategy = outputStrategy;
        this.fetcher = fetcher;
        this.cache = cache;
        this.poolSize = threadPool;
        this.executor = new ForkJoinPool(threadPool);
    }

    public void startCrawling(String seedUrl, long timeout, TimeUnit timeUnit) {
        if (!seedUrl.startsWith("http")) {
            throw new IllegalArgumentException("invalid url, correct example: https://monzo.com");
        }
        normalizer = new URLNormalizer(seedUrl);
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
                    var completableFutureLinks = CompletableFuture.supplyAsync(() -> crawl(url), executor);
                    completableFutureLinks.thenAcceptAsync(this::saveToTempQueue, cpuExecutor);
                    completableFutureLinks.thenAcceptAsync(link -> print(url, link), cpuExecutor);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Set<String> crawl(String url) {
        return fetcher.fetch(url);
    }

    private void print(String currUrl, Set<String> links) {
        cpuExecutor.submit(() -> outputStrategy.print(
                Thread.currentThread().getName()
                        + " - "
                        + currUrl
                        + " -> "
                        + links.toString()
                )
        );
    }

    // learnt about it from here: https://blog.krecan.net/2014/03/18/how-to-specify-thread-pool-for-java-8-parallel-streams/
    // ugly though
    private void saveToTempQueue(Set<String> links) {
        cpuExecutor.submit(() ->
                links.stream().parallel().forEach(each -> {
                    String normalizedUrl = normalizer.normalize(each);
                    if (!cache.contain(normalizedUrl)) {
                        cache.put(normalizedUrl);
                        tempQueue.offer(normalizedUrl);
                    }
                })
        );
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
