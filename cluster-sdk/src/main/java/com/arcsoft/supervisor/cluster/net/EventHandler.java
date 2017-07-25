package com.arcsoft.supervisor.cluster.net;

import java.net.SocketAddress;

/**
 * This class represents the event handler
 * <p>
 * The event handler is used to handler the event received from the multicast
 * transfer layer.
 * 
 * @author xpeng
 */
public interface EventHandler {

	/**
	 * Process received data package which received from client.
	 * 
	 * @param fromAddr - the sender's address
	 * @param dp - the received data package
	 */
	void process(SocketAddress fromAddr, DataPackage dp);

}
