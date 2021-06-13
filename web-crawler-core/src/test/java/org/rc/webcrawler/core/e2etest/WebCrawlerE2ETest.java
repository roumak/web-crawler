package org.rc.webcrawler.core.e2etest;

import org.junit.jupiter.api.Test;
import org.rc.webcrawler.core.WebCrawler;
import org.rc.webcrawler.core.helpers.TestConsoleWriter;
import org.rc.webcrawler.core.helpers.TestInMemoryCache;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Profiles will be set here for specific configuration
// in specific environments
class WebCrawlerE2ETest {

    protected static final String START_URL = "https://monzo.com";
    WebCrawler webCrawler = new WebCrawler(new LinkedBlockingQueue<>(),
            new TestInMemoryCache(),
            new TestConsoleWriter(),
            new ThreadPoolExecutor(16,128, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));


    @Test
    //~2850 urls, takes almost <50 secs, cmd line is little faster, I wonder why
    public void webCrawlerTest1() {
        webCrawler.startCrawling(START_URL, 10_000,subUrl -> subUrl.startsWith("/"));
    }

    @Test
    public void webCrawlerTest2() {
        webCrawler.startCrawling(START_URL,10_000,subUrl ->subUrl.startsWith("/"));
    }

}