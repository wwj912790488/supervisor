package com.arcsoft.supervisor.model.domain.settings;

public class Component {
	private String id;
	private Integer type;
	private Integer total;
	private Integer used;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getUsed() {
		return used;
	}
	public void setUsed(Integer used) {
		this.used = used;
	}
	
	public static Component CreateMemoryComponent(Integer total, Integer used) {
		Component c = new Component();
		c.type = 0;
		c.total = total;
		c.used = used;
		c.id = "Memory";
		return c;
	}
	
	public static Component CreateCPUComponent(String id, Integer used) {
		Component c = new Component();
		c.type = 1;
		c.total = 100;
		c.used = used;
		c.id = id;
		return c;
	}
	
	public static Component CreateGPUComponent(String id, Integer used) {
		Component c = new Component();
		c.type = 2;
		c.total = 100;
		c.used = used;
		c.id = id;
		return c;
	}
	
	public static Component CreateEthComponent(String id, Integer used) {
		Component c = new Component();
		c.type = 3;
		c.total = 100;
		c.used = used;
		c.id = id;
		return c;
	}
}
