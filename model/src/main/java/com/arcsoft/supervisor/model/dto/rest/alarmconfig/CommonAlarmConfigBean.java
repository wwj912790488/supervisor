package com.arcsoft.supervisor.model.dto.rest.alarmconfig;


import java.util.List;

public class CommonAlarmConfigBean {

	private List<AlarmConfigChannelBean> channels;

	private Boolean contentdetect = true;
	private Boolean black = true;
	private Boolean silence = true ;
	private Boolean noframe = true;
	private Boolean boomsonic = true;
	private Boolean green = true;
	private Boolean lowvolume = true;
	private Boolean loudvolume = true;

	private Boolean signaldetect = true;
	private Boolean broken = true;
	private Boolean progidloss = true;
	private Boolean videoloss = true;
	private Boolean audioloss = true;
	private Boolean ccerror = true;

	public List<AlarmConfigChannelBean> getChannels() {
		return channels;
	}
	public void setChannels(List<AlarmConfigChannelBean> channels) {
		this.channels = channels;
	}

	public Boolean getEnablecontentdetect() {
		return contentdetect;
	}

	public void setEnablecontentdetect(Boolean contentdetect) {
		this.contentdetect = contentdetect;
	}

	public Boolean getEnableBlack() {
		return black;
	}

	public void setEnableBlack(Boolean black) {
		this.black = black;
	}

	public Boolean getEnableSilence() {
		return silence;
	}

	public void setEnableSilence(Boolean silence) {
		this.silence = silence;
	}

	public Boolean getEnableNoFrame() {
		return noframe;
	}

	public void setEnableNoFrame(Boolean noframe) {
		this.noframe = noframe;
	}

	public Boolean getEnableBoomSonic() {
		return boomsonic;
	}

	public void setEnableBoomSonic(Boolean boomsonic) {
		this.boomsonic = boomsonic;
	}

	public Boolean getEnableGreen() {
		return green;
	}

	public void setEnableGreen(Boolean green) {
		this.green = green;
	}

	public Boolean getEnableLowVolume() {
		return lowvolume;
	}

	public void setEnableLowVolume(Boolean lowvolume) {
		this.lowvolume = lowvolume;
	}

	public Boolean getEnableLoudVolume() {
		return loudvolume;
	}

	public void setEnableLoudVolume(Boolean loudvolume) {
		this.loudvolume = loudvolume;
	}

	public Boolean getEnablesignaldetect() {
		return signaldetect;
	}

	public void setEnablesignaldetect(Boolean signaldetect) {
		this.signaldetect = signaldetect;
	}

	public Boolean getEnableBroken() {
		return broken;
	}

	public void setEnableBroken(Boolean broken) {
		this.broken = broken;
	}

	public Boolean getEnableProgidLoss() {
		return progidloss;
	}

	public void setEnableProgidLoss(Boolean progidloss) {
		this.progidloss = progidloss;
	}

	public Boolean getEnableVideoLoss() {
		return videoloss;
	}

	public void setEnableVideoLoss(Boolean videoloss) {
		this.videoloss = videoloss;
	}

	public Boolean getEnableAudioLoss() {
		return audioloss;
	}

	public void setEnableAudioLoss(Boolean audioloss) {
		this.audioloss = audioloss;
	}

	public Boolean getEnableCcError() {
		return ccerror;
	}

	public void setEnableCcError(Boolean ccerror) {
		this.ccerror = ccerror;
	}
}
