package com.arcsoft.supervisor.model.domain.graphic;

import java.util.List;

/**
 * Created by yshe on 2016/12/20.
 */
public class UserScreenLayout {
    private Integer screenid;
    private Integer width;
    private Integer height;
    private String background;
    List<UserChannelDesc> channels;

    public Integer getScreenid() {
        return screenid;
    }

    public void setScreenid(Integer screenid) {
        this.screenid = screenid;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<UserChannelDesc> getChannels() {
        return channels;
    }

    public void setChannels(List<UserChannelDesc> channels) {
        this.channels = channels;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
