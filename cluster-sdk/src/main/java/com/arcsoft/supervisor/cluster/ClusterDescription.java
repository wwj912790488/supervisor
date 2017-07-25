package com.arcsoft.supervisor.cluster;

/**
 * This class describe a cluster information, and it used to create a cluster.
 * 
 * @author fjli
 */
public class ClusterDescription {

	/**
	 * This description is used to create no broadcast cluster.
	 */
	public final static ClusterDescription NO_BROAD_CAST = new ClusterDescription();

	private String ip;
	private int port;
	private String bindIp;
	private int ttl;

	/**
	 * Create empty cluster description.
	 */
	private ClusterDescription() {
		
	}

	/**
	 * Construct cluster description.
	 * 
	 * @param ip - the cluster ip
	 * @param port - the cluster port
	 */
	public ClusterDescription(String ip, int port) {
		this(ip, port, null);
	}

	/**
	 * Construct cluster description.
	 * 
	 * @param ip - the cluster ip
	 * @param port - the cluster port
	 * @param bindIp - the network interface to be bind.
	 */
	public ClusterDescription(String ip, int port, String bindIp) {
		this(ip, port, bindIp, 1);
	}

	/**
	 * Construct cluster description.
	 * 
	 * @param ip - the cluster ip
	 * @param port - the cluster port
	 * @param bindIp - the network interface to be bind
	 * @param ttl - the time-to-live for multicast packets sent out on the socket
	 */
	public ClusterDescription(String ip, int port, String bindIp, int ttl) {
		this.ip = ip;
		this.port = port;
		this.bindIp = bindIp;
		this.ttl = ttl;
	}

	/**
	 * Get the cluster IP address.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Get the cluster port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the bind address.
	 */
	public String getBindAddress() {
		return bindIp;
	}

	/**
	 * Returns the default time-to-live for multicast packets sent out on the socket.
	 */
	public int getTimeToLive() {
		return this.ttl;
	}

}
