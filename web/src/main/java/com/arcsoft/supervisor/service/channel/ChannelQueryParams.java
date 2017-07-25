package com.arcsoft.supervisor.service.channel;

import java.util.ArrayList;
import java.util.List;

public class ChannelQueryParams {

    private String channelName;

    private List<Integer> tags = new ArrayList<>();

    private List<Integer> types = new ArrayList<>();

    private List<Integer> groups = new ArrayList<>();

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }
}
