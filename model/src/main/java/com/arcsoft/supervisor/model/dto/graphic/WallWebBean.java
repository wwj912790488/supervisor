package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.graphic.Wall;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;

import java.util.ArrayList;
import java.util.List;

public class WallWebBean {
	private Integer id;
	private Integer version;
	private String name;
	private Byte type;
	private Integer rowCount;
	private Integer columnCount;
	private List<String> opsIds = new ArrayList<>();
	private List<Integer> sdiIds = new ArrayList<>();
	
	public WallWebBean() {}
	
	public WallWebBean(Wall wall) {
		this.id = wall.getId();
		this.version = wall.getVersion();
		this.name = wall.getName();
		this.type = wall.getType();
		this.rowCount = wall.getRowCount();
		this.columnCount = wall.getColumnCount();
		if(this.type == 1) {
			for(int i = 0; i < wall.getWallPositions().size(); i++) {
				OpsServer ops = wall.getWallPositions().get(i).getOpsServer();
				if(ops != null) { 
					opsIds.add(ops.getId());
				}
			}
		} else if (this.type == 2) {
			for(int i = 0; i < wall.getWallPositions().size(); i++) {
				ServerComponent sdi = wall.getWallPositions().get(i).getSdiOutput();
				if(sdi != null) {
					sdiIds.add(sdi.getId());
				}
			}
		}
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public Integer getRowCount() {
		return rowCount;
	}
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	public Integer getColumnCount() {
		return columnCount;
	}
	public void setColumnCount(Integer columnCount) {
		this.columnCount = columnCount;
	}

	public List<String> getOpsIds() {
		return opsIds;
	}

	public void setOpsIds(List<String> opsIds) {
		this.opsIds = opsIds;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<Integer> getSdiIds() {
		return sdiIds;
	}

	public void setSdiIds(List<Integer> sdiIds) {
		this.sdiIds = sdiIds;
	}
	
	
}
