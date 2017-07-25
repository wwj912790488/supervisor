package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Response message for {@link GetTaskProgressRequest}.
 * 
 * @author zw
 */
@XmlRootElement
public class GetTaskProgressResponse extends BaseResponse {

	/**
	 * The xml string of  {@link TranscodingInfo}
	 */
	private String xml;
	
	public String getXml() {
		return xml;
	}

	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	
	
}
