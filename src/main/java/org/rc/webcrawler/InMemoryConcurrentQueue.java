package org.rc.webcrawler;

import java.util.concurrent.ConcurrentLinkedQueue;

class InMemoryConcurrentQueue implements Queue<String> {

    private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    @Override
    public String take() {
        return queue.poll();
    }

    @Override
    public void offer(String url) {
        queue.offer(url);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }


}
