package org.rc.webcrawler.core.helpers;

import org.rc.webcrawler.core.Writer;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Similar Implementation as {@link org.rc.webcrawler.app.ConsoleWriter }
 * but the context is different, this is used for testing the library, other one is
 * used for the app
 * <p>
 * These two impl have very different reasons to change.
 */
public class TestConsoleWriter implements Writer {

    private final AtomicInteger lineItemNumber = new AtomicInteger();

    @Override
    public void write(String parentUrl, Set<String> subUrl) {
        System.out.printf("[ %d ] - %s -> %s \n", lineItemNumber.incrementAndGet(), parentUrl, subUrl.toString());
    }
}
