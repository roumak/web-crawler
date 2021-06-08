package org.rc.webcrawler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class ExecPool_ForkJoinPoolTest {
    private static final String SEED_URL = "https://monzo.com";


    @Test
    public void test_Exec() throws InterruptedException {
        WebPageHandler webPageHandler = new WebPageHandler(10, TimeUnit.SECONDS);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        long start = System.currentTimeMillis();
        for(int i=0;i<100;i++){
            executorService.submit(() -> webPageHandler.fetch.apply(SEED_URL));
        }
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        System.out.println("Exec TP: time taken: " + (end - start) / 1000 + " sec");

    }


    @Test
    public void test_ForkJoin() throws InterruptedException, ExecutionException {
        WebPageHandler webPageHandler = new WebPageHandler(10,TimeUnit.SECONDS);
        ForkJoinPool mainPool = new ForkJoinPool(100);

        long start = System.currentTimeMillis();
        for(int i=0;i<100;i++){
            mainPool.submit(() -> webPageHandler.fetch.apply(SEED_URL));
        }
        mainPool.shutdown();
        mainPool.awaitTermination(100, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        System.out.println("Fork/Join: time taken: " + (end - start) / 1000 + " sec");

    }

    @Test
    public void teststream(){
        WebPageHandler webPageHandler = new WebPageHandler(10,TimeUnit.SECONDS);

        long start = System.currentTimeMillis();
        IntStream.rangeClosed(1,100).boxed().parallel()
                .forEach(i-> webPageHandler.fetch.apply(SEED_URL));

        long end = System.currentTimeMillis();
        System.out.println("stream: time taken: " + (end - start) / 1000 + " sec");

    }

    @Test
    public void teststream_onForkJoin() throws InterruptedException {
        WebPageHandler webPageHandler = new WebPageHandler(10,TimeUnit.SECONDS);
        ForkJoinPool mainPool = new ForkJoinPool(100);

        long start = System.currentTimeMillis();
        mainPool.submit(()->IntStream.rangeClosed(1,100).boxed().parallel()
                .forEach(i-> webPageHandler.fetch.apply(SEED_URL)));

        mainPool.shutdown();
        mainPool.awaitTermination(100, TimeUnit.SECONDS);


        long end = System.currentTimeMillis();
        System.out.println("stream: time taken: " + (end - start) / 1000 + " sec");

    }

}