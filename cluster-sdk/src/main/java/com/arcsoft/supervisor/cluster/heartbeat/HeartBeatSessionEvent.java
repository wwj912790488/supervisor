package com.arcsoft.supervisor.cluster.heartbeat;

import java.util.EventObject;

/**
 * Heart beat session event.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class HeartBeatSessionEvent extends EventObject {

	/**
	 * Construct new event with the specified session.
	 * 
	 * @param session - the specified session.
	 */
	public HeartBeatSessionEvent(HeartBeatSession session) {
		super(session);
	}

	/**
	 * Returns the session.
	 */
	public HeartBeatSession getSession() {
		return (HeartBeatSession) getSource();
	}

}
