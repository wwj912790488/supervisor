package com.arcsoft.supervisor.cluster.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;

/**
 * Send data package to the specified address & port.
 * 
 * @author xpeng
 * @author fjli
 */
public class MulticastClient extends DatagramSocket {

	/**
	 * Construct default send socket.
	 */
	public MulticastClient() throws IOException{
		super();
	}

	/**
	 * Construct client and bound to the specified local socket address. 
	 * 
	 * @param addr - the specified local socket address
	 * @throws java.io.IOException
	 */
	public MulticastClient(SocketAddress addr) throws IOException {
		super(addr);
	}

	/**
	 * Construct client with binding to the specified port.
	 * 
	 * @param port - the specified port to bind
	 * @throws java.io.IOException
	 */
	public MulticastClient(int port) throws IOException {
		super(port);
	}

	/**
	 * Construct client with binding to the specified address and port.
	 * 
	 * @param address - the specified address to bind
	 * @param port - the specified port to bind
	 * @throws java.io.IOException
	 */
	public MulticastClient(InetAddress address, int port) throws IOException {
		super(port, address);
	}

	/**
	 * Binds this client to a specific address & port. 
	 * 
	 * @param addr - the address to bind to, if it is null, use any local address.
	 * @param port - the port to bind to
	 * @throws java.io.IOException
	 */
	public void bind(String addr, int port) throws IOException {
		bind(new InetSocketAddress(addr != null ? InetAddress.getByName(addr) : null, port));
	}

	/**
	 * Send the data package to the specified address.
	 * 
	 * @param destAddr - the specified target address
	 * @param destPort - the specified target port
	 * @throws java.io.IOException
	 */
	public void send(String destAddr, int destPort, DataPackage dp) throws IOException {
		if (destAddr == null)
			throw new NullPointerException();
		InetAddress addr = InetAddress.getByName(destAddr);
		send(addr, destPort, dp);
	}

	/**
	 * Send the data package to the specified address.
	 * 
	 * @param destAddr - the specified target address
	 * @param destPort - the specified target port
	 * @throws java.io.IOException
	 */
	public void send(InetAddress destAddr, int destPort, DataPackage dp) throws IOException {
		byte[] sendData = dp.toBytes();
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destAddr, destPort);
		send(packet);
	}

	/**
	 * Send data package to the specified address.
	 * 
	 * @param destAddr - the specified target address
	 * @param pack - the data package to be sent
	 * @throws java.io.IOException - if send failed or socket already closed.
	 */
	public void send(SocketAddress destAddr, DataPackage pack) throws IOException {
		if (destAddr == null)
			throw new NullPointerException();
		byte[] sendData = pack.toBytes();
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destAddr);
		send(packet);
	}

	/**
	 * Receive data package with default buffer size.
	 * 
	 * @return Returns the received package.
	 * @throws java.io.IOException - if read failed or data is invalid.
	 */
	public DataPackage receive() throws IOException {
		return receive(4096);
	}

	/**
	 * Receive data package with the specified buffer size.
	 * 
	 * @param bufferSize - the max size to be received
	 * @return Returns the received package.
	 * @throws java.io.IOException - if read failed or data is invalid.
	 */
	public DataPackage receive(int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		receive(packet);
		int len = packet.getLength();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, len);
		try {
			return DataPackage.read(bais);
		} finally {
			bais.close();
		}
	}

}
