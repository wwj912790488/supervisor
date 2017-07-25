package com.arcsoft.supervisor.commons.spring.event;

/**
 * Event manager.
 * 
 * @author fjli
 */
public interface EventManager {

	/**
	 * Deliver event immediately.
	 * 
	 * @param event - the event to be dispatched
	 */
	void deliver(Object event);

	/**
	 * Submit a event.
	 * <ul>
	 * <li>If no transaction exists, the event will be dispatched immediately.
	 * <li>If there is read only transaction, the event will be dispatched immediately.
	 * <li>If there is transaction exists, dispatch event after transaction is committed.
	 * <li>If the transaction is failed, the event will be not dispatched.
	 * </ul>
	 * @param event - the event to be dispatched
	 */
	void submit(Object event);

}
