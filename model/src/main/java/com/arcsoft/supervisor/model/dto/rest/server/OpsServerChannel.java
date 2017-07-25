package com.arcsoft.supervisor.model.dto.rest.server;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The class used for serialize or de-serialize object for ops api.
 *
 * @author zw.
 */
public class OpsServerChannel {

    private String id;
    @JsonProperty("ch_id")
    private String channelId;
    @JsonProperty("ch_name")
    private String channelName;
    private String url;
    private String rect;
    private String source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRect() {
        return rect;
    }

    public void setRect(String rect) {
        this.rect = rect;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public static <T extends AbstractOpsServer> OpsServerChannel build(T server, String url){
        OpsServerChannel opsServerChannel = new OpsServerChannel();
        opsServerChannel.setId(server.getId());
        opsServerChannel.setChannelName("");
        opsServerChannel.setChannelId("");
        opsServerChannel.setUrl(url);
        opsServerChannel.setRect("0,0,1,1");
        return opsServerChannel;
    }

    public static <T extends AbstractOpsServer> OpsServerChannel build(T server, String url, String source) {
        OpsServerChannel opsServerChannel = new OpsServerChannel();
        opsServerChannel.setId(server.getId());
        opsServerChannel.setChannelName("");
        opsServerChannel.setChannelId("");
        opsServerChannel.setUrl(url);
        opsServerChannel.setRect("0,0,1,1");
        opsServerChannel.setSource(source);
        return opsServerChannel;
    }

    public static OpsServerChannel build(String id, String ip, String port, String url){
        OpsServerChannel opsServerChannel = new OpsServerChannel();
        opsServerChannel.setId(id);
        opsServerChannel.setChannelName("");
        opsServerChannel.setChannelId("");
        opsServerChannel.setUrl(url);
        opsServerChannel.setRect("0,0,1,1");
        return opsServerChannel;
    }
}
