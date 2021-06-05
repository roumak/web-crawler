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
import java.util.stream.Collectors;

class Fetcher {


    public Set<String> fetch(String url) {
        Set<String> uris = new HashSet<>();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(1000)
                    .execute();

            if (response.statusCode() == 200) {
                Elements links = response.parse().select("a[href]");
                uris = links.stream()
                        .map(link -> link.attr("href"))
                        .filter(uri -> uri.startsWith("/"))
                        .collect(Collectors.toSet());
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return uris;
    }
}
