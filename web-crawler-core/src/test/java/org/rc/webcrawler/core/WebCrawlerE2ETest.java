package org.rc.webcrawler.core;

import org.jsoup.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rc.webcrawler.core.WebCrawler;
import org.rc.webcrawler.core.helpers.TestConsoleWriter;
import org.rc.webcrawler.core.helpers.TestInMemoryCache;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

// Profiles will be set here for specific configuration
// in specific environments
class WebCrawlerE2ETest {

    private static final String START_URL = "https://monzo.com";
    private static final WebCrawler webCrawler = new WebCrawler(new LinkedBlockingQueue<>(),
            new TestInMemoryCache(),
            new TestConsoleWriter(),
            new ThreadPoolExecutor(16,128, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));

    private static Connection.Response responseMock= Mockito.mock(Connection.Response.class);

//    @BeforeAll
//    public void setup(){
//        ReflectionTestUtils.setField(webCrawler,"webPageHandler", new WebPageHandler((url,timeout)->{
//            try {
//                Thread.sleep(3_000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return Optional.of(responseMock);
//        }, ((response, subUrlPredicate) -> Optional.of(IntStrea))));
//    }

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