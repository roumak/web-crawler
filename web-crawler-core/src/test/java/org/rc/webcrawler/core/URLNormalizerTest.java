package org.rc.webcrawler.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class URLNormalizerTest {


    @Test
    public void normalizationDomainValidationTest() {
        Assertions.assertDoesNotThrow(() -> new URLNormalizer("http://google.com"));
        Assertions.assertDoesNotThrow(() -> new URLNormalizer("https://google.com"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> new URLNormalizer("google.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new URLNormalizer("www.google.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new URLNormalizer("whtervertio"));
    }

    @Test
    public void normalizationTest() {
        String domain = "https://monzo.com";
        URLNormalizer urlNormalizer = new URLNormalizer(domain);
        String normalizedUrl = urlNormalizer.normalize("/page1");

        Assertions.assertTrue(normalizedUrl.contains("/page1"));
        Assertions.assertTrue(normalizedUrl.contains(domain));
        Assertions.assertTrue(normalizedUrl.endsWith("/"));

    }

}