package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Response message for StartTaskRequest.
 * 
 * @author fjli
 */
@XmlRootElement
public class StartTaskResponse extends BaseResponse {
	
	/**
	 * This value contains task id as key and error code as value
	 */
	private Map<Integer, Integer> errorCodeAndTaskId = null;

	public Map<Integer, Integer> getErrorCodeAndTaskId() {
		return errorCodeAndTaskId;
	}
	
	public void setErrorCodeAndTaskId(Map<Integer, Integer> errorCodeAndTaskId) {
		this.errorCodeAndTaskId = errorCodeAndTaskId;
	}

}
