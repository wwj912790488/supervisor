package com.arcsoft.supervisor.recorder;

import com.arcsoft.supervisor.recorder.data.Command;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandClient implements Runnable {

	private Logger logger = Logger.getLogger(CommandClient.class);

	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;
	private int port;
	private String ip = "127.0.0.1";
	private Object lock = new Object();
	private static final int BUFFER_MAX_LEN = 4096;
	private RecordCommandHandler handler;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private ICommandClientStateChanged stateChangeHandler;

	public CommandClient(int port, RecordCommandHandler handler, ICommandClientStateChanged stateChangeHandler) {
		this.port = port;
		this.handler = handler;
		this.stateChangeHandler = stateChangeHandler;
	}

	public void connect() throws IOException {
		try {
			socket = new Socket(ip, port);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			executor.execute(this);
		} catch (UnknownHostException e) {
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
		executor.shutdown();
	}

	public boolean send(Command command) {

		synchronized (lock) {
			byte[] buffer = new byte[8];
			Arrays.fill(buffer, (byte) 0);

			MessagePack msgpack = new MessagePack();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Packer packer = msgpack.createPacker(out);
			try {
				packer.write(command);
				byte[] bytes = out.toByteArray();
				Utils.intToBytes(bytes.length, buffer, 0);
				Utils.intToBytes(command.getType(), buffer, 4);
				outputStream.write(buffer, 0, 8);
				outputStream.write(bytes, 0, bytes.length);
				outputStream.flush();
			} catch (IOException e) {
				return false;
			}
			try {
				packer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}

	}

	private void read() throws IOException {
		DataProcess socketReader = new DataProcess();
		
		byte[] buffer = new byte[1024];
		int len = SocketReader.readSocket(inputStream, buffer);
		if (len > 0) {
			Command command = socketReader.process(buffer, len);
			if(command != null) {
				handler.receive(command);
			}
		} else {
			throw new IOException();
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				read();
			} catch (IOException e) {
				break;
			}
		}
		stateChangeHandler.onClientDisconnected();
		close();
	}

}
