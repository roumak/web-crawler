package org.rc.webcrawler;

public class CommandLineExecutor {

    public static void main(String[] args) {
        WebCrawler webCrawler;
        switch (args.length){
            case 1:
                webCrawler = new WebCrawler();
                break;
            case 2:
                webCrawler = new WebCrawler(Integer.parseInt(args[1]));
                break;
            case 3:
                webCrawler = new WebCrawler(Integer.parseInt(args[1]), Long.parseLong(args[2]));
                break;
            default:
                throw new IllegalArgumentException("\nrequired command line arguments:" +
                        "\n arg[0] - startUrl (mandatory) example: https://monzo.com" +
                        "\n arg[1] - poolSize (optional) defaults to 128"+
                        "\n arg[2] - timeout in millis (optional) defaults to 10_000"
                );
        }
        webCrawler.startCrawling(args[0]);
    }

}
