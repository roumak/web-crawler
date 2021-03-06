package org.rc.webcrawler.app;

import org.rc.webcrawler.core.Cache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryConcurrentCache implements Cache {

    private final Set<String> cache = ConcurrentHashMap.newKeySet();

    public void put(String url) {
        cache.add(url);
    }

    public boolean contain(String url) {
        return cache.contains(url);
    }
}
