package org.rc.webcrawler.lib;

import java.util.Set;

/**
 * public adapter definition for writer
 */
public interface Writer {

    void write(String parentUrl, Set<String> subUrl );

}
