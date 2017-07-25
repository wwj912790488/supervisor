package com.arcsoft.supervisor.cluster.event;

import com.arcsoft.supervisor.cluster.node.Node;

import java.net.SocketAddress;


/**
 * Search event.
 * 
 * @author fjli
 */
public class SearchEvent extends Event {

	private int type;
	private SocketAddress from;

	/**
	 * Construct search event.
	 * 
	 * @param type - the node type to be searched.
	 * @see Node#TYPE_SEARCH_ALL
	 */
	public SearchEvent(int type) {
		this.type = type;
	}

	/**
	 * Construct search event which received from client.
	 * 
	 * @param from - the client address
	 * @param type - the node type to be searched
	 */
	public SearchEvent(SocketAddress from, int type) {
		this.from = from;
		this.type = type;
	}

	/**
	 * Return the search client address.
	 */
	public SocketAddress getFrom() {
		return from;
	}

	/**
	 * Returns the search type.
	 */
	public int getType() {
		return type;
	}

}
