package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Request the specified server to stop the specified task.
 * 
 * @author fjli
 */
@XmlRootElement
public class StopTaskRequest extends BaseRequest {

	private List<Integer> ids;

	
	public List<Integer> getIds() {
		return ids;
	}

	
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	

}
