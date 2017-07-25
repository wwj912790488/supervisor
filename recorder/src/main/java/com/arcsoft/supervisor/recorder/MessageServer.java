package com.arcsoft.supervisor.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A socket message server.
 * 
 * @author fjli
 */
public abstract class MessageServer {

    private static final Logger logger = LoggerFactory.getLogger(MessageServer.class);
	private ServerSocket server;
	private int port;
	private ThreadPoolExecutor executor;

	/**
	 * Construct message server on the specified port.
	 * 
	 * @param port - the listen port
	 */
	public MessageServer(int port) {
		this.port = port;
	}

	/**
	 * Start server, and wait for messages.
	 * 
	 * @throws java.io.IOException
	 */
	public synchronized void start() throws IOException {
		server = new ServerSocket();
		server.bind(new InetSocketAddress("127.0.0.1", port));
		executor = new ThreadPoolExecutor(0, 5, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						final Socket socket = server.accept();
						executor.execute(new Runnable() {
							@Override
							public void run() {
								process(socket);
							}
						});
					} catch (IOException e) {
						break;
					}
				}
			}
		});
	}

	/**
	 * Stop server.
	 */
	public synchronized void stop() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
			}
		}
        executor.shutdown();
	}

	/**
	 * Process the socket.
	 * 
	 * @param socket - the connected socket
	 */
	private void process(Socket socket) {
		DataInputStream dis = null;
		BufferedWriter writer = null;
		try {
			dis = new DataInputStream(socket.getInputStream());
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			messageReceived(dis.readUTF(), writer);
		} catch(IOException e) {
		} finally {
			if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Process the received message.
	 * 
	 * @param message - the received message
	 * @param writer - the output
	 */
	protected abstract void messageReceived(String message, BufferedWriter writer);

}
