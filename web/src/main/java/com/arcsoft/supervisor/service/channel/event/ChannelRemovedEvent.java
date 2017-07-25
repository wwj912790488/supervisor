package com.arcsoft.supervisor.service.channel.event;

import java.util.EventObject;

public class ChannelRemovedEvent extends EventObject {
    private Integer channelId;
    /**
     * Constructs a prototypical Event.
     *
     * @param channelId The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ChannelRemovedEvent(Integer channelId) {
        super(channelId);
        this.channelId = channelId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
}
