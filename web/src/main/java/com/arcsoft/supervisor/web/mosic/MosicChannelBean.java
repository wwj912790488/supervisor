package com.arcsoft.supervisor.web.mosic;

import com.arcsoft.supervisor.model.domain.channel.ChannelContentDetectConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelSignalDetectTypeConfig;
import com.arcsoft.supervisor.model.domain.graphic.UserChannelPos;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;

/**
 * Created by yshe on 2016/6/16.
 */
public class MosicChannelBean {

    @JsonProperty("posIdx")
    private Integer pos;//for position index

    @JsonProperty("pos")
    private UserChannelPos position;//for relative position

    @JsonProperty("channelid")
    private String channelid;

    @JsonProperty("sd")
    private ChannelSignalDetectTypeConfig sd;

    @JsonProperty("cd")
    private ChannelContentDetectConfig cd;

    @JsonProperty("heart")
    private String heart;

    public MosicChannelBean()
    {

    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public UserChannelPos getPosition() {
        return position;
    }

    public void setPosition(UserChannelPos position) {
        this.position = position;
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

    public String getChannelid(){return channelid;}
    public void setChannelid(String channelid){this.channelid=channelid;}

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }
}
