package com.arcsoft.supervisor.agent.service.agent;

import com.arcsoft.supervisor.cluster.ServerRole;
import com.arcsoft.supervisor.cluster.node.RemoteNode;

/**
 * Live agent is a server in the live cluster.
 *
 * @author fjli
 */
public class LiveAgent extends AgentServer {

	private RemoteNode target;
	private boolean bound;

	/**
	 * Construct new live agent with the specified configuration.
	 * 
	 * @param config - the specified configuration
	 */
	public LiveAgent(LiveAgentConfiguration config) {
		super(config);
	}

	/**
	 * Set binding node.
	 * 
	 * @param target the target to set
	 */
	public void setBindingNode(RemoteNode target) {
		this.target = target;
	}

	/**
	 * Return the server this server binding to.
	 */
	public RemoteNode getBindingNode() {
		return target;
	}

	/**
	 * Set bound status.
	 * 
	 * @param bound - the state to be set
	 */
	public void setBound(boolean bound) {
		this.bound = bound;
	}

	/**
	 * Indicate the agent is already bound or not.
	 */
	public boolean isBound() {
		return bound;
	}

	@Override
	public void resetAll() {
		setRole(ServerRole.ROLE_UNKNOWN);
		setBound(false);
		setBindingNode(null);
		super.resetAll();
	}

}
