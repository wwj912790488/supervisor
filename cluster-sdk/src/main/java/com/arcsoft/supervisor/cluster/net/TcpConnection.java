package com.arcsoft.supervisor.cluster.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;

/**
 * TcpConnection is a tcp socket connection between client and server.
 * 
 * @author fjli
 */
public interface TcpConnection extends Closeable {

	/**
	 * Set read timeout.
	 * 
	 * @param timeout - the specified timeout, in milliseconds
	 * @throws java.net.SocketException if there is an error in the underlying protocol, such as a TCP error.
	 */
	void setTimeout(int timeout) throws SocketException;

	/**
	 * Returns remote IP address.
	 */
	String getRemoteIp();

	/**
	 * Returns remote port.
	 */
	int getRemotePort();

	/**
	 * Returns local IP address.
	 */
	String getLocalIp();

	/**
	 * Return local port.
	 */
	int getLocalPort();

	/**
	 * Read data package from remote.
	 * 
	 * @return Returns received data package.
	 * @throws java.io.IOException - if connection error or invalid package received.
	 */
	DataPackage read() throws IOException;

	/**
	 * Write data to remote.
	 * 
	 * @param pack - the data package to be sent
	 * @throws java.io.IOException - if connection error.
	 */
	void write(DataPackage pack) throws IOException;

	/**
	 * Close this connection.
	 */
	void close();

}
