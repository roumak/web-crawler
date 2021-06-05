package org.rc.webcrawler;

/**
 *
 */
public interface Queue {

    String take();

    void put(String url);
}
