package org.rc.webcrawler.lib.e2etest;

import org.junit.jupiter.api.Test;
import org.rc.webcrawler.lib.WebCrawler;
import org.rc.webcrawler.lib.helpers.TestConsoleWriter;
import org.rc.webcrawler.lib.helpers.TestInMemoryCache;

import java.util.concurrent.LinkedBlockingQueue;

// profiles will be set here for specific configuration
// in different environments
public class WebCrawlerE2ETest {

    protected static final String START_URL = "https://monzo.com";

    @Test
    //~2850 urls, takes almost <50 secs, cmd line is little faster, I wonder why
    public void webCrawlerTest1() {
        WebCrawler webCrawler = new WebCrawler(new LinkedBlockingQueue<>(), new TestInMemoryCache(), new TestConsoleWriter());
        webCrawler.setTimeout(10_000);
        webCrawler.setPoolSize(64);
        webCrawler.startCrawling(START_URL);
    }

    @Test
    public void webCrawlerTest2() {
        WebCrawler webCrawler = new WebCrawler(new LinkedBlockingQueue<>(), new TestInMemoryCache(), new TestConsoleWriter());
        webCrawler.setPoolSize(8);
        webCrawler.startCrawling(START_URL);
    }

}