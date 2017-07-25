package com.arcsoft.supervisor.cluster.message;

/**
 * All requests responses and events implements this interface.
 * 
 * @author fjli
 */
public interface Message {

	/**
	 * Unknown message type.
	 */
	public static final int TYPE_UNKNOWN = 0x0000;

	/**
	 * Message type for system event.
	 */
	public static final int TYPE_EVENT = 0x0001;

	/**
	 * Message type for exception.
	 */
	public static final int TYPE_EXCEPTION = 0x0002;

	/**
	 * User custom message types starts from it.
	 */
	public static final int TYPE_MIN_USER = 0x1000;

	/**
	 * Returns message type.
	 */
	int getMessageType();

}
