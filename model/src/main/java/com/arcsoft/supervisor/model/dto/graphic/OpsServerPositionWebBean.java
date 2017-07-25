package com.arcsoft.supervisor.model.dto.graphic;

import java.util.ArrayList;
import java.util.List;

public class OpsServerPositionWebBean {
	private Integer wallId;
	private List<String> opsIds = new ArrayList<>();
	private List<PositionWebBean> positions = new ArrayList<PositionWebBean>();
	public Integer getWallId() {
		return wallId;
	}
	public void setWallId(Integer wallId) {
		this.wallId = wallId;
	}
	public List<String> getOpsIds() {
		return opsIds;
	}
	public void setOpsIds(List<String> opsIds) {
		this.opsIds = opsIds;
	}
	public List<PositionWebBean> getPositions() {
		return positions;
	}
	public void setPositions(List<PositionWebBean> positions) {
		this.positions = positions;
	}
	
	
}
