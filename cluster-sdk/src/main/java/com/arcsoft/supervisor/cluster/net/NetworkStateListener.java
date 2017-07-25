package com.arcsoft.supervisor.cluster.net;

/**
 * Network state listener.
 * 
 * @author fjli
 */
public interface NetworkStateListener {

	/**
	 * Notify when the network interface state changed.
	 * 
	 * @param eth - the name of the network interface
	 * @param active - the state of the network interface
	 */
	void ethStateChanged(String eth, boolean active);

}
