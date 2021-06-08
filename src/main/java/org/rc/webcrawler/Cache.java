package org.rc.webcrawler;

interface Cache {

    public void put(String url);

    public boolean contain(String url);
}
