package org.rc.webcrawler.lib;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.rc.webcrawler.lib.helpers.TestConsoleWriter;

import java.util.Optional;
import java.util.concurrent.Executors;
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

    @Test
    public void WebPageHandlerTest() {
        Stream<String> urlStream = IntStream.rangeClosed(1, 100).boxed().map(i -> "/url" + i);
        try (MockedStatic<WebPageHandler> mockedStatic = Mockito.mockStatic(WebPageHandler.class)) {
            mockedStatic.when(() -> WebPageHandler.URL_EXTRACTOR.apply(any(Connection.Response.class), any(Predicate.class)))
                    .thenReturn(Optional.of(urlStream))
                    .thenReturn(Optional.empty());

            Connection.Response mockResponse = Mockito.mock(Connection.Response.class);
            mockedStatic.when(() -> WebPageHandler.PAGE_FETCHER.apply(anyString(), any(Integer.class)))
                    .thenReturn(Optional.ofNullable(mockResponse));

            Assertions.assertEquals(mockResponse, WebPageHandler.PAGE_FETCHER.apply("/", 100).get());
            Stream<String> actualUrlSet = WebPageHandler.URL_EXTRACTOR.apply(Mockito.mock(HttpConnection.Response.class), s -> s.startsWith("/")).get();
            Assertions.assertEquals(urlStream, actualUrlSet);
        }
    }

}