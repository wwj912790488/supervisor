package com.arcsoft.supervisor.service.cluster.impl;

import com.arcsoft.supervisor.cluster.ClusterServer;
import com.arcsoft.supervisor.cluster.ClusterType;
import com.arcsoft.supervisor.cluster.NodeType;
import com.arcsoft.supervisor.cluster.ServerType;
import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.ErrorCode;
import com.arcsoft.supervisor.cluster.net.ConnectOptions;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import com.arcsoft.supervisor.cluster.node.NodeListener;
import com.arcsoft.supervisor.cluster.node.NodeSearcher;
import com.arcsoft.supervisor.cluster.node.RemoteNode;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.exception.server.ServerNotAvailableException;
import com.arcsoft.supervisor.model.domain.server.Server;

import java.io.IOException;



/**
 * Create commander server.
 * 
 * @author fjli
 */
public class CommanderServer extends ClusterServer {

	/**
	 * Construct a commander server with the specified configuration.
	 * 
	 * @param config - the specified configuration.
	 */
	public CommanderServer(CommanderConfiguration config) {
		super(config);
		setServerType(ServerType.TYPE_COMMANDER);
	}

	/**
	 * Create remote node from server instance.
	 * 
	 * @param agent - the server instance.
	 * @return the remote node.
	 */
	public RemoteNode createRemoteNode(Server agent) {
		NodeDescription desc = new NodeDescription(agent.getType(),
				agent.getId(), agent.getName(), agent.getIp(), agent.getPort());
		return createRemoteNode(desc);
	}

	/**
	 * Test the specified node type is valid or not.
	 * 
	 * @param nodeType - the specified node type
	 * @return true if it is valid.
	 */
	public boolean isValidNodeType(int nodeType) {
		switch(config.getClusterType()) {
		case ClusterType.CORE:
			return nodeType == NodeType.TYPE_CORE;
		case ClusterType.LIVE:
			return nodeType == NodeType.TYPE_LIVE;
		default:
			return false;
		}
	}

	/**
	 * Search the agents in the cluster this server belongs to.
	 * 
	 * @param listener - the node listener
	 * @return the instance of searcher.
	 * @throws java.io.IOException
	 */
	public NodeSearcher searchAgents(NodeListener listener) throws IOException {
		NodeSearcher searcher = cluster.createSeacher(listener);
		switch(config.getClusterType()) {
		case ClusterType.CORE:
			searcher.start(NodeType.TYPE_CORE);
			break;
		case ClusterType.LIVE:
			searcher.start(NodeType.TYPE_LIVE);
			break;
		}
		return searcher;
	}

	/**
	 * Execute request on the specified agent.
	 * 
	 * @param request - the request to be executed
	 * @param agent - the specified agent
	 * @param connectTimeout - the connection timeout
	 * @param readTimeout - the read timeout
	 * @return Returns the response.
	 * @throws ServerNotAvailableException if the agent server is not available.
	 * @throws RemoteException if invoke failed
	 */
	public BaseResponse execute(BaseRequest request, Server agent, int connectTimeout, int readTimeout) throws ServerNotAvailableException, RemoteException {
		RemoteNode target = createRemoteNode(agent);
		try {
			ConnectOptions options = new ConnectOptions();
			options.setString(ConnectOptions.OPTION_BIND_ADDR, getNode().getDescription().getIp());
			options.setInt(ConnectOptions.OPTION_CONNECT_TIMEOUT, connectTimeout);
			options.setInt(ConnectOptions.OPTION_READ_TIMEOUT, readTimeout);
			BaseResponse response = (BaseResponse) cluster.execute(request, target, options);
			if (!response.isSuccess())
				writeActionErrorLog(request, agent, response.getErrorCode(), null);
			return response;
		} catch (ActionException e) {
			writeActionErrorLog(request, agent, e.getErrorCode(), e.getCause());
			if (e.getErrorCode() == ErrorCode.SEND_REQUEST_FAILED)
				throw new ServerNotAvailableException(agent);
			else
				throw new RemoteException(agent);
		}
	}

	/**
	 * Execute request on the specified agent.
	 * 
	 * @param request - the request to be executed
	 * @param agent - the specified agent
	 * @return Returns the response.
	 * @throws ServerNotAvailableException if the agent server is not available.
	 * @throws RemoteException if invoke failed
	 */
	public BaseResponse execute(BaseRequest request, Server agent) throws ServerNotAvailableException, RemoteException {
		return execute(request, agent, 5000, 5000);
	}

	/**
	 * Write action error log.
	 * 
	 * @param request - the request
	 * @param agent - the specified agent
	 * @param errorCode - the error code
	 * @param cause the cause of exception
	 */
	private void writeActionErrorLog(BaseRequest request, Server agent, int errorCode, Throwable cause) {
		String format = "execute action failed, return code: 0x%08x, request: %s, target: %s:%d";
		String name = request.getClass().getName();
		log.error(String.format(format, errorCode, name, agent.getIp(), agent.getPort()), cause);
	}

}
