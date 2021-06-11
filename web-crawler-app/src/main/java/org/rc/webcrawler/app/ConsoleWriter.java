package org.rc.webcrawler.app;

import org.rc.webcrawler.lib.Writer;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class ConsoleWriter implements Writer {

    private final AtomicInteger lineItemNumber = new AtomicInteger();

    @Override
    public void write(String parentUrl, Set<String> subUrl) {
        System.out.printf("[ %d ] - %s -> %s \n", lineItemNumber.incrementAndGet(), parentUrl, subUrl.toString());
    }
}
