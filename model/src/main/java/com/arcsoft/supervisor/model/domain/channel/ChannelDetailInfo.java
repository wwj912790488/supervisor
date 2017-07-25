package com.arcsoft.supervisor.model.domain.channel;

import java.util.HashSet;

/**
 * Created by yshe on 2016/5/5.
 */
public class ChannelDetailInfo {

    private ChannelInfo info;
    private String  source;
    private String  output;
    private HashSet screenInfo;

    public ChannelDetailInfo(ChannelInfo info, String output, HashSet screenInfo, String source) {
        this.info = info;
        this.output = output;
        this.screenInfo = screenInfo;
        this.source = source;
    }

    public void setChannelInfo(ChannelInfo info){this.info=info;}
    public ChannelInfo getChannelInfo(){return info;}

    public void setSource(String source){this.source=source;}
    public String getSource(){return source;}

    public void setOutput(String output){this.output=output;}
    public String getOutput(){return output;}

    public HashSet getScreenInfo() {
        return screenInfo;
    }

    public void setScreenInfo(HashSet screenInfo) {
        this.screenInfo = screenInfo;
    }
}
