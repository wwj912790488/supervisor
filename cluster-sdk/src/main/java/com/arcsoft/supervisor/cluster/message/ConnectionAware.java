package com.arcsoft.supervisor.cluster.message;

import com.arcsoft.supervisor.cluster.net.TcpConnection;

/**
 * If the message implements this interface, indicate when the node received this
 * type message, the current connection will be set.
 * 
 * @author fjli
 */
public interface ConnectionAware {

	/**
	 * Set the current connection to the message.
	 * 
	 * @param connection - the current connection
	 */
	void setConnection(TcpConnection connection);

}
