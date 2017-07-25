package com.arcsoft.supervisor.model.domain.channel;

import javax.persistence.*;

/**
 * Content detect config options for channel.
 *
 * @author zw.
 */
@Entity
@Table(name = "channel_content_detect_config")
public class ChannelContentDetectConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "black_seconds")
    private Float blackSeconds = 0.0f;
    @Column(name = "silence_seconds")
    private Float silenceSeconds = 0.0f;
    @Column(name = "silence_threshold")
    private Integer silenceThreshold = -70;
    @Column(name = "no_frame_seconds")
    private Float noFrameSeconds = 0.0f;
    @Column(name = "enable_boom_sonic")
    private Boolean enableBoomSonic = false;
    @Column(name = "boomSonic_threshold")
    private Integer boomSonicThreshold = -50;
    @Column(name = "green_seconds")
    private Float greenSeconds = 0.0f;
    
    @Column(name = "lowVolume_seconds")
    private Float lowVolumeSeconds = 0.0f;
    @Column(name = "lowVolume_threshold")
    private Integer lowVolumeThreshold = -60;
    @Column(name = "loudVolume_seconds")
    private Float loudVolumeSeconds = 0.0f;
    @Column(name = "loudVolume_threshold")
    private Integer loudVolumeThreshold = -10;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getBlackSeconds() {
        return blackSeconds;
    }

    public void setBlackSeconds(Float blackSeconds) {
        this.blackSeconds = blackSeconds;
    }

    public Float getSilenceSeconds() {
        return silenceSeconds;
    }

    public void setSilenceSeconds(Float silenceSeconds) {
        this.silenceSeconds = silenceSeconds;
    }
    
    public Integer getSilenceThreshold() {
        return silenceThreshold;
    }

    public void setSilenceThreshold(Integer silenceThreshold) {
        this.silenceThreshold = silenceThreshold;
    }

    public Float getNoFrameSeconds() {
        return noFrameSeconds;
    }

    public void setNoFrameSeconds(Float noFrameSeconds) {
        this.noFrameSeconds = noFrameSeconds;
    }

    public Boolean getEnableBoomSonic() {
        return enableBoomSonic;
    }

    public void setEnableBoomSonic(Boolean enableBoomSonic) {
        this.enableBoomSonic = enableBoomSonic;
    }
    
    public Integer getBoomSonicThreshold() {
        return boomSonicThreshold;
    }

    public void setBoomSonicThreshold(Integer boomSonicThreshold) {
        this.boomSonicThreshold = boomSonicThreshold;
    }

    public Float getGreenSeconds() {
        return greenSeconds;
    }

    public void setGreenSeconds(Float greenSeconds) {
        this.greenSeconds = greenSeconds;
    }
    
    public Float getLowVolumeSeconds() {
        return lowVolumeSeconds;
    }

    public void setLowVolumeSeconds(Float lowVolumeSeconds) {
        this.lowVolumeSeconds = lowVolumeSeconds;
    }
    
    public Integer getLowVolumeThreshold() {
        return lowVolumeThreshold;
    }

    public void setLowVolumeThreshold(Integer lowVolumeThreshold) {
        this.lowVolumeThreshold = lowVolumeThreshold;
    }
    
    public Float getLoudVolumeSeconds() {
        return loudVolumeSeconds;
    }

    public void setLoudVolumeSeconds(Float loudVolumeSeconds) {
        this.loudVolumeSeconds = loudVolumeSeconds;
    }
    
    public Integer getLoudVolumeThreshold() {
        return loudVolumeThreshold;
    }

    public void setLoudVolumeThreshold(Integer loudVolumeThreshold) {
        this.loudVolumeThreshold = loudVolumeThreshold;
    }
}
