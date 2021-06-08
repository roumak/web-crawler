package org.rc.webcrawler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CommandLineExecutorTest {

    protected static final String SEED_URL = "https://monzo.com";

    @Test
    //~2850 urls, takes almost <100 secs, in my Intellij, cmd line is faster
    public void webCrawlerTest1() {
        WebCrawler webCrawler = createWebCrawler( 200);
        webCrawler.startCrawling(SEED_URL, 10 ,TimeUnit.SECONDS);
    }

    @Test
    public void webCrawlerTest2() {
        WebCrawler webCrawler = createWebCrawler( 20);
        webCrawler.startCrawling(SEED_URL, 10 ,TimeUnit.SECONDS);
    }

    protected WebCrawler createWebCrawler(int pool) {
        return createWebCrawler( null, null, null, pool);
    }

    protected WebCrawler createWebCrawler(BlockingQueue<String> queue, Cache cache, OutputStrategy outputStrategy, int pool) {

        return new WebCrawler(
                queue == null ? new LinkedBlockingQueue<>() : queue,
                cache == null ? new InMemoryConcurrentCache() : cache,
                outputStrategy == null ? new ConsoleOutputStrategy() : outputStrategy,
                new WebPageHandler(10,TimeUnit.SECONDS),
                pool);
    }

}