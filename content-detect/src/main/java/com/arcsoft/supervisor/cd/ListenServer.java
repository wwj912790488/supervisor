package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ListenServer implements Runnable {

	private int port;
	private volatile boolean runing = true;
	private Logger logger = Logger.getLogger(ListenServer.class);
	private DataProcessListener dataProcessListener = null;
	private ExecutorService pool = Executors.newCachedThreadPool(NamedThreadFactory.create("Content-Detect:ListenServer"));
	ServerSocket serverSocket = null;

	public ListenServer() {

	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setDataProcessListener(DataProcessListener dataProcessListener) {
		this.dataProcessListener = dataProcessListener;
	}

	public void shutdown() {
		runing = false;
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {

			}
		}
		pool.shutdown();
		try {
			pool.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			pool.shutdownNow();
		}

	}

	@Override
	public void run() {

		try {
			serverSocket = new ServerSocket(port);
			port = serverSocket.getLocalPort();
			do {
				final Socket socket = serverSocket.accept();
				pool.execute(new Runnable() {
					@Override
					public void run() {
						process(socket);
					}
				});

			} while (runing);
		} catch (IOException e) {
			// logger.error(null, e);
		}
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {
			logger.error(null, e);
		}

	}

	private void process(Socket socket) {
		ListenChild child = new ListenChild(socket);
		child.setDataProcessListener(dataProcessListener);
		child.start();
	}

}
