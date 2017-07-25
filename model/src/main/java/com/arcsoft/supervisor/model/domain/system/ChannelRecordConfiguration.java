package com.arcsoft.supervisor.model.domain.system;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="configuration_channel_record")
@DiscriminatorValue("6")
public class ChannelRecordConfiguration extends Configuration {
    private String domain;
    private String supervisorStoragePath;
    private String recorderStoragePath;
    private Integer profileId;
    private Integer keepTime;
    private String contentDetectStoragePath;
    private Integer contentDetectKeepTime;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSupervisorStoragePath() {
        if(supervisorStoragePath==null)
            return "";
        return supervisorStoragePath;
    }

    public void setSupervisorStoragePath(String supervisorStoragePath) {
        this.supervisorStoragePath = supervisorStoragePath;
    }

    public String getRecorderStoragePath() {
        return recorderStoragePath;
    }

    public void setRecorderStoragePath(String recorderStoragePath) {
        this.recorderStoragePath = recorderStoragePath;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Integer getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Integer keepTime) {
        this.keepTime = keepTime;
    }

    public String getContentDetectStoragePath() {
        if(contentDetectStoragePath==null)
            return "";
        return contentDetectStoragePath;
    }

    public void setContentDetectStoragePath(String contentDetectStoragePath) {
        this.contentDetectStoragePath = contentDetectStoragePath;
    }

    public Integer getContentDetectKeepTime() {
        return contentDetectKeepTime;
    }

    public void setContentDetectKeepTime(Integer contentDetectKeepTime) {
        this.contentDetectKeepTime = contentDetectKeepTime;
    }
}
