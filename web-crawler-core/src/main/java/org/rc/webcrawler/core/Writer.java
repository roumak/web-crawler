package org.rc.webcrawler.core;

import java.util.Set;

/**
 * public adapter definition for writer
 */
public interface Writer {

    void write(String parentUrl, Set<String> subUrl );

}
