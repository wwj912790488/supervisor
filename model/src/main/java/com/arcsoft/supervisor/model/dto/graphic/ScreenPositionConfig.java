package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.dto.channel.SignalDetectSetting;

/**
 * @author zw.
 */
public class ScreenPositionConfig {

    private Integer index;
    private String url;
    private String programId;
    private String audioId;
    private Integer row;
    private Integer column;
    private Integer x;
    private Integer y;
    private Integer group;
    private String channelName;
    private SignalDetectSetting signalDetectSetting;
    private boolean isPlaceHolder;
    private String originalId;
    private String apiHeart;
    private String protocol;
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getApiHeart() {
        return apiHeart;
    }

    public void setApiHeart(String apiHeart) {
        this.apiHeart = apiHeart;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public SignalDetectSetting getSignalDetectSetting() {
        return signalDetectSetting;
    }

    public void setSignalDetectSetting(SignalDetectSetting signalDetectSetting) {
        this.signalDetectSetting = signalDetectSetting;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public boolean getIsPlaceHolder() {
        return isPlaceHolder;
    }

    public void setIsPlaceHolder(boolean isPlaceHolder) {
        this.isPlaceHolder = isPlaceHolder;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getOriginalId() {
        return originalId;
    }

    public ScreenPositionConfig(boolean isPlaceHolder) {
        this.isPlaceHolder = isPlaceHolder;
    }

    /**
     * Creates a new config for place holder.
     *
     * @param row    the left
     * @param column the top
     * @param x      the width
     * @param y      the height
     * @param index  the index
     * @return a new place holder config
     */
    public static ScreenPositionConfig placeHolderConfig(int row, int column, int x, int y, int group, int index) {
        ScreenPositionConfig placeHolderConfig = new ScreenPositionConfig(true);
        placeHolderConfig.setIndex(index);
        //An un-reachable url for place holder
        placeHolderConfig.setUrl("udp://127.0.0.2:9999");
        placeHolderConfig.setProgramId("-1");
        placeHolderConfig.setAudioId("-1");
        placeHolderConfig.setChannelName("");
        placeHolderConfig.setSignalDetectSetting(null);
        placeHolderConfig.setRow(row);
        placeHolderConfig.setColumn(column);
        placeHolderConfig.setX(x);
        placeHolderConfig.setY(y);
        placeHolderConfig.setGroup(group);
        placeHolderConfig.setOriginalId("-1");
        return placeHolderConfig;
    }

    public static ScreenPositionConfig from(Channel channel, int index, int row, int column, int x, int y, int group) {
        ScreenPositionConfig config = new ScreenPositionConfig(false);
        config.setUrl(channel.getAddress());
        config.setProtocol(channel.getProtocol());
        config.setPort(channel.getPort());
        config.setIndex(index);
        config.setRow(row);
        config.setColumn(column);
        config.setX(x);
        config.setY(y);
        config.setChannelName(channel.getName());
        config.setProgramId(channel.getProgramId());
        config.setAudioId(channel.getAudioId());
        config.setSignalDetectSetting(
                SignalDetectSetting.builder()
                        .channel(channel).build()
        );
        config.setOriginalId(channel.getOrigchannelid());
        config.setApiHeart(channel.getApiHeart());
        config.setGroup(group);
        return config;
    }
}
