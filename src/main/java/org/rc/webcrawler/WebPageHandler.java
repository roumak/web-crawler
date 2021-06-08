package org.rc.webcrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class WebPageHandler {

    public static final BiFunction<String, Long, Optional<Connection.Response>> fetch = WebPageHandler::fetch;

    public static final BiFunction<Connection.Response, Predicate<String>, Optional<Stream<String>>> filter = WebPageHandler::filter;


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
            System.out.println(e.getMessage());
            return Optional.empty();
        }

    }

    private static Optional<Stream<String>> filter(Connection.Response response, Predicate<String> urlFilter) {
        try {
            return Optional.of(
                    response.parse().select("a[href]")
                            .parallelStream()
                            .map(link -> link.attr("href"))
                            .filter(urlFilter));

        } catch (IOException e) {
            System.out.println(response.statusCode() + e.getMessage());
            return Optional.empty();
        }
    }

}
