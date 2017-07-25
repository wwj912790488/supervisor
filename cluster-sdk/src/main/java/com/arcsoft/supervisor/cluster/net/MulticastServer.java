package com.arcsoft.supervisor.cluster.net;

import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class describes the multicast transfer layer, including sending and
 * receiving UDP data package, the nodes in cluster transfer the event using
 * multicast transfer layer.
 * 
 * @author xpeng
 * 
 */
public class MulticastServer implements Runnable {
	private static final int DEFAULT_BROADCAST_PORT = 8898;
	private static final int DEFAULT_PROCESS_THREADS = 5;
	private static final int DATA_LEN = 4096;

	private MulticastSocket multicastSocket;
	private InetAddress broadcastAddress;
	private int groupPort = DEFAULT_BROADCAST_PORT;
	private EventHandler eventHandler;
	private Thread listenThread;
	private ExecutorService threadPool;
	private volatile boolean closed = false;
	private Object lock = new byte[0];

	private Logger logger = Logger.getLogger(MulticastServer.class);

	/**
	 * Initialize the multicast server.
	 * 
	 * @param ip - the multicast ip
	 * @param port - the mutlicast port
	 * @param bindIp - the bind ip
	 * @param threads - the max threads count to process events
	 * @param handler - the event handler which used to process received events
	 * @throws java.io.IOException if any socket exception occur
	 */
	public void init(String ip, int port, String bindIp, int threads, EventHandler handler) throws IOException {
		init(ip, port, bindIp, -1, threads, handler);
	}

	/**
	 * Initialize the multicast server.
	 * 
	 * @param ip - the multicast ip
	 * @param port - the mutlicast port
	 * @param bindIp - the bind ip
	 * @param ttl - the time-to-live for multicast packets sent out on the socket
	 * @param threads - the max threads count to process events
	 * @param handler - the event handler which used to process received events
	 * @throws java.io.IOException if any socket exception occur
	 */
	public void init(String ip, int port, String bindIp, int ttl, int threads, EventHandler handler) throws IOException {
		if (null == ip) {
			throw new NullPointerException("group address cannot be null");
		}
		if (port > 0)
			this.groupPort = port;
		int nThreads = DEFAULT_PROCESS_THREADS;
		if (threads > 0)
			nThreads = threads;
		this.eventHandler = handler;

		multicastSocket = new MulticastSocket(this.groupPort);
		if (null != bindIp) {
			InetAddress inf = InetAddress.getByName(bindIp);
			multicastSocket.setInterface(inf);
		}
		if (ttl >= 0)
			multicastSocket.setTimeToLive(ttl);
		broadcastAddress = InetAddress.getByName(ip);
		multicastSocket.joinGroup(broadcastAddress);
		threadPool = Executors.newFixedThreadPool(nThreads, NamedThreadFactory.create("MulticastServer-" + this));
		listenThread = new Thread(this);
		listenThread.start(); 
	}

	/**
	 * uninitialize the multicast transfer layer.
	 * 
	 * @throws java.io.IOException
	 * @throws InterruptedException
	 */
	public void uninit() throws IOException {
		// TODO: how to close the thread which is receiving data
		synchronized (lock) {
			if (multicastSocket != null) {
				multicastSocket.leaveGroup(broadcastAddress);
				multicastSocket.close();
				closed = true;
			}
		}
		try {
			if (null != listenThread) {
				listenThread.join();
			}
			if (null != threadPool) {
				threadPool.shutdown();
			}
		} catch (InterruptedException e) {

		}
	}

	/**
	 * Send package to the all group members.
	 * 
	 * @param pack - the data package to be sent
	 */
	public void send(DataPackage pack) throws IOException {
		if (multicastSocket == null)
			throw new IOException("This server has not initialized.");
		byte[] data = pack.toBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length,
				broadcastAddress, groupPort);
		multicastSocket.send(packet);
	}

	class ProcessThread implements Runnable {
		private SocketAddress fromAddr;
		private DataPackage dp;

		ProcessThread(SocketAddress fromAddress, DataPackage dp) {
			this.fromAddr = fromAddress;
			this.dp = dp;
		}

		public void run() {
			if (null != eventHandler) {
				eventHandler.process(fromAddr, dp);
			}
		}
	}

	public void run() {
		try {
			while (true) {
				// TODO: if the data length larger than DATA_LEN?
				byte[] inBuff = new byte[DATA_LEN];
				DatagramPacket inPacket = new DatagramPacket(inBuff,
						inBuff.length);

				multicastSocket.receive(inPacket);

				logger.debug("------Receive broadcast message");
				DataPackage dp = null;
				try {
					ByteArrayInputStream bin = new ByteArrayInputStream(
							inPacket.getData(), 0, inPacket.getLength());

					dp = DataPackage.read(bin);
					bin.close();
				} catch (IOException ioe) {
					logger.error("multicast server receive a error package");
				}
				if (dp != null) {
					logger.debug("action: " + dp.getType());
					logger.debug("recv form ip: " + inPacket.getAddress()
							+ ", port: " + inPacket.getPort());

					logger.debug("recv at ip: "
							+ multicastSocket.getLocalAddress() + ", port:"
							+ multicastSocket.getLocalPort());

					threadPool.execute(new ProcessThread(inPacket.getSocketAddress(),
							dp));
				}

				logger.debug("receive one package end");

			}
		} catch (IOException ex) {
			logger.info("receive broadcast err");			
			try {
				synchronized (lock) {
					if (!closed) {
						multicastSocket.leaveGroup(broadcastAddress);
						multicastSocket.close();
					}
					multicastSocket = null;
				}
			} catch (IOException e) {
				logger.debug("close broadcast Socket err", e);
			}
		}
		logger.info("stop receive broadcast!");
	}

	public InetAddress getBroadcastAddress() {
		return broadcastAddress;
	}

	public int getGroupPort() {
		return groupPort;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setEventHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

}
