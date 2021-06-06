package org.rc.webcrawler;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandLineExecutorTest {


    private static String SEED_URL = "https://monzo.com";


    @Test
    //~2850 urls, takes almost 118 - 140 secs, in my Intellij, cmd line is faster
    public void singleWebCrawlerInstance() {
        WebCrawler webCrawler = createWebCrawler(1, 100);
        webCrawler.startCrawling(SEED_URL);
    }

    @Test
    //~ 2850 urls, This takes almost 106-160 secs total,
    // but most of it is done within 60 sec,
    // the last "https://monzo.com/blog/*/" takes time
    public void multipleWebCrawlerInstance() throws InterruptedException {
        multipleWebCrawlerInstance(10,10); // effectively 100
    }

    private void multipleWebCrawlerInstance(int numOfInstance, int pool) throws InterruptedException {
        Cache sharedCache = new InMemoryCache();
        Queue<String> sharedQueue = new InMemoryConcurrentQueue();
        OutputStrategy sharedOutputStrategy = new ConsoleOutputStrategy();

        ExecutorService executorService = Executors.newFixedThreadPool(numOfInstance);
        List<Callable<Boolean>> sharedCallables = IntStream.rangeClosed(1, numOfInstance)
                .boxed()
                .parallel()
                .map(i -> createWebCrawler(i, sharedQueue, sharedCache, sharedOutputStrategy, pool))
                .map(this::toCallable)
                .collect(Collectors.toList());

        executorService.invokeAll(sharedCallables);

    }

    private Callable<Boolean> toCallable(WebCrawler crawler) {
        return () -> {
            crawler.startCrawling(SEED_URL);
            return true;
        };
    }

    public WebCrawler createWebCrawler(int id, int pool) {
        return createWebCrawler(id, null, null, null, pool);
    }

    private WebCrawler createWebCrawler(int id,
                                       Queue<String> queue,
                                       Cache cache,
                                       OutputStrategy outputStrategy,
                                       int pool) {

        return new WebCrawler("test-crawler" + id,
                queue == null ? new InMemoryConcurrentQueue() : queue,
                cache == null ? new InMemoryCache() : cache,
                outputStrategy == null ? new ConsoleOutputStrategy() : outputStrategy,
                new Fetcher(),
                pool);
    }

}