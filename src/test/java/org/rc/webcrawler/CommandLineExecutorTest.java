package org.rc.webcrawler;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandLineExecutorTest {

    protected static final String SEED_URL = "https://monzo.com";

    @Test
    //~2850 urls, takes almost 118 - 140 secs, in my Intellij, cmd line is faster
    public void webCrawlerTest1() {
        WebCrawler webCrawler = createWebCrawler(1, 200);
        webCrawler.startCrawling(SEED_URL, 10 ,TimeUnit.SECONDS);
    }

    @Test
    public void webCrawlerTest2() {
        WebCrawler webCrawler = createWebCrawler(2, 20);
        webCrawler.startCrawling(SEED_URL, 10 ,TimeUnit.SECONDS);
    }


    protected WebCrawler createWebCrawler(int id, int pool) {
        return createWebCrawler(id, null, null, null, pool);
    }

    protected WebCrawler createWebCrawler(int id, BlockingQueue<String> queue, Cache cache, OutputStrategy outputStrategy, int pool) {

        return new WebCrawler("test-crawler" + id,
                queue == null ? new LinkedBlockingQueue<>() : queue,
                cache == null ? new InMemoryCache() : cache,
                outputStrategy == null ? new ConsoleOutputStrategy() : outputStrategy,
                new Fetcher(10,TimeUnit.SECONDS),
                pool);
    }




}