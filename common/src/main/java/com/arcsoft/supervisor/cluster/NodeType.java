package com.arcsoft.supervisor.cluster;

/**
 * Define node types.
 * 
 * @author fjli
 */
public interface NodeType {

	/**
	 * Indicate the node is a commander.
	 */
	public static final int TYPE_COMMANDER = 2;

	/**
	 * Indicate the node is a live agent.
	 */
	public static final int TYPE_LIVE = 3;

	/**
	 * Indicate this node is a core agent.
	 */
	public static final int TYPE_CORE = 4;

}
