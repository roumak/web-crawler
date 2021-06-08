package org.rc.webcrawler;

import java.util.concurrent.atomic.AtomicInteger;

class ConsoleOutputStrategy implements OutputStrategy {

    private final AtomicInteger lineItemNumber = new AtomicInteger();

    @Override
    public void output(String line) {
        System.out.printf("[ %d ] - %s \n", lineItemNumber.incrementAndGet(), line);
    }
}
