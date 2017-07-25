package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectCheck {

	private ExecutorService pool = Executors.newCachedThreadPool(NamedThreadFactory.create("Content-Detect:ConnectCheck"));
	private String ip = "127.0.0.1";
	private ConnectCheckListener connectCheckListener = null;
	private Logger logger = Logger.getLogger(ConnectCheck.class);

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setConnectCheckListener(ConnectCheckListener connectCheckListener) {
		this.connectCheckListener = connectCheckListener;
	}

	public void uninit() {
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			pool.shutdownNow();
		}
	}

	public void addPort(final int port) {

		pool.execute(new Runnable() {
			@Override
			public void run() {
				process(port);
			}
		});
	}

	private void process(int port) {
		Socket socket = null;
		InputStream inputStream = null;
		try {
			socket = new Socket(ip, port);
			inputStream = socket.getInputStream();
			do {
				byte[] in = new byte[1024];
				int len = inputStream.read(in);
				if (len < 0) {
					break;
				}
			} while (true);

		} catch (Exception e) {

		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {

			}
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (Exception ex) {

			}
		}

		if (connectCheckListener != null) {
			connectCheckListener.connectError(port);
		}
		logger.info("process exit");
	}

}
