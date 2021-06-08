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

class WebPageHandler {

    private URLNormalizer normalizer;
    private final long timeout;
    private final TimeUnit timeUnit;

    public Function<String, Optional<Connection.Response>> fetch = this::fetch;

    public BiFunction<Connection.Response, Predicate<String>, Optional<Set<String>>> filter = this::filter;

    public WebPageHandler(long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public void setNormalizer(URLNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    private Optional<Connection.Response> fetch(String url) {
        try {
            return Optional.of(
                    Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout((int) timeUnit.toMillis(timeout))
                            .ignoreContentType(true)
                            .execute()
            );

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }

    }

    private Optional<Set<String>> filter(Connection.Response response, Predicate<String> urlFilter) {
        try {
            return Optional.of(
                    response.parse().select("a[href]")
                            .stream()
                            .map(link -> link.attr("href"))
                            .filter(urlFilter)
                            .map(filteredUrl -> normalizer == null ? filteredUrl : normalizer.normalize(filteredUrl))
                            .collect(Collectors.toSet())
            );

        } catch (IOException e) {
            System.out.println(response.statusCode() + e.getMessage());
            return Optional.empty();
        }
    }

}
