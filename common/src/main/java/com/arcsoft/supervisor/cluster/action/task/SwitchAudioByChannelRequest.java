package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Switch task audio by channel.
 * 
 * @author jt
 */
@XmlRootElement
public class SwitchAudioByChannelRequest extends BaseRequest {
	
	private Integer taskId;
	
	private Integer videoSettingId;

	private Integer channelId;
	
	public Integer getTaskId() {
		return taskId;
	}
	
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
	public Integer getVideoSettingId() {
		return videoSettingId;
	}
	
	public void setVideoSettingId(Integer videoSettingId) {
		this.videoSettingId = videoSettingId;
	}

	public Integer getChannelId() {
		return channelId;
	}
	
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
	
}
