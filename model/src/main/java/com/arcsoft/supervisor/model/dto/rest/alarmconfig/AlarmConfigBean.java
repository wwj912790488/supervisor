package com.arcsoft.supervisor.model.dto.rest.alarmconfig;


import java.util.List;

public class AlarmConfigBean {
	private Integer id;
	private List<AlarmConfigChannelBean> channels;

	private Boolean enablecontentdetect = true;
	private Boolean enableBlack = true;
	private Boolean enableSilence = true ;
	private Boolean enableNoFrame = true;
	private Boolean enableBoomSonic = true;
	private Boolean enableGreen = true;
	private Boolean enableLowVolume = true;
	private Boolean enableLoudVolume = true;

	private Boolean enablesignaldetect = true;
	private Boolean enableBroken = true;
	private Boolean enableProgidLoss = true;
	private Boolean enableVideoLoss = true;
	private Boolean enableAudioLoss = true;
	private Boolean enableCcError = true;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public List<AlarmConfigChannelBean> getChannels() {
		return channels;
	}
	public void setChannels(List<AlarmConfigChannelBean> channels) {
		this.channels = channels;
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
		
}
