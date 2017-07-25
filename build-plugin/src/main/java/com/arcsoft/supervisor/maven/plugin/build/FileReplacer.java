package com.arcsoft.supervisor.maven.plugin.build;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * File resource replacer.
 *
 * @author zw.
 */
public class FileReplacer implements Replacer {

    @Override
    public void replace(Replace replace) throws IOException {
        if (StringUtils.isNotBlank(replace.getSource()) &&
                StringUtils.isNotBlank(replace.getTarget())) {
            File source = new File(replace.getSource());
            File target = new File(replace.getTarget());
            if (target.isDirectory()) {
                target = new File(target, source.getName());
            }
            FileUtils.copyFile(source, target);
            if (replace.getDeleteAfterSuccess()) {
                FileUtils.forceDelete(source);
            }
        }
    }
}
