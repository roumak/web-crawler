package org.rc.webcrawler;

import java.util.function.Function;

class URLNormalizer {

    private String urlDomain;

    URLNormalizer(String urlDomain){
        this.urlDomain=urlDomain;
    }

    private final Function<String,String> urlDomainNormalizer = url -> {
        if(!url.contains(urlDomain)){
            return urlDomain+url;
        }
        return url;
    };

    private final Function<String,String> lastSlash = url-> {
        if(!url.endsWith("/")){
            return url+"/";
        }
        return url;
    };

    public String normalize(String uri) {
       return urlDomainNormalizer
               .andThen(lastSlash)
               .apply(uri);
    }
}
