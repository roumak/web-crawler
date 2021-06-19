package org.rc.webcrawler.core;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.rc.webcrawler.core.Cache;
import org.rc.webcrawler.core.WebCrawler;
import org.rc.webcrawler.core.Writer;
import org.rc.webcrawler.core.helpers.TestConsoleWriter;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;

class WebCrawlerTest {

    Cache mockCache = Mockito.mock(Cache.class);
    Writer writer = new TestConsoleWriter();
    WebCrawler webCrawler = new WebCrawler(new LinkedBlockingQueue<>(), mockCache, writer, new ThreadPoolExecutor(8,32,3, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));

    @Test
    public void startUrlValidation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> webCrawler.startCrawling("google.com", 10_000,url -> url.startsWith("/")));
    }

}