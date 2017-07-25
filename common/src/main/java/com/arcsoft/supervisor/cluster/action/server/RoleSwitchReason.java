package com.arcsoft.supervisor.cluster.action.server;

/**
 * Role switch reason.
 * 
 * @author fjli
 */
public interface RoleSwitchReason {

	/**
	 * Indicate this request is raised because of heart beat timeout.
	 */
	public static final int REASON_HEARTBEAT = 1;

	/**
	 * Indicate this request is raised by user.
	 */
	public static final int REASON_USER = 2;

	/**
	 * Indicate this request is raised because of master is deleted.
	 */
	public static final int REASON_MASTER_DELETED = 3;

	/**
	 * Indicate this request is raised because of the master's network error.
	 */
	public static final int REASON_MASTER_NETWORK_ERROR = 4;

}
