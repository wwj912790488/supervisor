package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.AbstractInfo;
import com.arcsoft.supervisor.cd.data.CommandDefine;
import com.arcsoft.supervisor.cd.data.CommandGetThumbnail;
import org.apache.log4j.Logger;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MessageSender {

	private Logger logger = Logger.getLogger(MessageSender.class);

	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket = null;
	private int port;
	private String ip = "127.0.0.1";
	private Object lock = new Object();
	private static final int BUFFER_MAX_LEN = 4096;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public MessageSender() {
	}

	public void create() {
		try {
			socket = new Socket(ip, port);
			// socket.setSoTimeout(5000);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (UnknownHostException e) {
			logger.error(null, e);
		} catch (IOException e) {
			logger.error(null, e);
		}
	}

	public boolean isConnected() {
		if (socket != null) {
			return socket.isConnected();
		}

		return false;
	}

	public void close() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}

			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {

		}
	}

	public AbstractInfo send(int command, AbstractInfo data) {
		AbstractInfo ret = null;

		synchronized (lock) {
			byte[] buffer = new byte[1024];
			Arrays.fill(buffer, (byte) 0);
			buffer[0] = 'M';
			buffer[1] = 'V';
			buffer[2] = 'C';
			buffer[3] = 'H';
			Utils.intToBytes(command, buffer, 4);

			MessagePack msgpack = new MessagePack();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Packer packer = msgpack.createPacker(out);
			try {
				packer.write(data);
				byte[] bytes = out.toByteArray();
				Utils.intToBytes(bytes.length, buffer, 8);
				outputStream.write(buffer, 0, 12);
				outputStream.write(bytes, 0, bytes.length);
				outputStream.flush();
				int bufferSize = BUFFER_MAX_LEN;
				if (command == CommandDefine.COMMAND_THUMBNAIL) {
					CommandGetThumbnail thumb = (CommandGetThumbnail) data;
					bufferSize = thumb.getWidth() * thumb.getHeight() * 3 / 2 + 64;
				}

				ret = getCommandResult(bufferSize);

			} catch (IOException e) {
				// logger.error(null, e);
			}
			try {
				packer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;

	}

	public AbstractInfo sendCommand(int command) {
		AbstractInfo ret = null;
		synchronized (lock) {
			try {
				byte[] buffer = new byte[1024];
				Arrays.fill(buffer, (byte) 0);
				buffer[0] = 'M';
				buffer[1] = 'V';
				buffer[2] = 'C';
				buffer[3] = 'H';

				Utils.intToBytes(command, buffer, 4);
				Utils.intToBytes(0, buffer, 8);
				outputStream.write(buffer, 0, 12);
				outputStream.flush();
				ret = getCommandResult(BUFFER_MAX_LEN);
			} catch (IOException e) {

			}
		}

		return ret;
	}

	private AbstractInfo getCommandResult(int bufferSize) {
		DataProcess socketReader = new DataProcess();
		AbstractInfo ret = null;
		try {
			byte[] buffer = new byte[bufferSize];
			int len = SocketReader.readSocket(inputStream, buffer);
			if (len > 0) {
				ret = socketReader.process(buffer, len);
			}
		} catch (Exception e) {
			logger.error(null, e);
		}
		if (ret != null) {
			return ret;
		}
		return null;
	}

}
