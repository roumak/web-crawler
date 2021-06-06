package org.rc.webcrawler;

import java.util.concurrent.ConcurrentHashMap;

class InMemoryCache implements Cache {

    private ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();

    public void put(String url){
        cache.putIfAbsent(url, 1);
        cache.computeIfPresent(url,(k,v)->v+1);
    }

    public boolean contain(String url){
        return cache.containsKey(url);
    }
}
