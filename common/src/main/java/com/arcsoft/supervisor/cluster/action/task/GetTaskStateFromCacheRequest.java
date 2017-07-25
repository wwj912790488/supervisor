package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Gets task state from cache.
 * 
 * @author zw
 */
@XmlRootElement
public class GetTaskStateFromCacheRequest extends BaseRequest {
	
	private Integer taskId;
	
	public Integer getTaskId() {
		return taskId;
	}
	
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
}
