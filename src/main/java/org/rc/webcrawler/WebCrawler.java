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
import java.util.stream.Collectors;

public class WebCrawler {

    private final BlockingQueue<String> queue;
    private final Cache cache;
    private final OutputStrategy outputStrategy;
    private final ExecutorService executor;
    private final long timeoutInMills;
    private final int poolSize;
    private URLNormalizer normalizer;

    private final ReentrantLock lock = new ReentrantLock();

    public WebCrawler() {
        this(128, 10_000);
    }

    public WebCrawler(int poolSize) {
        this(poolSize, 10_000);
    }

    public WebCrawler(int poolSize, long timeoutInMills) {
        this.poolSize = poolSize;
        this.timeoutInMills = timeoutInMills;
        this.queue = new LinkedBlockingQueue<>();
        this.outputStrategy = new ConsoleOutputStrategy();
        this.cache = new InMemoryConcurrentCache();
        this.executor = Executors.newFixedThreadPool(this.poolSize);
    }

    public WebCrawler(int threadPool,
                      long timeoutInMills,
                      BlockingQueue<String> queue,
                      Cache cache,
                      OutputStrategy outputStrategy) {
        this.timeoutInMills = timeoutInMills;
        this.queue = queue;
        this.outputStrategy = outputStrategy;
        this.cache = cache;
        this.poolSize = threadPool;
        this.executor = Executors.newFixedThreadPool(threadPool);
    }

    private void preSets(String startUrl) {
        normalizer = new URLNormalizer(startUrl);
        queue.offer(normalizer.normalize(startUrl));
    }

    public void startCrawling(String startUrl) {
        if (!startUrl.startsWith("http")) {
            throw new IllegalArgumentException("invalid url format, correct example: https://monzo.com");
        }
        preSets(startUrl);
        System.out.printf("Initiating web crawling, startUrl=%s, poolSize=%d%n", startUrl, poolSize);
        // start crawling
        startCrawling();
    }

    private void startCrawling() {
        long start = System.currentTimeMillis();
        start();
        System.out.printf("WebCrawler -- main thread exit, process completed in [%d] sec, but was waiting for additional [%d] sec to see if new URL appears %n",
                (System.currentTimeMillis() - timeoutInMills - start) / 1000,
                timeoutInMills / 1000);
        executor.shutdown();
    }

    private void start() {
        try {
            String url;
            while ((url = queue.poll(10, TimeUnit.SECONDS)) != null) {
                final String finalUrl = url;
                var completableFutureLinks =
                        CompletableFuture.supplyAsync(() -> fetchAndFilter(finalUrl, subUrl -> subUrl.startsWith("/")), executor);
                completableFutureLinks.thenAcceptAsync(links -> output(finalUrl, links));
                completableFutureLinks.thenAccept(this::saveToTempQueue);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Set<String> fetchAndFilter(String url, Predicate<String> urlFilter) {
        return WebPageHandler.fetch.apply(url, timeoutInMills)
                .flatMap(page -> WebPageHandler.filter.apply(page, urlFilter))
                .map(urlStream -> urlStream.map(normalizer::normalize).collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    private void output(String currUrl, Set<String> links) {
        outputStrategy.output(currUrl + " -> " + links.toString());
    }


    private void saveToTempQueue(Set<String> links) {
        // possible multiple batch of links may overlap each other here
        // that's why a lock is introduced
        lock.lock();
        links.parallelStream().forEach(each -> {
            String normalizedUrl = normalizer.normalize(each);
            if (!cache.contain(normalizedUrl)) {
                cache.put(normalizedUrl);
                queue.offer(normalizedUrl);
            }
        });
        lock.unlock();
    }

}
