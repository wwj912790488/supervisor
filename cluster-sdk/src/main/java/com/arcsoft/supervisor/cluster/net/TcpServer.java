package com.arcsoft.supervisor.cluster.net;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server Thread listening for client socket connect
 * @author sxchen
 *
 */
public class TcpServer implements Runnable {
	
	private static final Logger logger = Logger.getLogger(TcpServer.class);
	
	private ServerSocket listenSocket = null;
	private boolean bRunning = false;
	private Thread thread;
	private TcpConnectionListener tcpConnectionListener = null;
	
	public TcpServer() {
	}

	/**
	 * ServerSocket is listening, wait for accept client connect
	 * 
	 */
	public void run() {
		while (bRunning) {
			try {
				if (listenSocket != null) {
					Socket connSocket = listenSocket.accept();
					TcpClient connection = new TcpClient(connSocket);
					if (tcpConnectionListener != null) {
						tcpConnectionListener.connectionCreated(connection);
					}
				}
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
			}
		}
	}

	/**
	 * tcp server start listen at ip with port
	 * @throws java.io.IOException
	 */
	public void listen(String ip, int port) throws IOException {
		listenSocket = new ServerSocket();
		if (ip == null) {
			listenSocket.bind(new InetSocketAddress(port));
		}
		else {
			listenSocket.bind(new InetSocketAddress(ip, port));
		}
		
		bRunning = true;
		
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * close the tcp server socket which listen at ip with port
	 */
	public void close() {
		bRunning = false;
		tcpConnectionListener = null;
		
		if (listenSocket != null) {
			try {
				listenSocket.close();
			} catch (IOException e) {
			}
		}
		
		try {
			if (thread != null)
				thread.join();
		} catch (InterruptedException e1) {
		}

		listenSocket = null;
	}

	/**
	 * add listenter to TcpServer: when tcp client socket connect in, 
	 * tell the listener this accepted tcp socket 
	 * @param listener
	 */
	public void addListener(TcpConnectionListener listener) {
		if (listener != null) {
			tcpConnectionListener = listener;
		}
	}

	/**
	 * remove the listener which added by addListener
	 * @param listener
	 */
	public void removeListener(TcpConnectionListener listener) {
		tcpConnectionListener = listener;
	}
}
