package org.rc.webcrawler.core;

/**
 * Adapter definition for caching
 */
public interface Cache {

    void put(String url);

    boolean contain(String url);
}
