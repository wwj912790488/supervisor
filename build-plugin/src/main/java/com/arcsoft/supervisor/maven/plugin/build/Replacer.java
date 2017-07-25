package com.arcsoft.supervisor.maven.plugin.build;

import java.io.IOException;

/**
 * A resource replace interface.
 *
 * @author zw.
 */
public interface Replacer {

    void replace(Replace replace) throws IOException;
}
