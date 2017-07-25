package com.arcsoft.supervisor.cluster;

import com.arcsoft.supervisor.cluster.node.NodeDescription;

/**
 * Remote node info.
 * 
 * @author fjli
 */
public class RemoteNodeInfo {

	private String id;
	private String ip;
	private int port;
	private int gpus;

	/**
	 * Default construct.
	 */
	public RemoteNodeInfo() {
	}

	/**
	 * Create RemoteNodeInfo with the specified node description.
	 * 
	 * @param desc - the specified node description
	 */
	public RemoteNodeInfo(NodeDescription desc) {
		this.id = desc.getId();
		this.ip = desc.getIp();
		this.port = desc.getPort();
		this.gpus=desc.getGpus();
	}

	/**
	 * Return the node id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the node id.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the node ip.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Set the node ip.
	 * 
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Returns the node port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set the node port.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public int getGpus() {
		return gpus;
	}

	public void setGpus(int gpus) {
		this.gpus = gpus;
	}
}
