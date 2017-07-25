package com.arcsoft.supervisor.model.dto.alert;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.vo.task.MediaCheckType;

public class AlertMessage {
	private Long id;
	private String title;
	private String message;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setMessageFrom(ContentDetectLog log) {
		this.title = "内容检测告警";
		this.message = String.format("频道：%s 出现%s", log.getChannelName(), contentDetectType(log.getType()));
	}
	
	private String contentDetectType(int type) {
		switch(type) {
		case MediaCheckType.CHECK_TYPE_BLACK_FIELD_INDEX:
			return "黑帧";
		case MediaCheckType.CHECK_TYPE_GREEN_FIELD_INDEX:
			return "绿帧";
		case MediaCheckType.CHECK_TYPE_STATIC_FRAME_INDEX:
			return "静帧";
		case MediaCheckType.CHECK_TYPE_COLOR_BAR_INDEX:
			return "";
		case MediaCheckType.CHECK_TYPE_MOSAIC_INDEX:
			return "马赛克";
		case MediaCheckType.CHECK_TYPE_STREAM_INTERRUPT_INDEX:
			return "流中断";
		case MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX:
			return "静音";
		case MediaCheckType.CHECK_TYPE_VOLUME_LOW_INDEX:
			return "低音";
		case MediaCheckType.CHECK_TYPE_VOLUME_LOUD_INDEX:
			return "高音";
		case MediaCheckType.CHECK_TYPE_VOLUME_LOUDN_INDEX:
			return "";
		case MediaCheckType.CHECK_TYPE_TONE_INDEX:
			return "";
		case MediaCheckType.CHECK_TYPE_BREAK_INDEX:
			return "爆音";
		case MediaCheckType.SIGNAL_STREAM_CCERROR:
			return "CC错误";
		case MediaCheckType.SIGNAL_STREAM_NOAUDIO:
			return "Audio丢失";
		case MediaCheckType.SIGNAL_STREAM_NOVIDEO:
			return "Video丢失";
		case MediaCheckType.SIGNAL_STREAM_INTERRUPT:
			return "信源中断";
		default:
			return "未知错误";
		}
	}
	
}
