package com.arcsoft.supervisor.cluster.event;


import com.arcsoft.supervisor.cluster.message.Message;

/**
 * This is the base class for all events.
 * 
 * @author fjli
 */
public abstract class Event implements Message {

	/**
	 * Event id for join event.
	 */
	public static final int JOIN_EVENT = 0x0001;

	/**
	 * Event id for leave event.
	 */
	public static final int LEAVE_EVENT = 0x0002;

	/**
	 * Event id for heart beat event.
	 */
	public static final int HEART_BEAT_EVENT = 0x0004;

	/**
	 * Event id for search event.
	 */
	public static final int SEARCH_EVENT = 0x0005;

	/**
	 * Event id for heart beat start event.
	 */
	public static final int HEART_BEAT_START_EVENT = 0x0006;

	/**
	 * Event id for heart beat stop event.
	 */
	public static final int HEART_BEAT_STOP_EVENT = 0x0007;

	/**
	 * Event id for response event.
	 */
	public static final int RESPONSE_EVENT = 0x0008;

	@Override
	public int getMessageType() {
		return Message.TYPE_EVENT;
	}

}
