package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScreenPositionWebBean {
	private Integer schemaId;
	private Integer row;
	private Integer column;
	private Integer group;
	@JsonProperty("channel_id")
	private Integer channelId;
	@JsonProperty("channel_name")
	private String channelName;
	private Integer x;
	private Integer y;
	
	public ScreenPositionWebBean() {}
	
	public ScreenPositionWebBean(ScreenPosition screenPosition) {
		this.schemaId = screenPosition.getScreenSchema().getId();
		this.row = screenPosition.getRow();
		this.column = screenPosition.getColumn();
		this.group = screenPosition.getGroupIndex();
		this.x = screenPosition.getX();
		this.y = screenPosition.getY();
		Channel channel = screenPosition.getChannel();
		if(channel != null) {
			this.channelId = channel.getId();
			this.channelName = channel.getName();
		} else {
			this.channelId = -1;
			this.channelName = "";
		}
	}
	
	public Integer getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Integer schemaId) {
		this.schemaId = schemaId;
	}
	
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}
}
