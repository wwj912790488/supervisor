package com.arcsoft.supervisor.cluster;

import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.ErrorCode;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.event.*;
import com.arcsoft.supervisor.cluster.net.ConnectOptions;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.net.MulticastClient;
import com.arcsoft.supervisor.cluster.net.TcpClient;
import com.arcsoft.supervisor.cluster.node.*;
import com.arcsoft.supervisor.cluster.service.ClusterService;
import com.arcsoft.supervisor.cluster.service.HeartBeatSenderListener;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a default implementation of the cluster.
 * 
 * @author fjli
 */
public class DefaultCluster extends Cluster {

	private Logger log = Logger.getLogger(DefaultCluster.class);
	private List<EventListener> listeners = new ArrayList<EventListener>();
	private List<Node> nodes = new ArrayList<Node>();
	private ClusterService service;

	public DefaultCluster(ClusterDescription desc) {
		super(desc);
		if (isBroadcastSupported())
			service = new ClusterService(this);
	}

	@Override
	public void start() throws IOException {
		if (isBroadcastSupported())
			service.start();
	}

	@Override
	public NodeSearcher createSeacher(final NodeListener listener) {
		if (!isBroadcastSupported())
			throw new UnsupportedOperationException();
		NodeSearcher searcher = new NodeSearcher(this) {
			@Override
			protected void processEvent(JoinEvent event) {
				RemoteNode remoteNode = createRemoteNode(event.getNode());
				if (remoteNode != null) {
					listener.nodeReceived(remoteNode);
				}
			}
		};
		return searcher;
	}

	@Override
	public void refresh(final int type) throws IOException {
		if (!isBroadcastSupported())
			throw new UnsupportedOperationException();
		// clear the nodes with the specified type.
		Collection<Node> toRemoved = getNodes(new NodeFilter() {
			@Override
			public boolean accept(Node node) {
				if (!(node instanceof RemoteNode))
					return false;
				if (type == Node.TYPE_SEARCH_ALL || type == node.getDescription().getType())
					return true;
				return false;
			}
		});
		for (Node node : toRemoved) {
			nodes.remove(node);
		}

		// start search.
		service.search(type);
	}

	@Override
	public Collection<Node> getNodes() {
		return nodes;
	}

	@Override
	public LocalNode createNode(NodeDescription desc) {
		return createNode(desc, null);
	}

	@Override
	public LocalNode createNode(NodeDescription desc, HeartBeatSenderListener heartBeatSenderListener) {
		LocalNode node = createLocalNode(desc, heartBeatSenderListener);
		if (node != null)
			nodes.add(node);
		return node;
	}

	@Override
	public void removeNode(LocalNode node) {
		if (nodes.contains(node)) {
			if (node.isJoin()) {
				try {
					node.leave();
				} catch (IOException e) {
					log.error("Send leave event fail!", e);
				}
			}
			nodes.remove(node);
		}
	}

	/**
	 * Create local node.
	 * 
	 * @param desc - the specified description of node
	 */
	protected LocalNode createLocalNode(NodeDescription desc, HeartBeatSenderListener heartBeatSenderListener) {
		return new LocalNode(this, desc, heartBeatSenderListener);
	}

	/**
	 * Create remote node.
	 * 
	 * @param desc - the specified description of node
	 */
	protected RemoteNode createRemoteNode(NodeDescription desc) {
		return new RemoteNode(this, desc);
	}

	@Override
	public Collection<Node> getNodes(NodeFilter filter) {
		if (filter == null)
			throw new NullPointerException("filter cannot be null.");
		ArrayList<Node> list = new ArrayList<Node>();
		for (Node node : nodes) {
			if (filter.accept(node))
				list.add(node);
		}
		return list;
	}

	@Override
	public Node getNode(String id) {
		for (Node node : nodes) {
			if (node.getDescription().getId().equals(id))
				return node;
		}
		return null;
	}

	@Override
	public void sendEvent(Event event, Node receiver, ConnectOptions options) throws IOException {
		// Set default options.
		if (options == null)
			options = new ConnectOptions();
		DataPackage pack = ConversionService.convert(event);
		TcpClient client = null;
		try {
			NodeDescription desc = receiver.getDescription();
			client = new TcpClient(desc.getIp(), desc.getPort(), options);
			client.write(pack);
			DataPackage resp_pack = client.read();
			Object resp = ConversionService.convert(resp_pack);
			if (!(resp instanceof ResponseEvent))
				throw new IOException("Invalid response data.");
		} finally {
			if (client != null)
				client.close();
		}
	}

	@Override
	public void sendEvent(Event event, Node receiver) throws IOException {
		sendEvent(event, receiver, null);
	}

