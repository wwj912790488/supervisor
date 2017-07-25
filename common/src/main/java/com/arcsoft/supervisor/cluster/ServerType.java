package com.arcsoft.supervisor.cluster;

/**
 * Definition the type of servers.
 * 
 * @author fjli
 */
public interface ServerType {

	/**
	 * Indicate the type of the server is not set.
	 */
	public static final int TYPE_UNKNOWN = 0;

	/**
	 * Indicate the server is a commander server.
	 */
	public static final int TYPE_COMMANDER = 1;

	/**
	 * Indicate the server is a 1+1 agent.
	 */
	public static final int TYPE_1_1 = 2;

	/**
	 * Indicate the server is a m+n agent.
	 */
	public static final int TYPE_M_N = 3;

}
