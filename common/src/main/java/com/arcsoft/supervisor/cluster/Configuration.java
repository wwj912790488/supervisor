package com.arcsoft.supervisor.cluster;

import com.arcsoft.supervisor.cluster.service.HeartBeatSenderListener;

/**
 * The configuration for creating new cluster server.
 * 
 * @author fjli
 */
public abstract class Configuration {

	private int clusterType;
	private String clusterIp;
	private int clusterPort;
	private String bindAddr;
	private int timeToLive;
	private int serverType;
	private String serverId;
	private String serverName;
	private int serverPort;
    /** The comma separation function of agent.*/
    private String functions;

	/**
	 * A listener for {@code HeartBeatSender} of local node
	 */
	private HeartBeatSenderListener heartBeatSenderListener;

    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
    }

	/**
	 * Returns cluster type.
	 */
	public int getClusterType() {
		return clusterType;
	}

	/**
	 * Set cluster type.
	 * 
	 * @param clusterType
	 * @see ClusterType#CORE
	 * @see ClusterType#LIVE
	 */
	public void setClusterType(int clusterType) {
		this.clusterType = clusterType;
	}

	/**
	 * Returns cluster ip.
	 */
	public String getClusterIp() {
		return clusterIp;
	}

	/**
	 * Set cluster ip.
	 * 
	 * @param clusterIp - ip address
	 */
	public void setClusterIp(String clusterIp) {
		this.clusterIp = clusterIp;
	}

	/**
	 * Returns the cluster port.
	 */
	public int getClusterPort() {
		return clusterPort;
	}

	/**
	 * Set the cluster port.
	 * 
	 * @param clusterPort
	 */
	public void setClusterPort(int clusterPort) {
		this.clusterPort = clusterPort;
	}

	/**
	 * Returns the bind address.
	 */
	public String getBindAddr() {
		return bindAddr;
	}

	/**
	 * Set the bind address.
	 * 
	 * @param bindAddr
	 */
	public void setBindAddr(String bindAddr) {
		this.bindAddr = bindAddr;
	}

	/**
	 * Returns the time-to-live of the cluster.
	 */
	public Integer getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Set the time-to-live for the cluster.
	 * 
	 * @param timeToLive - the timeToLive to be set
	 */
	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	/**
	 * Returns server type.
	 */
	public int getServerType() {
		return serverType;
	}

	/**
	 * Set server type.
	 * 
	 * @param serverType
	 */
	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	/**
	 * Returns server id.
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * Set the server id.
	 * 
	 * @param serverId
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * Returns the server name.
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Set the server name.
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * Returns the server port.
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Set the server port.
	 * 
	 * @param serverPort
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

    public HeartBeatSenderListener getHeartBeatSenderListener() {
        return heartBeatSenderListener;
    }

    public void setHeartBeatSenderListener(HeartBeatSenderListener heartBeatSenderListener) {
        this.heartBeatSenderListener = heartBeatSenderListener;
    }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("cluster.type=").append(getClusterType());
		b.append(", cluster.ip=").append(getClusterIp());
		b.append(", cluster.port=").append(getClusterPort());
		b.append(", cluster.bindAddr=").append(getBindAddr());
		b.append(", server.id=").append(getServerId());
		b.append(", server.type=").append(getServerType());
		b.append(", server.name=").append(getServerName());
		b.append(", server.port=").append(getServerPort());
		return b.toString();
	}

}
