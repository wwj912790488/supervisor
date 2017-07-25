package com.arcsoft.supervisor.cluster.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * socket connection
 * @author sxchen
 */
public class TcpClient implements TcpConnection {

	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;

	/**
	 * Socket connection created by Server accept the connect from client
	 * 
	 * @param socket - the connected socket
	 * @throws java.io.IOException
	 */
	public TcpClient(Socket socket) throws IOException {
		this.socket = socket;
		try {
			init();
		} catch(IOException e) {
			close();
			throw e;
		}
	}

	/**
	 * Socket connection created by Client connect to host and port,
	 * it will create socket
	 *
	 * @param ip: the ip to connect; if ip = null, connect local host
	 * @param port
	 * @throws java.io.IOException
	 */
	public TcpClient(String ip, int port) throws IOException {
		this.socket = new Socket(ip, port);
		try {
			init();
		} catch(IOException e) {
			close();
			throw e;
		}
	}

	/**
	 * Create connection to the specified host and port within the timeout.
	 * 
	 * @param ip - the target ip address
	 * @param port - the target port
	 * @param timeout - the connect timeout
	 * @throws java.io.IOException if an error occurs during the connection
	 * @throws java.net.SocketTimeoutException if timeout expires before connecting
	 */
	public TcpClient(String ip, int port, int timeout) throws IOException {
		this.socket = new Socket();
		try {
			InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(ip), port);
			socket.connect(addr, timeout);
			init();
		} catch(IOException e) {
			close();
			throw e;
		}
	}

	/**
	 * Create connection to the specified host and port with the specified options.
	 * 
	 * @param ip - the target ip address
	 * @param port - the target port
	 * @param options - the connect options
	 * @throws java.io.IOException if an error occurs during the connection
	 * @throws java.net.SocketTimeoutException if timeout expires before connecting
	 */
	public TcpClient(String ip, int port, ConnectOptions options) throws IOException {
		if (options == null) {
			this.socket = new Socket(ip, port);
			try {
				init();
			} catch(IOException e) {
				close();
				throw e;
			}
		} else {
			String bindIP = options.getString(ConnectOptions.OPTION_BIND_ADDR);
			int bindPort = options.getInt(ConnectOptions.OPTION_BIND_PORT, 0);
			int timeout = options.getInt(ConnectOptions.OPTION_CONNECT_TIMEOUT, 0);
			InetAddress bindAddr = null;
			if (bindIP != null)
				bindAddr = InetAddress.getByName(bindIP);
			this.socket = new Socket();
			try {
				socket.bind(new InetSocketAddress(bindAddr, bindPort));
				socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), timeout);
				init();
				if (options.containsOption(ConnectOptions.OPTION_READ_TIMEOUT)) {
					int readTimeout = options.getInt(ConnectOptions.OPTION_READ_TIMEOUT, 0);
					socket.setSoTimeout(readTimeout);
				}
				if (options.containsOption(ConnectOptions.OPTION_TCP_NODELAY)) {
					int noDelay = options.getInt(ConnectOptions.OPTION_TCP_NODELAY, 0);
					socket.setTcpNoDelay(noDelay > 0);
				}
			} catch(IOException e) {
				close();
				throw e;
			}
		}
	}

	/**
	 * Initialize socket objects.
	 * 
	 * @throws java.io.IOException if
	 */
	private void init() throws IOException {
		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void setTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	/**
	 * uninit other resources and socket connection by close
	 */
	@Override
	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
		if (dos != null) {
			try {
				dos.close();
			} catch (IOException e) {
			}
		}
		if (dis != null) {
			try {
				dis.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public String getRemoteIp() {
		InetAddress inet = socket.getInetAddress();
		return inet.getHostAddress();
	}

	@Override
	public int getRemotePort() {
		return socket.getPort();
	}

	@Override
	public String getLocalIp() {
		InetAddress inet = socket.getLocalAddress();
		return inet.getHostAddress();
	}

	@Override
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	@Override
	public DataPackage read() throws IOException {
		return DataPackage.read(dis);
	}

	@Override
	public void write(DataPackage pack) throws IOException {
		dos.write(pack.toBytes());
		dos.flush();
	}

}
