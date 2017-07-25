package com.arcsoft.supervisor.commons.spring.event;

import org.springframework.transaction.support.ResourceHolderSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * The event resource holder.
 * 
 * @author fjli
 */
public class EventResourceHolder extends ResourceHolderSupport {

	private List<Object> eventList = new ArrayList<>();

	/**
	 * Add new event into this holder.
	 * 
	 * @param event - the event to be added
	 */
	public void addEvent(Object event) {
		eventList.add(event);
	}

	/**
	 * Returns all events in this holder.
	 */
	public List<?> getEvents() {
		return eventList;
	}

	/**
	 * Clear the events.
	 */
	public void clear() {
		super.clear();
		eventList.clear();
	}

}
