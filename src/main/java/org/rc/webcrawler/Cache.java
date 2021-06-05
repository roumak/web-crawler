package org.rc.webcrawler;

import java.util.concurrent.ConcurrentHashMap;

interface Cache {

    public void put(String url);

    public boolean contain(String url);
}
