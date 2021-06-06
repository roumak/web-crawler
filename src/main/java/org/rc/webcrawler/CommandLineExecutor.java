package org.rc.webcrawler;

public class CommandLineExecutor {

    /**
     * You can run the {@link CommandLineExecutorTest} to see the results
     *
     */
    public static void main(String[] args) throws InterruptedException {
        String url = args[0];
        int pool = Integer.parseInt(args[1]);


        WebCrawler webCrawler = new WebCrawler("crawler1",
                new InMemoryConcurrentQueue(),
                new InMemoryCache(),
                new ConsoleOutputStrategy(),
                new Fetcher(),
                pool);


        webCrawler.startCrawling(url);
    }

}
