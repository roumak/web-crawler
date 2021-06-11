package org.rc.webcrawler.lib;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

class WebPageHandler {

    private static final Logger logger = Logger.getLogger(WebPageHandler.class.getName());

    public static final BiFunction<String, Integer, Optional<Connection.Response>> PAGE_FETCHER = WebPageHandler::fetch;
    public static final BiFunction<Connection.Response, Predicate<String>, Optional<Stream<String>>> URL_EXTRACTOR = WebPageHandler::extract;


    private static Optional<Connection.Response> fetch(String url, int timeoutInMills) {
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

    private static Optional<Stream<String>> extract(Connection.Response response, Predicate<String> subUrlFilter) {
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
