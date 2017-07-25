package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Send a md5sum complete action from agent to commander.
 * 
 * @author zw
 */
@XmlRootElement
public class Md5sumCompleteRequest extends BaseRequest {
	
	private String serverId;
	
	public String getServerId() {
		return serverId;
	}
	
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
}
