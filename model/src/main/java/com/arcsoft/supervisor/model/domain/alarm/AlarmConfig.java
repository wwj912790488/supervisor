package com.arcsoft.supervisor.model.domain.alarm;

import com.arcsoft.supervisor.model.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * the class the <tt>alarm config</tt>.
 *
 * @author jt.
 */
@Entity
@Table(name = "alarm_config")
public class AlarmConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content_detect")
    private Boolean enablecontentdetect = true;
    
    @Column(name = "content_black")
    private Boolean enableBlack = true;
    @Column(name = "content_silence")
    private Boolean enableSilence = true ;
    @Column(name = "content_no_frame")
    private Boolean enableNoFrame = true;
    @Column(name = "content_boom_sonic")
    private Boolean enableBoomSonic = true;
    @Column(name = "content_green")
    private Boolean enableGreen = true;
    @Column(name = "content_low_Volume")
    private Boolean enableLowVolume = true;
    @Column(name = "content_loud_Volume")
    private Boolean enableLoudVolume = true;
      
    @Column(name = "signal_detect")
    private Boolean enablesignaldetect = true;
    
    @Column(name = "signal_broken")
    private Boolean enableBroken = true;
    @Column(name = "signal_progid_loss")
    private Boolean enableProgidLoss = true;
    @Column(name = "signal_video_loss")
    private Boolean enableVideoLoss = true;
    @Column(name = "signal_audio_loss")
    private Boolean enableAudioLoss = true;   
    @Column(name = "signal_cc_error")
    private Boolean enableCcError = true;

	@JsonIgnore
	@OneToOne( fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alarmConfig", fetch = FetchType.LAZY)
	private List<AlarmConfigChannel> channels = new ArrayList<>();

	@JsonIgnore
	private Date lastUpdate;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Boolean getEnablecontentdetect() {
		return enablecontentdetect;
	}
	
	public void setEnablecontentdetect(Boolean enablecontentdetect) {
		this.enablecontentdetect = enablecontentdetect;
	}
	
	public Boolean getEnableBlack() {
		return enableBlack;
	}
	
	public void setEnableBlack(Boolean enableBlack) {
		this.enableBlack = enableBlack;
	}
	
	public Boolean getEnableSilence() {
		return enableSilence;
	}
	
	public void setEnableSilence(Boolean enableSilence) {
		this.enableSilence = enableSilence;
	}
	
	public Boolean getEnableNoFrame() {
		return enableNoFrame;
	}
	
	public void setEnableNoFrame(Boolean enableNoFrame) {
		this.enableNoFrame = enableNoFrame;
	}
	
	public Boolean getEnableBoomSonic() {
		return enableBoomSonic;
	}
	
	public void setEnableBoomSonic(Boolean enableBoomSonic) {
		this.enableBoomSonic = enableBoomSonic;
	}
	
	public Boolean getEnableGreen() {
		return enableGreen;
	}
	
	public void setEnableGreen(Boolean enableGreen) {
		this.enableGreen = enableGreen;
	}
	
	public Boolean getEnableLowVolume() {
		return enableLowVolume;
	}
	
	public void setEnableLowVolume(Boolean enableLowVolume) {
		this.enableLowVolume = enableLowVolume;
	}
	
	public Boolean getEnableLoudVolume() {
		return enableLoudVolume;
	}
	
	public void setEnableLoudVolume(Boolean enableLoudVolume) {
		this.enableLoudVolume = enableLoudVolume;
	}
	
	public Boolean getEnablesignaldetect() {
		return enablesignaldetect;
	}
	
	public void setEnablesignaldetect(Boolean enablesignaldetect) {
		this.enablesignaldetect = enablesignaldetect;
	}
	
	public Boolean getEnableBroken() {
		return enableBroken;
	}
	
	public void setEnableBroken(Boolean enableBroken) {
		this.enableBroken = enableBroken;
	}
	
	public Boolean getEnableProgidLoss() {
		return enableProgidLoss;
	}
	
	public void setEnableProgidLoss(Boolean enableProgidLoss) {
		this.enableProgidLoss = enableProgidLoss;
	}
	
	public Boolean getEnableVideoLoss() {
		return enableVideoLoss;
	}
	
	public void setEnableVideoLoss(Boolean enableVideoLoss) {
		this.enableVideoLoss = enableVideoLoss;
	}
	
	public Boolean getEnableAudioLoss() {
		return enableAudioLoss;
	}
	
	public void setEnableAudioLoss(Boolean enableAudioLoss) {
		this.enableAudioLoss = enableAudioLoss;
	}
	
	public Boolean getEnableCcError() {
		return enableCcError;
	}
	
	public void setEnableCcError(Boolean enableCcError) {
		this.enableCcError = enableCcError;
	}

	public List<AlarmConfigChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<AlarmConfigChannel> channels) {
		this.channels = channels;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setDefault(){
		this.enablecontentdetect = true;
		this.enableBlack = true;
		this.enableSilence = true ;
		this.enableNoFrame = true;
		this.enableBoomSonic = true;
		this.enableGreen = true;
		this.enableLowVolume = true;
		this.enableLoudVolume = true;
		this.enablesignaldetect = true;
		this.enableBroken = true;
		this.enableProgidLoss = true;
		this.enableVideoLoss = true;
		this.enableAudioLoss = true;
		this.enableCcError = true;
		//this.user;
	}
}
