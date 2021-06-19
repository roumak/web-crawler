package org.rc.webcrawler.core;

import org.jsoup.Connection;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebCrawler {

    private final BlockingQueue<String> queue;
    private final Cache cache;
    private final Writer writer;
    private final ExecutorService executor;
    private final WebPageHandler webPageHandler = new WebPageHandler();

    private URLNormalizer normalizer;

    private final ReentrantLock lock = new ReentrantLock();
    private final Logger logger = Logger.getLogger(WebCrawler.class.getName());

    public WebCrawler(BlockingQueue<String> queue,
                      Cache cache,
                      Writer writer,
                      ExecutorService executorService) {
        this.queue = queue;
        this.writer = writer;
        this.cache = cache;
        this.executor = executorService;
    }

    public void startCrawling(String startUrl, int timeoutInMillis, Predicate<String> pageUrlFilter) {
        startCrawling(startUrl, timeoutInMillis, page -> { /* do nothing */ }, pageUrlFilter);
    }

    public void startCrawling(String startUrl, int timeoutInMillis, Consumer<Optional<Connection.Response>> pageAction, Predicate<String> pageUrlFilter) {
        preSets(startUrl);
        logger.info(String.format("Initiating web crawling, startUrl=%s", startUrl));
        // start crawling
        begin(pageUrlFilter, pageAction, timeoutInMillis);
    }

    private void preSets(String startUrl) {
        normalizer = new URLNormalizer(startUrl);
        queue.offer(normalizer.normalize(startUrl));
    }

    private void begin(Predicate<String> pageUrlFilter, Consumer<Optional<Connection.Response>> responseAction, int timeoutInMillis) {
        long start = System.currentTimeMillis();
        try {
            String url;
            while ((url = queue.poll(timeoutInMillis, TimeUnit.MILLISECONDS)) != null) {
                final String currentPageUrl = url;
                var pageCf =
                        CompletableFuture.supplyAsync(() -> fetch(currentPageUrl, timeoutInMillis), executor);
                pageCf.thenAcceptAsync(responseAction, executor);

               var subUrlCf =  pageCf.thenApply(page -> extractUrls(page, pageUrlFilter));
                subUrlCf.thenAcceptAsync(this::saveToQueue);
                subUrlCf.thenAcceptAsync(subUrl -> writer.write(currentPageUrl , subUrl));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            logger.info(String.format("WebCrawler -- main thread exit, process completed in [%d] sec, but was waiting for additional [%d] sec to see if new URL appears %n",
                    (System.currentTimeMillis() - timeoutInMillis - start) / 1000,
                    timeoutInMillis / 1000));
        }
    }

    private Optional<Connection.Response> fetch(String url, int timeoutInMillis) {
        return webPageHandler.PAGE_FETCHER.apply(url, timeoutInMillis);
    }

    private Set<String> extractUrls(Optional<Connection.Response> response, Predicate<String> urlFilter) {
        return response
                .flatMap(page -> webPageHandler.URL_EXTRACTOR.apply(page, urlFilter))
                .map(urlStream -> urlStream.map(normalizer::normalize).collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }


    private void saveToQueue(Set<String> normalizedUrls) {
        // possible multiple batch of urls may overlap each other here
        // that's why a lock is introduced
        lock.lock();
        try {
            normalizedUrls.parallelStream().forEach(each -> {
                if (!cache.contain(each)) {
                    cache.put(each);
                    queue.offer(each);
                }
            });
        }
        finally{
            lock.unlock();
        }
    }
}
