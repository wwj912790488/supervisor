package com.arcsoft.supervisor.model.dto.channel;

import com.arcsoft.supervisor.model.domain.channel.ChannelContentDetectConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelMobileConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelSignalDetectTypeConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelTag;

import java.util.ArrayList;
import java.util.List;

public class EditChannelForm {
    private Boolean isSupportMobile = false;
    private Boolean enableSignalDetectByType = false;
    private Boolean enableContentDetect = false;
    private Boolean enableRecord = false;
    private Boolean enableTriggerRecord = false;
    private List<ChannelTag> tags;
    private List<ChannelMobileConfig> mobileConfigs = new ArrayList<>();
    private ChannelSignalDetectTypeConfig signalDetectByTypeConfig;
    private ChannelContentDetectConfig contentDetectConfig;
    private List<Integer> channelIds;

    public Boolean getIsSupportMobile() {
        return isSupportMobile;
    }

    public void setIsSupportMobile(Boolean isSupportMobile) {
        this.isSupportMobile = isSupportMobile;
    }

    public Boolean getEnableSignalDetectByType() {
        return enableSignalDetectByType;
    }

    public void setEnableSignalDetectByType(Boolean enableSignalDetectByType) {
        this.enableSignalDetectByType = enableSignalDetectByType;
    }

    public Boolean getEnableContentDetect() {
        return enableContentDetect;
    }

    public void setEnableContentDetect(Boolean enableContentDetect) {
        this.enableContentDetect = enableContentDetect;
    }

    public Boolean getEnableRecord() {
        return enableRecord;
    }

    public void setEnableRecord(Boolean enableRecord) {
        this.enableRecord = enableRecord;
    }

    public Boolean getEnableTriggerRecord() {
        return enableTriggerRecord;
    }

    public void setEnableTriggerRecord(Boolean enableTriggerRecord) {
        this.enableTriggerRecord = enableTriggerRecord;
    }

    public List<ChannelTag> getTags() {
        return tags;
    }

    public void setTags(List<ChannelTag> tags) {
        this.tags = tags;
    }

    public List<ChannelMobileConfig> getMobileConfigs() {
        return mobileConfigs;
    }

    public void setMobileConfigs(List<ChannelMobileConfig> mobileConfigs) {
        this.mobileConfigs = mobileConfigs;
    }

    public ChannelSignalDetectTypeConfig getSignalDetectByTypeConfig() {
        return signalDetectByTypeConfig;
    }

    public void setSignalDetectByTypeConfig(ChannelSignalDetectTypeConfig signalDetectByTypeConfig) {
        this.signalDetectByTypeConfig = signalDetectByTypeConfig;
    }

    public ChannelContentDetectConfig getContentDetectConfig() {
        return contentDetectConfig;
    }

    public void setContentDetectConfig(ChannelContentDetectConfig contentDetectConfig) {
        this.contentDetectConfig = contentDetectConfig;
    }

    public List<Integer> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Integer> channelIds) {
        this.channelIds = channelIds;
    }
}
