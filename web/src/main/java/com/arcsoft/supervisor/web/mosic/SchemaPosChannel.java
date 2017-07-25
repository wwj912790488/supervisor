package com.arcsoft.supervisor.web.mosic;

import com.arcsoft.supervisor.model.domain.channel.Channel;

/**
 * Created by yshe on 2016/6/17.
 */
public class SchemaPosChannel {

    private Integer pos;
    private Channel channel;

    public SchemaPosChannel(Integer pos,Channel channel)
    {
        this.pos = pos;
        this.channel = channel;
    }

    public Integer getPos(){return pos;}
    public void setPos(Integer pos){this.pos = pos;}

    public Channel getChannel(){return channel;}
    public void setChannel(Channel channel){this.channel=channel;}
}
