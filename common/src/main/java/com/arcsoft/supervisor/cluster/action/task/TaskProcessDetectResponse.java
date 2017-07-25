package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Task process detect action response.
 * 
 * @author zw
 */
@XmlRootElement
public class TaskProcessDetectResponse extends BaseResponse {

	private Integer taskId;
	private boolean isProcessExists;

	
	public Integer getTaskId() {
		return taskId;
	}
	
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public boolean isProcessExists() {
		return isProcessExists;
	}
	
	public void setProcessExists(boolean isProcessExists) {
		this.isProcessExists = isProcessExists;
	}
	
	
}
