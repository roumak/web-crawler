package org.rc.webcrawler;

public interface Cache {

    void put(String url);

    boolean contain(String url);
}
