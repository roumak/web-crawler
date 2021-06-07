package org.rc.webcrawler;

import java.util.concurrent.TimeUnit;

public class CommandLineExecutor {

    /**
     * You can run the Unit Test to see the results
     *
     */
    public static void main(String[] args) {
        WebCrawler webCrawler;
        switch (args.length){
            case 1:
                webCrawler = new WebCrawler();
                break;
            case 2:
                webCrawler = new WebCrawler(Integer.parseInt(args[1]));
                break;
            default:
                throw new IllegalArgumentException("\nrequired command line arguments:" +
                        "\n arg[0] - seedUrl (mandatory) example: https://monzo.com" +
                        "\n arg[1] - poolSize (optional) defaults to 200");
        }
        webCrawler.startCrawling(args[0], 10, TimeUnit.SECONDS);
    }

}
