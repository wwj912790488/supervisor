package com.arcsoft.supervisor.maven.plugin.build;

import java.util.List;

public class Profile {

    private String id;

    private List<Include> includes;

    private List<Replace> replaces;

    public String getId() {
        return id;
    }

    public List<Include> getIncludes() {
        return includes;
    }

    public List<Replace> getReplaces() {
        return replaces;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("com.arcsoft.supervisor.maven.plugin.build.Profile{");
        sb.append("id='").append(id).append('\'');
        sb.append(", includes=").append(includes);
        sb.append(", replaces=").append(replaces);
        sb.append('}');
        return sb.toString();
    }
}
