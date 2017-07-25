package com.arcsoft.supervisor.maven.plugin.build;

import java.util.List;

/**
 *
 * A class correspond to <code>include</code> node of configuration.
 *
 * @author zw.
 */
public class Include {

    private String classAnnotation;

    private List<String> patterns;

    public String getClassAnnotation() {
        return classAnnotation;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("com.arcsoft.supervisor.maven.plugin.build.Include{");
        sb.append("classAnnotation='").append(classAnnotation).append('\'');
        sb.append(", patterns=").append(patterns);
        sb.append('}');
        return sb.toString();
    }
}
