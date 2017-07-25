package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * Response for {@link GetTaskStateFromCacheRequest}.
 * 
 * @author zw
 */
@XmlRootElement
public class GetTaskStateFromCacheResponse extends BaseResponse {
	
	/** if the state is blank or null then indicate the task state is not in the cache. */
	private String state;
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	

}
