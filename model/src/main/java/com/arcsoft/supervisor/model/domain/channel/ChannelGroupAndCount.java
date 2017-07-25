package com.arcsoft.supervisor.model.domain.channel;

/**
 * Created by wwj on 2017/7/4.
 */
public class ChannelGroupAndCount {
    ChannelGroup channelGroup;
    Integer count;

    public ChannelGroupAndCount(ChannelGroup channelGroup, Integer count) {
        this.channelGroup = channelGroup;
        this.count = count;
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
