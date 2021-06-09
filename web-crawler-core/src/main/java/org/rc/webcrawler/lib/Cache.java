package org.rc.webcrawler.lib;

/**
 * Adapter definition for caching
 */
public interface Cache {

    void put(String url);

    boolean contain(String url);
}
