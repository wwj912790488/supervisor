package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Request the specified server to get the task thumbnail of specified task.
 * 
 * @author zw
 */
@XmlRootElement
public class GetTaskThumbnailRequest extends BaseRequest {

    /**
     * The identify value of task.
     */
	private Integer id;
	
	private Integer width;

	
	public Integer getId() {
		return id;
	}

	
	public void setId(Integer id) {
		this.id = id;
	}

	
	public Integer getWidth() {
		return width;
	}

	
	public void setWidth(Integer width) {
		this.width = width;
	}
}
