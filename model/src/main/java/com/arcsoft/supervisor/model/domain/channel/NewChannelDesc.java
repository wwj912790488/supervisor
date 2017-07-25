package com.arcsoft.supervisor.model.domain.channel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by yshe on 2017/2/20.
 */
public class NewChannelDesc {
    @JsonProperty("id")
    private Integer channelid;
    private String name;
    @JsonProperty("address")
    private String address;
    @JsonProperty("pid")
    private String pid;
    @JsonProperty("audioId")
    private String audioId;
    @JsonProperty("sd")
    private ChannelSignalDetectTypeConfig sd;
    @JsonProperty("cd")
    private ChannelContentDetectConfig cd;

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
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
}
