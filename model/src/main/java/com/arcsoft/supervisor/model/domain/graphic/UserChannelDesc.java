package com.arcsoft.supervisor.model.domain.graphic;

import com.arcsoft.supervisor.model.domain.channel.ChannelContentDetectConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelSignalDetectTypeConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;

/**
 * Created by yshe on 2016/12/20.
 */
public class UserChannelDesc {
    @JsonProperty("pos")
    private UserChannelPos position;//for relative position

    @JsonProperty("channelid")
    private Integer channelid;

    @JsonProperty("name")
    private String channelname;

    @JsonProperty("sd")
    private ChannelSignalDetectTypeConfig sd;

    @JsonProperty("cd")
    private ChannelContentDetectConfig cd;

    public UserChannelPos getPosition() {
        return position;
    }

    public void setPosition(UserChannelPos position) {
        this.position = position;
    }

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public ChannelSignalDetectTypeConfig getSd() {
        return sd;
    }

    public void setSd(ChannelSignalDetectTypeConfig sd) {
        this.sd = sd;
    }

    public ChannelContentDetectConfig getCd() {
        return cd;
    }

    public void setCd(ChannelContentDetectConfig cd) {
        this.cd = cd;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }
}
