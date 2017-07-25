package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.RemoteNodeInfo;
import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.cluster.node.NodeDescription;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This request is used to bind master and slave in 1+1 live group. This
 * message is send to master by commander.
 * 
 * @author fjli
 */
@XmlRootElement
public class GroupBindRequest extends BaseRequest {

	private RemoteNodeInfo slave;
	private boolean autoRebind = false;

	/**
	 * Default construct, for data converter.
	 */
	public GroupBindRequest() {
	}

	/**
	 * Construct bind response with error code.
	 * 
	 * @param errorCode - the error code.
	 */
	public GroupBindRequest(NodeDescription slave) {
		this.slave = new RemoteNodeInfo();
		this.slave.setId(slave.getId());
		this.slave.setIp(slave.getIp());
		this.slave.setPort(slave.getPort());
	}

	/**
	 * Returns slave node info.
	 */
	public RemoteNodeInfo getSlave() {
		return slave;
	}

	/**
	 * Set slave node info.
	 * 
	 * @param slave - the slave node info
	 */
	public void setSlave(RemoteNodeInfo slave) {
		this.slave = slave;
	}

	/**
	 * Get auto rebind flag indicating this request is auto rebind request or not.
	 */
	public boolean isAutoRebind() {
		return autoRebind;
	}

	/**
	 * Set this request is a rebind request or not.
	 * 
	 * @param autoRebind - the auto rebind flag to set
	 */
	public void setAutoRebind(boolean autoRebind) {
		this.autoRebind = autoRebind;
	}

}
