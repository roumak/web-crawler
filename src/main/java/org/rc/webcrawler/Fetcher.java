package org.rc.webcrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Fetcher {

    private final Predicate<String> urlFilter;
    private final long timeout;
    private final TimeUnit timeUnit;

    public Fetcher(long timeout, TimeUnit timeUnit){
        urlFilter = url -> url.startsWith("/");
        this.timeout =timeout;
        this.timeUnit = timeUnit;
    }

    public Fetcher(Predicate<String> urlFilter, long timeout, TimeUnit timeUnit){
        this.urlFilter=urlFilter;
        this.timeout =timeout;
        this.timeUnit = timeUnit;
    }

    public Set<String> fetch(String url) {
        Set<String> uris = new HashSet<>();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout((int) timeUnit.toMillis(timeout))
                    .ignoreContentType(true)
                    .execute();

                Elements links = response.parse().select("a[href]");
                uris = links.stream()
                        .map(link -> link.attr("href"))
                        .filter(urlFilter)
                        .collect(Collectors.toSet());


        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return uris;
    }
}
