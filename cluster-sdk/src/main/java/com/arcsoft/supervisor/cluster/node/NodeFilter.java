package com.arcsoft.supervisor.cluster.node;


/**
 * Instances of concrete classes that extend <code>NodeFilter</code>
 * are passed to the Cluster.getNodes methods to allow an applications
 * to set a filter on the list of nodes.
 * 
 * @author fjli
 */
public interface NodeFilter {

	/**
	 * Test if a specified node should be included in the collection.
	 * 
	 * @param node - the specified node to test.
	 * @return true if the node should be listed, false otherwise.
	 */
	public boolean accept(Node node);

}
