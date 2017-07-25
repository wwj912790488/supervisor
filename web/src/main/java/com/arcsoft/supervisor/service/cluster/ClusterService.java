package com.arcsoft.supervisor.service.cluster;

import com.arcsoft.supervisor.cluster.node.RemoteNode;

import java.io.IOException;
import java.util.Enumeration;



/**
 * Cluster service.
 * 
 * @author fjli
 */
public interface ClusterService {

	/**
	 * Search servers in cluster. Stop search if cannot find more server within the specified timeout.
	 * 
	 * @param timeout - the max waiting time since last server found.
	 * @return Enumeration of servers.
	 * @throws java.io.IOException - if cannot create search session.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 */
	Enumeration<RemoteNode> search(long timeout) throws IOException;


}
