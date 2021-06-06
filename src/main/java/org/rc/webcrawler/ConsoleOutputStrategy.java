package org.rc.webcrawler;

import java.util.concurrent.atomic.AtomicInteger;

class ConsoleOutputStrategy implements OutputStrategy {

    AtomicInteger num= new AtomicInteger();

    @Override
    public void print(String line) {
        System.out.println(num.incrementAndGet() +" - " + line);
    }
}
