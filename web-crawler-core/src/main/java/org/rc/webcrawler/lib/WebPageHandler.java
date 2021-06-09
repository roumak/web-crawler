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

    public static final BiFunction<String, Long, Optional<Connection.Response>> FETCHER = WebPageHandler::fetch;
    public static final BiFunction<Connection.Response, Predicate<String>, Optional<Stream<String>>> LINK_EXTRACTOR = WebPageHandler::extract;


    private static Optional<Connection.Response> fetch(String url, long timeoutInMills) {
        try {
            return Optional.of(
                    Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout((int) timeoutInMills)
                            .ignoreContentType(true)
                            .execute()
            );

        } catch (IOException e) {
            logger.warning(String.format("error fetching url=%s, %s", url, e.getMessage()));
            return Optional.empty();
        }

    }

    private static Optional<Stream<String>> extract(Connection.Response response, Predicate<String> urlFilter) {
        try {
            return Optional.of(
                    response.parse().select("a[href]")
                            .parallelStream()
                            .map(link -> link.attr("href"))
                            .filter(urlFilter));

        } catch (IOException e) {
            logger.warning(String.format("error while extracting links, %s", e.getMessage()));
            return Optional.empty();
        }
    }

}
