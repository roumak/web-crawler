package org.rc.webcrawler.lib;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebCrawler {

    private final BlockingQueue<String> queue;
    private final Cache cache;
    private final Writer writer;

    private ExecutorService executor;
    private long timeoutInMillis;
    private int poolSize;
    private URLNormalizer normalizer;

    private final ReentrantLock lock = new ReentrantLock();
    private final Logger logger = Logger.getLogger(WebCrawler.class.getName());

    public WebCrawler(BlockingQueue<String> queue,
                      Cache cache,
                      Writer writer) {
        this.queue = queue;
        this.writer = writer;
        this.cache = cache;
        this.poolSize = 128;
        this.timeoutInMillis = 10_000;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public void setTimeout(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    public void startCrawling(String startUrl) {
        preSets(startUrl);
        logger.info(String.format("Initiating web crawling, startUrl=%s, poolSize=%d%n", startUrl, poolSize));
        // start crawling
        startCrawling();
    }

    private void preSets(String startUrl) {
        this.executor = Executors.newFixedThreadPool(this.poolSize);
        normalizer = new URLNormalizer(startUrl);
        queue.offer(normalizer.normalize(startUrl));
    }

    private void startCrawling() {
        long start = System.currentTimeMillis();
        start();
        logger.info(String.format("WebCrawler -- main thread exit, process completed in [%d] sec, but was waiting for additional [%d] sec to see if new URL appears %n",
                (System.currentTimeMillis() - timeoutInMillis - start) / 1000,
                timeoutInMillis / 1000));
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
        return WebPageHandler.FETCHER.apply(url, timeoutInMillis)
                .flatMap(page -> WebPageHandler.LINK_EXTRACTOR.apply(page, urlFilter))
                .map(urlStream -> urlStream.map(normalizer::normalize).collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    private void output(String currUrl, Set<String> links) {
        writer.write(currUrl + " -> " + links.toString());
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
