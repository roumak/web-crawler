package org.rc.webcrawler.app;

import org.rc.webcrawler.lib.WebCrawler;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simple command line app that uses {@link WebCrawler}
 */
public class CommandLineExecutor {

    private static final WebCrawler webCrawler =
            new WebCrawler(new LinkedBlockingQueue<>(),
                    new InMemoryConcurrentCache(),
                    new ConsoleWriter());

    public static void main(String[] args) {
        switch (args.length) {
            case 1:
                break;
            case 2:
                webCrawler.setPoolSize(Integer.parseInt(args[1]));
                break;
            case 3:
                webCrawler.setPoolSize(Integer.parseInt(args[1]));
                webCrawler.setTimeout(Integer.parseInt(args[1]));
                break;
            default:
                throw new IllegalArgumentException("" +
                        "\nrequired command line arguments:" +
                        "\n arg[0] - startUrl (mandatory) example: https://monzo.com" +
                        "\n arg[1] - poolSize (optional) defaults to 128" +
                        "\n arg[2] - timeout in millis (optional) defaults to 10_000"
                );
        }
        webCrawler.startCrawling(args[0]);
    }

}
