package org.rc.webcrawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandLineExecutor {

    /**
     * You can run the {@link CommandLineExecutorTest} to see the results
     *
     */
    public static void main(String[] args) throws InterruptedException {
        String url = args[0];
        int pool = Integer.parseInt(args[1]);

        BlockingQueue<String> queue =  new LinkedBlockingQueue<>();
        Cache cache = new InMemoryCache();
        OutputStrategy outputStrategy = new ConsoleOutputStrategy();

        WebCrawler webCrawler = new WebCrawler("crawler1",
                queue,
                cache,
                outputStrategy,
                new Fetcher(),
                pool);


        Thread t1 = new Thread(()->webCrawler.startCrawling(url));
        t1.start();
        t1.join();
    }

}
