package com.arcsoft.supervisor.cluster;

/**
 * Definition the roles of servers.
 * 
 * @author fjli
 */
public interface ServerRole {

	/**
	 * Indicate the role of the server is not set.
	 */
	public static final int ROLE_UNKNOWN = 0;

	/**
	 * Indicate the server work as a master.
	 */
	public static final int ROLE_MASTER = 1;

	/**
	 * Indicate the server work as a slave.
	 */
	public static final int ROLE_SLAVE = 2;

}
