package com.arcsoft.supervisor.cluster.node;

import com.arcsoft.supervisor.cluster.Cluster;
import com.arcsoft.supervisor.cluster.app.RequestHandler;
import com.arcsoft.supervisor.cluster.event.JoinEvent;
import com.arcsoft.supervisor.cluster.event.LeaveEvent;
import com.arcsoft.supervisor.cluster.service.HeartBeatSenderListener;
import com.arcsoft.supervisor.cluster.service.NodeService;

import java.io.IOException;
import java.util.HashMap;



/**
 * Local node is a node in this server. Local node has a service to receive event or request.
 * And it can join and leave the cluster.
 * 
 * @author fjli
 */
public class LocalNode extends AbstractNode {

	private HashMap<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();
	private boolean joinFlag = false;
	private NodeService service;

	/**
	 * Construct local node in the specified cluster.
	 * 
	 * @param cluster - the specified cluster
	 * @param desc - the description of node to be created
	 */
	public LocalNode(Cluster cluster, NodeDescription desc) {
		this(cluster, desc, null);
	}

	/**
	 * Construct local node.
	 *
	 * @param cluster the specified cluster
	 * @param desc the description of node to be created
	 * @param heartBeatSenderListener the {@link HeartBeatSenderListener} for the node
	 */
	public LocalNode(Cluster cluster, NodeDescription desc, HeartBeatSenderListener heartBeatSenderListener) {
		super(cluster, desc);
		if (desc.getId() == null || desc.getId().length() == 0)
			throw new IllegalArgumentException("Invalid node description: id not set.");
		if (desc.getName() == null || desc.getName().length() == 0)
			throw new IllegalArgumentException("Invalid node description: name not set.");
		if (cluster.isBroadcastSupported()) {
			String ipAddr = cluster.getDescription().getBindAddress();
			if (desc.getIp() != null && !desc.getIp().equals(ipAddr))
				throw new IllegalArgumentException("Invalid node description: ip address cannot set to the value other than cluster bind address.");
			if (desc.getIp() == null)
				this.desc = new NodeDescription(desc.getType(),
						desc.getId(),
						desc.getName(),
						ipAddr,
						desc.getPort(),
						desc.getNetmask(),
						desc.getEth(),
						desc.getGateway(),
						desc.getFunctions());
		}
		service = new NodeService(this, heartBeatSenderListener);
	}



	@Override
	public NodeDescription getDescription() {
		if (service != null)
			return service.getNodeDescription();
		return super.getDescription();
	}

	/**
	 * Join this node to cluster.
	 */
	public void join() throws IOException {
		// if node is already joined, do nothing.
		if (isJoin())
			return;
		// start service and send join event.
		service.start();
		joinFlag = true;
		if (cluster.isBroadcastSupported())
			cluster.broadcast(new JoinEvent(getDescription()));
	}

	/**
	 * Leave this node from cluster.
	 */
	public void leave() throws IOException {
		service.stop();
		joinFlag = false;
		if (cluster.isBroadcastSupported())
			cluster.broadcast(new LeaveEvent(getDescription().getId()));
	}

	/**
	 * Indicate the node is joined to cluster or not.
	 */
	public boolean isJoin() {
		return joinFlag;
	}

	/**
	 * Add request handler for processing the specified message type.
	 * 
	 * @param type - the message type
	 * @param subType - the message sub type
	 * @param handler - the handler to be added
	 */
	public void addHandler(int type, int subType, RequestHandler handler) {
		String key = type + "_" + subType;
		if (!handlers.containsKey(key))
			handlers.put(key, handler);
	}

	/**
	 * Remove the request handler for the specified message type.
	 * 
	 * @param type - the message type
	 * @param subType - the message sub type
	 */
	public void removeHandler(int type, int subType) {
		String key = type + "_" + subType;
		handlers.remove(key);
	}

	/**
	 * Returns the request handler for processing the specified message type.
	 * 
	 * @param type - the message type
	 * @param subType - the message sub type
	 */
	public RequestHandler getHandler(int type, int subType) {
		String key = type + "_" + subType;
		return handlers.get(key);
	}

}
