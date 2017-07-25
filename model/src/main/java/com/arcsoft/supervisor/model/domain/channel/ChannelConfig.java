package com.arcsoft.supervisor.model.domain.channel;

import com.arcsoft.supervisor.model.domain.graphic.UserChannelPos;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by yshe on 2017/2/20.
 */
public class ChannelConfig {
    @JsonProperty("posIdx")
    private Integer posIdx;
    @JsonProperty("pos")
    private UserChannelPos position;
    @JsonProperty("channel")
    private NewChannelDesc  channel;

    public Integer getPosIdx() {
        return posIdx;
    }

    public void setPosIdx(Integer posIdx) {
        this.posIdx = posIdx;
    }

    public UserChannelPos getPosition() {
        return position;
    }

    public void setPosition(UserChannelPos position) {
        this.position = position;
    }

    public NewChannelDesc getChannel() {
        return channel;
    }

    public void setChannel(NewChannelDesc channel) {
        this.channel = channel;
    }
}
