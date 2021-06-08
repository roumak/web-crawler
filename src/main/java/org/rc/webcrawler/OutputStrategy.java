package org.rc.webcrawler;

@FunctionalInterface
public interface OutputStrategy {
    void output(String line);
}
