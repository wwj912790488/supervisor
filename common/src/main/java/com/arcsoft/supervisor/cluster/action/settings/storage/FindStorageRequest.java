package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This request used to find all storage which is local
 * 
 * @author hxiang
 */
@XmlRootElement
public class FindStorageRequest extends BaseRequest {
	public enum SearchType{ LOCAL, REMOETE, ALL };
	private Integer id;
	private String name;
	private SearchType type = SearchType.ALL;
	
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
	
	public SearchType getType() {
		return type;
	}
	
	public void setType(SearchType type) {
		this.type = type;
	}
}
