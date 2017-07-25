package com.arcsoft.supervisor.cluster.service;

import com.arcsoft.supervisor.cluster.app.*;
import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.event.Event;
import com.arcsoft.supervisor.cluster.event.HeartBeatStartEvent;
import com.arcsoft.supervisor.cluster.event.HeartBeatStopEvent;
import com.arcsoft.supervisor.cluster.event.ResponseEvent;
import com.arcsoft.supervisor.cluster.message.ConnectionAware;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.net.TcpConnection;
import com.arcsoft.supervisor.cluster.net.TcpConnectionListener;
import com.arcsoft.supervisor.cluster.net.TcpServer;
import com.arcsoft.supervisor.cluster.node.LocalNode;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * The node service is responsible for receiving events or requests from cluster or nodes.
 * 
 * @author fjli
 * @author zw
 */
public class NodeService extends Service implements TcpConnectionListener {

	private static Logger log = Logger.getLogger(NodeService.class);
	protected LocalNode node;
	private TcpServer server;
	private ExecutorService threadPool;
	private NodeDescription desc;
	private final HashMap<String, HeartBeatSender> heartBeatSenders = new HashMap<>();
	private final HeartBeatSenderListener heartBeatSenderListener;

	/**
	 * Construct node service for the specified node.
	 * 
	 * @param node - the node this service belongs to.
	 */
	public NodeService(LocalNode node) {
		this(node, null);
	}

	/**
	 *
	 * Construct node service for the specified node and add the {@link HeartBeatSenderListener} to it.
	 *
	 * @param node the node this service belongs to
	 * @param heartBeatSenderListener the {@link HeartBeatSenderListener} for listen the {@link HeartBeatSender} of
	 *                                this node
	 */
	public NodeService(LocalNode node, HeartBeatSenderListener heartBeatSenderListener) {
		super(node.getCluster());
		this.node = node;
		server = new TcpServer();
		desc = node.getDescription();
		this.heartBeatSenderListener = heartBeatSenderListener;
	}


	/**
	 * Get the node description.
	 */
	public NodeDescription getNodeDescription() {
		return desc;
	}

	@Override
	public synchronized void start() throws IOException {
		// create thread pool for process connections.
		if (threadPool == null)
			threadPool = Executors.newCachedThreadPool(NamedThreadFactory.create("NodeService-" + this));

		// start server.
		server.addListener(this);
		if (desc.getPort() <= 0) {
			// if port is <= 0, then auto listen on random port.
			boolean created = false;
			for (int i = 0; i < 10; i++) {
				int port = (int) (Math.random() * 1000) + 5000;
				try {
					server.listen(desc.getIp(), port);
					log.info(node.getDescription().getName() + " start on port " + port);
					desc = new NodeDescription(desc.getType(),
                            desc.getId(),
                            desc.getName(),
                            desc.getIp(),
                            port,
                            desc.getNetmask(),
                            desc.getEth(),
                            desc.getGateway(),
                            desc.getFunctions());
					created = true;
					break;
				} catch (IOException e) {
					// ignore
				}
			}
			if (!created)
				throw new IOException("Start service failed.");
		} else {
			// otherwise, listen on the specified port.
			server.listen(desc.getIp(), desc.getPort());
			log.info(node.getDescription().getName() + " start on port " + desc.getPort());
		}
	}

	@Override
	public synchronized void stop() {
		// stop server.
		server.removeListener(this);
		server.close();

		// shutdown thread pool
		if (threadPool != null) {
			threadPool.shutdown();
			threadPool = null;
		}

		// stop heart beat sessions.
		synchronized (heartBeatSenders) {
			for (HeartBeatSender sender : heartBeatSenders.values()) {
				sender.stop();
			}
			heartBeatSenders.clear();
		}
	}

	@Override
	public void connectionCreated(final TcpConnection connection) {
		try {
			if (threadPool != null) {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						processConnection(connection);
					}
				});
			}
		} catch(Exception e) {
			// ignore all exceptions.
		}
	}

	/**
	 * Process connection.
	 * 
	 * @param connection - the new created connection.
	 */
	private void processConnection(TcpConnection connection) {
		try {
			// Read data pack and convert to object.
			DataPackage pack = connection.read();
			Object object = ConversionService.convert(pack);

			// If the received message aware the connection, set it.
			if (object instanceof ConnectionAware) {
				((ConnectionAware) object).setConnection(connection);
			}

			if (object instanceof Event) {
				if (object instanceof HeartBeatStartEvent) {
					processHeartBeatStartEvent((HeartBeatStartEvent) object, connection);
				} else if (object instanceof HeartBeatStopEvent) {
					processHeartBeatStopEvent((HeartBeatStopEvent) object);
				} else {
					// process event
					getCluster().processEvent((Event) object);
				}
				// send response event.
				DataPackage data = ConversionService.convert(new ResponseEvent());
				connection.write(data);
			} else if (object instanceof Request) {
				// process request
				DataPackage data = null;
				Request request = (Request) object;
				try {
					RequestHandler handler = node.getHandler(pack.getType(), pack.getSubType());
					if (handler == null)
						throw new ActionException(ErrorCode.ACTION_NOT_FOUND, "Cannot find the action for request " + request.toString());
					Response response = handler.execute(request);
					if (response == null)
						throw new ActionException(ErrorCode.NULL_RESPONSE, "Action returns null for request " + request.toString());
					try {
						data = ConversionService.convert(response);
					} catch(IOException e) {
						throw new ActionException(ErrorCode.CONVERT_RESPONSE_FAILED, "Convert response failed.", e);
					}
				} catch (ActionException e) {
					log.error("process request failed: " + e.getMessage(), e);
					data = ConversionService.convert(e);
				}
				connection.write(data);
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			connection.close();
		}
	}

	/**
	 * Process heart beat start event.
	 */
	private void processHeartBeatStartEvent(HeartBeatStartEvent event, TcpConnection connection) throws IOException {
		InetSocketAddress destAddr = new InetSocketAddress(connection.getRemoteIp(), event.getPort());
		HeartBeatSender oldSender;
		synchronized (heartBeatSenders) {
			HeartBeatSender newSender = new HeartBeatSender(node, this.heartBeatSenderListener);
			newSender.setLocalAddress(new InetSocketAddress(connection.getLocalIp(), 0));
			newSender.setRemoteAddress(destAddr);
			newSender.setInterval(event.getInterval());
			oldSender = heartBeatSenders.put(event.getNodeId(), newSender);
			if (oldSender != null)
				oldSender.stop();
			newSender.start();
		}
	}

	/**
	 * Process heart beat stop event.
	 */
	private void processHeartBeatStopEvent(HeartBeatStopEvent event) {
		synchronized (heartBeatSenders) {
			HeartBeatSender sender = heartBeatSenders.remove(event.getNodeId());
			if (sender != null) {
				sender.stop();
			}
		}
	}

}
