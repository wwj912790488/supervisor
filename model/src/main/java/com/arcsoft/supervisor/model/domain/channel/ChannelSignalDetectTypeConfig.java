package com.arcsoft.supervisor.model.domain.channel;

import javax.persistence.*;

/**
 * Signal detect config options for channel.
 *
 * @author jt.
 */
@Entity
@Table(name = "channel_signal_detect_type_config")
public class ChannelSignalDetectTypeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "notify_interval")
    private Integer notifyInterval = 600;
    
    @Column(name = "enable_warning_signal_broken")
    private Boolean enableWarningSignalBroken = false;
    @Column(name = "warning_signal_broken_timeout")
    private Integer warningSignalBrokenTimeout = 5000;
    
    @Column(name = "enable_warning_progid_loss")
    private Boolean enableWarningProgidLoss = false;
    @Column(name = "warning_progid_loss_timeout")
    private Integer warningProgidLossTimeout = 5000;
    
    @Column(name = "enable_warning_video_loss")
    private Boolean enableWarningVideoLoss = false;
    @Column(name = "warning_video_loss_timeout")
    private Integer warningVideoLossTimeout = 5000;
    
    @Column(name = "enable_warning_audio_loss")
    private Boolean enableWarningAudioLoss = false;
    @Column(name = "warning_audio_loss_timeout")
    private Integer warningAudioLossTimeout = 5000;
    
    @Column(name = "enable_warning_cc_error")
    private Boolean enableWarningCcError = false;
    @Column(name = "warning_cc_error_timeout")
    private Integer warningCcErrorTimeout = 5000;
    @Column(name = "warning_cc_error_count")
    private Integer warningCcErrorCount = 200;
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getNotifyInterval() {
        return notifyInterval;
    }

    public void setNotifyInterval(Integer notifyInterval) {
        this.notifyInterval = notifyInterval;
    }

    public Boolean getEnableWarningSignalBroken() {
        return enableWarningSignalBroken;
    }

    public void setEnableWarningSignalBroken(Boolean enableWarningSignalBroken) {
        this.enableWarningSignalBroken = enableWarningSignalBroken;
    }
    
    public Integer getWarningSignalBrokenTimeout() {
        return warningSignalBrokenTimeout;
    }

    public void setWarningSignalBrokenTimeout(Integer warningSignalBrokenTimeout) {
        this.warningSignalBrokenTimeout = warningSignalBrokenTimeout;
    }

    public Boolean getEnableWarningProgidLoss() {
        return enableWarningProgidLoss;
    }

    public void setEnableWarningProgidLoss(Boolean enableWarningProgidLoss) {
        this.enableWarningProgidLoss = enableWarningProgidLoss;
    }
    
    public Integer getWarningProgidLossTimeout() {
        return warningProgidLossTimeout;
    }

    public void setWarningProgidLossTimeout(Integer warningProgidLossTimeout) {
        this.warningProgidLossTimeout = warningProgidLossTimeout;
    }

    public Boolean getEnableWarningVideoLoss() {
        return enableWarningVideoLoss;
    }

    public void setEnableWarningVideoLoss(Boolean enableWarningVideoLoss) {
        this.enableWarningVideoLoss = enableWarningVideoLoss;
    }
    
    public Integer getWarningVideoLossTimeout() {
        return warningVideoLossTimeout;
    }

    public void setWarningVideoLossTimeout(Integer warningVideoLossTimeout) {
        this.warningVideoLossTimeout = warningVideoLossTimeout;
    }
    
    public Boolean getEnableWarningAudioLoss() {
        return enableWarningAudioLoss;
    }

    public void setEnableWarningAudioLoss(Boolean enableWarningAudioLoss) {
        this.enableWarningAudioLoss = enableWarningAudioLoss;
    }
    
    public Integer getWarningAudioLossTimeout() {
        return warningAudioLossTimeout;
    }

    public void setWarningAudioLossTimeout(Integer warningAudioLossTimeout) {
        this.warningAudioLossTimeout = warningAudioLossTimeout;
    }
    
    public Boolean getEnableWarningCcError() {
        return enableWarningCcError;
    }

    public void setEnableWarningCcError(Boolean enableWarningCcError) {
        this.enableWarningCcError = enableWarningCcError;
    }
    
    public Integer getWarningCcErrorTimeout() {
        return warningCcErrorTimeout;
    }

    public void setWarningCcErrorTimeout(Integer warningCcErrorTimeout) {
        this.warningCcErrorTimeout = warningCcErrorTimeout;
    }
    
    public Integer getWarningCcErrorCount() {
        return warningCcErrorCount;
    }

    public void setWarningCcErrorCount(Integer warningCcErrorCount) {
        this.warningCcErrorCount = warningCcErrorCount;
    }
}