	@Override
	public void broadcast(Event event) throws IOException {
		if (!isBroadcastSupported())
			throw new UnsupportedOperationException();
		service.broadcast(event);
	}

	@Override
	public Response execute(Request request, Node node, ConnectOptions options) throws ActionException {
		// Set default options.
		if (options == null)
			options = new ConnectOptions();
		// Convert request to data package.
		DataPackage pack;
		try {
			pack = ConversionService.convert(request);
		} catch (IOException e1) {
			throw new ActionException(ErrorCode.CONVERT_REQUEST_FAILED, "Convert request failed.", e1);
		}
		// Create connection, then send request and read response.
		TcpClient client = null;
		DataPackage result = null;
		NodeDescription desc = null;
		try {
			desc = node.getDescription();
			client = new TcpClient(desc.getIp(), desc.getPort(), options);
			client.write(pack);
			result = client.read();
		} catch (IOException e) {
			log.error("remote excute failed, requset = [" +request+"]");
			log.error("remote excute failed, target = [" +desc+"]");
			log.error("remote excute failed, pack = {}" +pack);
			throw new ActionException(ErrorCode.SEND_REQUEST_FAILED, "Send request failed.", e);
		} finally {
			if (client != null)
				client.close();
		}
		// Convert data package to response.
		Object object = null;
		try {
			object = ConversionService.convert(result);
			if (object instanceof ActionException)
				throw (ActionException) object;
			return (Response) object;
		} catch (IOException e1) {
			throw new ActionException(ErrorCode.CONVERT_RESPONSE_FAILED, "Convert response failed.", e1);
		}
	}

	@Override
	public Response execute(Request request, Node node) throws ActionException {
		return execute(request, node, null);
	}

	@Override
	public void addListener(EventListener listener) {
		if (listener != null && !listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify event to registered listeners.
	 * 
	 * @param event
	 */
	protected void notifyEvent(Event event) {
		for (EventListener listener : listeners) {
			listener.eventReceied(event);
		}
	}

	@Override
	public void processEvent(Event event) {
		if (event instanceof JoinEvent) {
			processJoinEvent((JoinEvent) event);
		} else if (event instanceof LeaveEvent) {
			processLeaveEvent((LeaveEvent) event);
		} else if (event instanceof SearchEvent) {
			processSearchEvent((SearchEvent) event);
		}
		notifyEvent(event);
	}

	/**
	 * Process search event.
	 * 
	 * @param searchEvent - the received search event.
	 */
	protected void processSearchEvent(SearchEvent searchEvent) {
		// If the search event comes from unknown target, do nothing.
		if (searchEvent.getFrom() == null)
			return;

		// Get local nodes matches the search condition.
		final int type = searchEvent.getType();
		Collection<Node> nodes = getNodes(new NodeFilter() {
			@Override
			public boolean accept(Node node) {
				if (!(node instanceof LocalNode))
					return false;
				if (type == Node.TYPE_SEARCH_ALL)
					return true;
				int nodeType = node.getDescription().getType();
				return (nodeType == type);
			}
		});

		// Send response event for each node.
		if (!nodes.isEmpty()) {
			MulticastClient client = null;
			try {
				client = new MulticastClient();
				for (Node node : nodes) {
					LocalNode localNode = (LocalNode) node;
					if (!localNode.isJoin())
						continue;
					try {
						JoinEvent event = new JoinEvent(node.getDescription());
						DataPackage pack = ConversionService.convert(event);
						client.send(searchEvent.getFrom(), pack);
					} catch (IOException e) {
						log.error("Send search response event failed.", e);
					}
				}
			} catch (IOException e) {
				log.error("Process search event failed.", e);
			} finally {
				if (client != null)
					client.close();
			}
		}
	}

	/**
	 * Process node join event.
	 * 
	 * @param event - the received join event
	 */
	protected void processJoinEvent(JoinEvent event) {
		Node node = getNode(event.getNode().getId());
		if (node == null) {
			RemoteNode remoteNode = createRemoteNode(event.getNode());
			if (remoteNode != null)
				nodes.add(remoteNode);
		}
	}

	/**
	 * Process node leave event.
	 * 
	 * @param event - the received leave event
	 */
	protected void processLeaveEvent(LeaveEvent event) {
		Node node = getNode(event.getNodeId());
		if (node != null && node instanceof RemoteNode) {
			nodes.remove(node);
		}
	}

	@Override
	public void close() {
		for (Node node : nodes) {
			if (node instanceof LocalNode) {
				LocalNode localNode = (LocalNode) node;
				try {
					if (localNode.isJoin())
						localNode.leave();
				} catch(Exception e) {
					log.error("Send leave event fail!", e);
				}
			}
		}
		if (service != null) {
			service.stop();
			service = null;
		}
		nodes.clear();
	}

}
