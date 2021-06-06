package org.rc.webcrawler;

/**
 * Actually we don't need this,
 * If we really want to write our own implementation of a distributed
 * concurrent queue, I feel we could use {@link java.util.Queue} itself
 *
 * Still keeping it just for demonstration
 */
public interface Queue<T> {

    T take();

    void offer(T url);

    boolean isEmpty();
}
