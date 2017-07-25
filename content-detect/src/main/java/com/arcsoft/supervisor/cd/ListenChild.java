package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.AbstractInfo;
import com.arcsoft.supervisor.cd.data.TaskCompleteInfo;
import com.arcsoft.supervisor.cd.data.TaskState;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ListenChild {
	private Logger logger = Logger.getLogger(ListenChild.class);
	private final Socket socket;
	private DataProcessListener dataProcessListener = null;
	private volatile boolean running = true;
	private static final int BUFFER_MAX_LEN = 4096;
	private byte[] buffer = new byte[BUFFER_MAX_LEN];

	private DataProcess dataProcess = new DataProcess();

	public ListenChild(Socket socket) {
		this.socket = socket;

	}

	public void setDataProcessListener(DataProcessListener dataProcessListener) {
		this.dataProcessListener = dataProcessListener;
	}

	public void shutdown() {
		running = false;
		try {
			if (!socket.isOutputShutdown()) {
				socket.shutdownOutput();
			}
			if (!socket.isInputShutdown()) {
				socket.shutdownInput();
			}
			socket.close();
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	public void start() {
		InputStream inputStream = null;
		try {
			inputStream = socket.getInputStream();
			do {
				int ret = SocketReader.readSocket(inputStream, buffer);
				if (ret < 0) {
					break;
				}
				process(ret);
			} while (running);
		} catch (IOException e) {
			logger.error(null, e);
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	private void process(int len) {
		if (len > 0) {
			AbstractInfo data = dataProcess.process(buffer, len);
			if (data instanceof TaskCompleteInfo) {
				TaskCompleteInfo info = (TaskCompleteInfo) data;
				if (info.getResult() < 0) {
					dataProcessListener.receivedTaskState(info.getTaskid(), TaskState.STATE_ERROR);
				} else {
					dataProcessListener.receivedTaskState(info.getTaskid(), TaskState.STATE_STOP);
				}
			} else {
				dataProcessListener.receive(data);
			}

			data = null;
		}

	}
}
