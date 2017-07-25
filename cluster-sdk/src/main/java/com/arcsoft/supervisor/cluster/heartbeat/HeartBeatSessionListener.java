package com.arcsoft.supervisor.cluster.heartbeat;

/**
 * Heart beat session listener.
 * 
 * @author fjli
 */
public interface HeartBeatSessionListener {

	/**
	 * Notify this listener with the specified session event.
	 * 
	 * @param event - the session event
	 */
	void sessionEventReceived(HeartBeatSessionEvent event);

}
