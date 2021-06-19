package org.rc.webcrawler.core;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

class WebPageHandler {

    private final Logger logger = Logger.getLogger(WebPageHandler.class.getName());

    public final BiFunction<String, Integer, Optional<Connection.Response>> PAGE_FETCHER;
    public final BiFunction<Connection.Response, Predicate<String>, Optional<Stream<String>>> URL_EXTRACTOR;

    WebPageHandler(){
        PAGE_FETCHER = this::fetch;
        URL_EXTRACTOR = this::extract;
    }

    WebPageHandler(BiFunction<String, Integer,Optional<Connection.Response>> fetcher,BiFunction<Connection.Response, Predicate<String>, Optional<Stream<String>>> extractor){
        PAGE_FETCHER = fetcher;
        URL_EXTRACTOR = extractor;
    }

    private Optional<Connection.Response> fetch(String url, int timeoutInMills) {
        try {
            return Optional.of(
                    Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(timeoutInMills)
                            .ignoreContentType(true)
                            .execute()
            );

        } catch (IOException e) {
            logger.warning(String.format("error fetching url=%s, %s", url, e.getMessage()));
            return Optional.empty();
        }

    }

    private Optional<Stream<String>> extract(Connection.Response response, Predicate<String> subUrlFilter) {
        try {
            return Optional.of(
                    response.parse().select("a[href]")
                            .parallelStream()
                            .map(url -> url.attr("href"))
                            .filter(subUrlFilter));

        } catch (IOException e) {
            logger.warning(String.format("error while extracting urls, %s", e.getMessage()));
            return Optional.empty();
        }
    }

}
