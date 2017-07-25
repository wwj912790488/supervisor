package com.arcsoft.supervisor.model.vo.task.cd;

import java.util.ArrayList;
import java.util.List;

public class ScreenContentDetectConfig {
	public static final String DETECT_TIME_KEY = "Time";
	public static final String BREAK_DETECT_DEFAULT_TIME_VALUE = "200";
	public static final String DETECT_AREA_KEY = "Area";
	public static final String DETECT_DEFAULT_AREA_VALUE = "99.55";
	public static final String DETECT_DB_KEY = "db";
	public static final String DETECT_DBMIN_KEY = "dbmin";
	public static final String DETECT_DBDIFF_KEY = "dbdiff";
	
	private Integer index;
	private Integer channelId;
	private Boolean isAlive;
	private List<ContentDetectParam> detectSettings = new ArrayList<>();
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Integer getChannelId() {
		return channelId;
	}
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
	public Boolean getIsAlive() {
		return isAlive;
	}
	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}
	public List<ContentDetectParam> getDetectSettings() {
		return detectSettings;
	}
	public void setDetectSettings(List<ContentDetectParam> detectSettings) {
		this.detectSettings = detectSettings;
	}
	
	
	
}
