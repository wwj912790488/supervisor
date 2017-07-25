package com.arcsoft.supervisor.cluster.heartbeat;

/**
 * This event will be raised when the session is time out.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class HeartBeatSessionTimeoutEvent extends HeartBeatSessionEvent {

	/**
	 * Construct new heart beat session timeout event.
	 * 
	 * @param session - the specified session.
	 */
	public HeartBeatSessionTimeoutEvent(HeartBeatSession session) {
		super(session);
	}

}
