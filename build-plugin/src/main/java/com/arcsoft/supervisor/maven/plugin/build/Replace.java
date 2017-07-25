package com.arcsoft.supervisor.maven.plugin.build;

/**
 *
 * A class correspond to <code>replace</code> node.
 *
 * @author zw.
 */
public class Replace {

    private String source;

    private String target;

    private Boolean deleteAfterSuccess = false;

    public Boolean getDeleteAfterSuccess() {
        return deleteAfterSuccess;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("com.arcsoft.supervisor.maven.plugin.build.Replace{");
        sb.append("source='").append(source).append('\'');
        sb.append(", target='").append(target).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
