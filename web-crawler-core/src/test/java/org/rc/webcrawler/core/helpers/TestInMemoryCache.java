package org.rc.webcrawler.core.helpers;

import org.rc.webcrawler.core.Cache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Similar Implementation as {@link org.rc.webcrawler.app.InMemoryConcurrentCache}
 * but the context is different, this is used for testing the library, other one is
 * used for the app
 * <p>
 * These two impl have very different reasons to change.
 */
public class TestInMemoryCache implements Cache {

    private final Set<String> cache = ConcurrentHashMap.newKeySet();

    public void put(String url) {
        cache.add(url);
    }

    public boolean contain(String url) {
        return cache.contains(url);
    }
}
