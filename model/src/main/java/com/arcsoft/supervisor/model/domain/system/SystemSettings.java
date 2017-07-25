package com.arcsoft.supervisor.model.domain.system;

import com.arcsoft.supervisor.cluster.ClusterType;

/**
 * Settings for system initialization.
 * 
 * @author fjli
 */
public class SystemSettings {

	/**
	 * Settings key for cluster type.
	 */
	public static final String CLUSTER_TYPE = "cluster.type";

	/**
	 * Settings key for cluster ip.
	 */
	public static final String CLUSTER_IP = "cluster.ip";

	/**
	 * Settings key for cluster port.
	 */
	public static final String CLUSTER_PORT = "cluster.port";

	/**
	 * Settings for cluster bind address.
	 */
	public static final String CLUSTER_BINDADDR = "cluster.bindAddr";

	/**
	 * Settings for cluster time-to-live.
	 */
	public static final String CLUSTER_TIME_TO_LIVE = "cluster.ttl";

	/**
	 * Settings for cluster heart beat interval.
	 */
	public static final String CLUSTER_HEARTBEAT_INTERVAL = "cluster.heartbeat.interval";

	/**
	 * Settings for cluster heart beat timeout.
	 */
	public static final String CLUSTER_HEARTBEAT_TIMEOUT = "cluster.heartbeat.timeout";

	public static final String ALERT_AUTO_DELETE_DAYS="alert.auto-delete.days";

	private String clusterIp;
	private Integer clusterPort;
	private String bindAddr;
	private Integer clusterType = ClusterType.CORE; //Default set to CORE
	private Integer heartbeatInterval;
	private Integer heartbeatTimeout;
	private Integer timeToLive;
	private Integer alertAutoDeleteDays=0;

	/**
	 * Returns cluster IP.
	 */
	public String getClusterIp() {
		return clusterIp;
	}

	/**
	 * Set cluster IP.
	 * 
	 * @param clusterIp - the cluster ip
	 */
	public void setClusterIp(String clusterIp) {
		this.clusterIp = clusterIp;
	}

	/**
	 * Returns the cluster port.
	 */
	public Integer getClusterPort() {
		return clusterPort;
	}

	/**
	 * Set the cluster port.
	 * 
	 * @param clusterPort - the cluster port
	 */
	public void setClusterPort(Integer clusterPort) {
		this.clusterPort = clusterPort;
	}

	/**
	 * Returns the cluster bind address.
	 */
	public String getBindAddr() {
		return bindAddr;
	}

	/**
	 * Set the cluster bind address.
	 * 
	 * @param bindAddr - the bind address
	 */
	public void setBindAddr(String bindAddr) {
		this.bindAddr = bindAddr;
	}

	/**
	 * Returns cluster type.
	 * 
	 * @see ClusterType#LIVE
	 * @see ClusterType#CORE
	 */
	public Integer getClusterType() {
		return clusterType;
	}

	/**
	 * Set the cluster type.
	 * 
	 * @param clusterType - the cluster type
	 * @see ClusterType#LIVE
	 * @see ClusterType#CORE
	 */
	public void setClusterType(Integer clusterType) {
		this.clusterType = clusterType;
	}

	/**
	 * Returns heart beat interval between two heart beat messages.
	 */
	public Integer getHeartbeatInterval() {
		return heartbeatInterval;
	}

	/**
	 * Set heart beat interval between two heart beat messages.
	 * 
	 * @param interval - the interval to be set
	 */
	public void setHeartbeatInterval(Integer interval) {
		this.heartbeatInterval = interval;
	}

	/**
	 * Returns heart beat timeout.
	 */
	public Integer getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	/**
	 * Set heart beat timeout.
	 * 
	 * @param timeout - the timeout to be set
	 */
	public void setHeartbeatTimeout(Integer timeout) {
		this.heartbeatTimeout = timeout;
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

	public Integer getAlertAutoDeleteDays() {
		return alertAutoDeleteDays;
	}

	public void setAlertAutoDeleteDays(Integer alertAutoDeleteDays) {
		this.alertAutoDeleteDays = alertAutoDeleteDays;
	}
}
