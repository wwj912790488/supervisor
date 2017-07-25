package com.arcsoft.supervisor.cluster.event;

/**
 * The listener interface for receiving events.
 * 
 * @author fjli
 */
public interface EventListener {

	/**
	 * Notify the event.
	 * 
	 * @param event - the received event.
	 */
	void eventReceied(Event event);

}
