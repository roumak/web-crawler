package org.rc.webcrawler.app;

import org.jsoup.Connection;
import org.rc.webcrawler.lib.WebCrawler;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Simple command line app that uses {@link WebCrawler}
 */
public class CommandLineExecutor {
    static final ExecutorService executorService = Executors.newFixedThreadPool(128);

    private static final WebCrawler webCrawler = new WebCrawler(new LinkedBlockingQueue<>(),
            new InMemoryConcurrentCache(),
            new ConsoleWriter(),
            executorService);

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("need two fields\n" +
                    "args[0] - start url, ex-https://monzo.com,\n" +
                    "args[1] - timeout in mills\n");
        }
        webCrawler.startCrawling(args[0],
                Integer.parseInt(args[1]),
                subUrls -> subUrls.startsWith("/")
        );
    }
}
