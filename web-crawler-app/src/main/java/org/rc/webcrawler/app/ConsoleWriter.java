package org.rc.webcrawler.app;

import org.rc.webcrawler.lib.Writer;

import java.util.concurrent.atomic.AtomicInteger;

class ConsoleWriter implements Writer {

    private final AtomicInteger lineItemNumber = new AtomicInteger();

    @Override
    public void write(String line) {
        System.out.printf("[ %d ] - %s \n", lineItemNumber.incrementAndGet(), line);
    }
}
