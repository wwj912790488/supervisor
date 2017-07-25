package com.arcsoft.supervisor.web.mosic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yshe on 2016/6/16.
 */
public class MosicScreenBean {
    private String token;
    private Integer screenid;
    private Integer channelcount;
    private Integer width;
    private Integer height;
    private String foreground;
    @JsonProperty("Channels")
    List<MosicChannelBean>  Channels;
    private Boolean flag;

    public MosicScreenBean()
    {
        this.flag = false;
    }

    public String getToken(){return token;}
    public void setToken(String token){this.token=token;}

    public Integer getScreenid(){return screenid;}
    public void setScreenid(Integer screenid){this.screenid=screenid;}

    public Integer getChannelcount(){return channelcount;}
    public void   setChannelcount(Integer channelcount){this.channelcount=channelcount;}

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

    public String getForeground() {
        return foreground;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public List<MosicChannelBean> getChannels() {
        return Channels;
    }

    public void setChannels(List<MosicChannelBean> Channels) {
        this.Channels = Channels;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @JsonIgnore
    public void sortChannels(){
        if(CollectionUtils.isEmpty(Channels))
            return;
        if(Channels.get(0).getPosition()==null)
            return;
        try{
            Collections.sort(Channels, new Comparator<MosicChannelBean>() {
                @Override
                public int compare(MosicChannelBean o1, MosicChannelBean o2) {
                    MosicChannelBean channel1= (MosicChannelBean)o1;
                    MosicChannelBean channel2= (MosicChannelBean)o2;
                    int result = 0;
                    if(channel1.getPosition().getX() >channel2.getPosition().getX()){
                        return 1;
                    }else {
                        if(channel1.getPosition().getY() >channel2.getPosition().getY())
                            return 1;
                    }

                    return 0;
                }
            });
        }catch (Exception e){
        }
    }

}
