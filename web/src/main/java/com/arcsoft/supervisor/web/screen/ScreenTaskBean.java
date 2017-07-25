package com.arcsoft.supervisor.web.screen;

import com.arcsoft.supervisor.model.domain.channel.ChannelConfig;

import java.util.List;

/**
 * Created by yshe on 2017/2/15.
 */
public class ScreenTaskBean {
    private String token;
    private Integer screenid;
    private Integer channelcount;
    private Integer width;
    private Integer height;
    private String foreground;
    List<ChannelConfig> channels;

    public String getForeground() {
        return foreground;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getScreenid() {
        return screenid;
    }

    public void setScreenid(Integer screenid) {
        this.screenid = screenid;
    }

    public Integer getChannelcount() {
        return channelcount;
    }

    public void setChannelcount(Integer channelcount) {
        this.channelcount = channelcount;
    }

    public List<ChannelConfig> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelConfig> channels) {
        this.channels = channels;
    }
}
