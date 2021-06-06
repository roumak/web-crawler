package org.rc.webcrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebCrawler {

    private String id;
    private volatile Queue<String> queue;
    private volatile Cache cache;
    private OutputStrategy outputStrategy;
    private Fetcher fetcher;
    private URLNormalizer normalizer;

    private ExecutorService executor;
    private int threadPool;

    public WebCrawler(String id,
                      Queue<String> queue,
                      Cache cache,
                      OutputStrategy outputStrategy,
                      Fetcher fetcher,
                      int threadPool) {

        this.id = id;
        this.queue = queue;
        this.outputStrategy = outputStrategy;
        this.fetcher = fetcher;
        this.cache = cache;
        this.executor = Executors.newFixedThreadPool(threadPool);
        this.threadPool = threadPool;
    }

    public void startCrawling(String seedUrl) {
        if(!seedUrl.startsWith("http")){
            throw new RuntimeException("invalid url, correct example: https://monzo.com");
        }
        normalizer = new URLNormalizer(seedUrl);
        queue.offer(normalizer.normalize(seedUrl));
        start();
        executor.shutdown();
    }

    private void start() {

        long start = System.currentTimeMillis();
        while (!queue.isEmpty()) {
                List<Future<Set<String>>> futures = new ArrayList<>();
                for (int i = 0; i < threadPool; i++) {
                    if (queue.isEmpty()) {
                        break;
                    }
                    String url = queue.take();
                    Future<Set<String>> future = executor.submit(() -> crawl(url));
                    futures.add(future);
                }

                futures.stream().parallel().forEach(this::doJob);
        }
        System.out.println(id + "-main thread exit, time taken: "+ (System.currentTimeMillis() - start)/1000 +" sec");
    }

    private Set<String> crawl(String url) {
        Set<String> set = fetcher.fetch(url);
        print(url, set);
        return set;
    }

    private void print(String currUrl, Set<String> links) {
        StringBuilder sb = new StringBuilder();

        outputStrategy.print(
                sb.append(id)
                .append("-")
                .append(Thread.currentThread().getName())
                .append(" - ")
                .append(currUrl)
                .append(" -> ")
                .append(links.toString())
                .append("\n\n")
                        .toString()
        );
    }

    private void doJob(Future<Set<String>> future) {
        try {
            for (String each : future.get()) {
                String normalizedUrl = normalizer.normalize(each);
                if (!cache.contain(normalizedUrl)) {
                    cache.put(normalizedUrl);
                    queue.offer(normalizedUrl);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }


}
