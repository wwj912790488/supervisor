package com.arcsoft.supervisor.cluster;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.RequestHandler;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.converter.RequestDataConverter;
import com.arcsoft.supervisor.cluster.converter.ResponseDataConverter;
import com.arcsoft.supervisor.cluster.node.LocalNode;
import com.arcsoft.supervisor.cluster.node.Node;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import com.arcsoft.supervisor.cluster.node.RemoteNode;
import com.arcsoft.supervisor.utils.NetworkHelper;
import com.arcsoft.supervisor.utils.SystemHelper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Cluster server.
 * 
 * @author fjli
 */
public class ClusterServer {

	protected final Logger log = Logger.getLogger(getClass());
	protected Configuration config;
	protected Cluster cluster;
	protected LocalNode node;
	protected int role = ServerRole.ROLE_UNKNOWN;
	protected int serverType = ServerType.TYPE_UNKNOWN;

	/**
	 * Construct a ClusterServer with the specified configuration.
	 * 
	 * @param config - the specified configuration
	 */
	public ClusterServer(Configuration config) {
		this.config = config;
		createCluster();
		createNode();
		registerConverters();
		addHandlers();
	}

	/**
	 * Create cluster.
	 */
	private void createCluster() {
		String ip = config.getClusterIp();
		int port = config.getClusterPort();
		String bindAddr = config.getBindAddr();
		int ttl = config.getTimeToLive();
		ClusterDescription desc = new ClusterDescription(ip, port, bindAddr, ttl);
		cluster = Cluster.createInstance(desc);
	}

	/**
	 * Create node.
	 */
	private void createNode() {
        String netmask = NetworkHelper.getNetmaskWithIp(config.getBindAddr());
		String eth = "eth0";
		try{
			eth = NetworkHelper.getInterfaceByHostAddr(config.getBindAddr()).getName();
		}catch (Exception e){
			eth = "eth0";
			log.info("ip address changed, use default.");
		}
        String gateway = null;
        try {
            gateway = SystemHelper.net.getGatewayWithIp(config.getBindAddr());
        } catch (IOException e) {
            //TODO: process exception
            e.printStackTrace();
        }
		NodeDescription agentDesc = new NodeDescription(
				config.getServerType(),
				config.getServerId(),
				config.getServerName(),
                config.getBindAddr(),
                config.getServerPort(),
                netmask,
                eth,
                gateway,
                config.getFunctions());
		node = cluster.createNode(agentDesc, config.getHeartBeatSenderListener());
	}

	/**
	 * Register converters.
	 */
	protected void registerConverters() {
		ConversionService.addConverter(new RequestDataConverter());
		ConversionService.addConverter(new ResponseDataConverter());
	}

	/**
	 * Add handlers.
	 */
	protected void addHandlers() {
		
	}

	/**
	 * Return the cluster this server belongs to.
	 */
	public Cluster getCluster() {
		return cluster;
	}

	/**
	 * Returns the local node.
	 */
	public LocalNode getNode() {
		return node;
	}

	/**
	 * Returns the server type.
	 */
	public int getServerType() {
		return serverType;
	}

	/**
	 * Set the server type.
	 * 
	 * @param serverType - the server type
	 */
	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	/**
	 * Returns the server role.
	 */
	public int getRole() {
		return role;
	}

	/**
	 * Set the server role.
	 * 
	 * @param role - the server role.
	 */
	public void setRole(int role) {
		this.role = role;
	}

	/**
	 * Create remote node with the specified node info.
	 * 
	 * @param info - the specified node info
	 * @return the created remote node.
	 */
	public RemoteNode createRemoteNode(RemoteNodeInfo info,Integer gpus) {
		int type = Node.TYPE_DEFAULT;
		switch(config.getClusterType()) {
		case ClusterType.LIVE:
			type = NodeType.TYPE_LIVE;
			break;
		case ClusterType.CORE:
			type = NodeType.TYPE_CORE;
			break;
		default:
			return null;
		}
		NodeDescription desc = new NodeDescription(type, info.getId(), null,
				info.getIp(), info.getPort(),gpus);
		return createRemoteNode(desc);
	}

	/**
	 * Create remote node with the specified node info.
	 * 
	 * @param desc - the specified node description
	 * @return the created remote node.
	 */
	public RemoteNode createRemoteNode(NodeDescription desc) {
		return new RemoteNode(cluster, desc);
	}

	/**
	 * Execute request on the specified node.
	 * 
	 * @param request - the request to be executed
	 * @param target - the specified node
	 * @return Returns the response.
	 * @throws ActionException - if dispatch or execute failed
	 */
	public Response execute(Request request, Node target) throws ActionException {
		return cluster.execute(request, target);
	}

	/**
	 * Add action handler.
	 * 
	 * @param action - the action id
	 * @param handler - the request handler
	 */
	public void addHandler(int action, RequestHandler handler) {
		node.addHandler(Actions.TYPE_REQUEST, action, handler);
	}

	/**
	 * Start server.
	 */
	public void start() throws IOException {
		log.info("start server with config: " + config);
		cluster.start();
		node.join();
	}

	/**
	 * Stop server.
	 */
	public void stop() {
		cluster.close();
	}

}
