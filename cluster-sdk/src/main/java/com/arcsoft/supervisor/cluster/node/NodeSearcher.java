package com.arcsoft.supervisor.cluster.node;

import com.arcsoft.supervisor.cluster.Cluster;
import com.arcsoft.supervisor.cluster.ClusterDescription;
import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.event.JoinEvent;
import com.arcsoft.supervisor.cluster.event.SearchEvent;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.net.MulticastClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;



/**
 * Search nodes in cluster.
 * 
 * @author fjli
 */
public abstract class NodeSearcher implements Runnable {

	private MulticastClient client;
	private Thread thread;
	private boolean stop;
	private SocketAddress groupAddr;
	private String bindAddr;

	/**
	 * Construct searcher in the specified cluster.
	 * 
	 * @param cluster - the specified cluster
	 */
	public NodeSearcher(Cluster cluster) {
		ClusterDescription desc = cluster.getDescription();
		this.groupAddr = new InetSocketAddress(desc.getIp(), desc.getPort());
		this.bindAddr = desc.getBindAddress();
	}

	/**
	 * Start search.
	 * 
	 * @param type - the search type.
	 * @throws java.io.IOException
	 */
	public synchronized void start(int type) throws IOException {
		stop = false;
		SearchEvent event = new SearchEvent(type);
		DataPackage pack = ConversionService.convert(event);
		client = new MulticastClient(null);
		if (bindAddr != null)
			client.bind(new InetSocketAddress(bindAddr, 0));
		else
			client.bind(new InetSocketAddress(0));
		client.send(groupAddr, pack);
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Stop search.
	 */
	public synchronized void stop() {
		stop = true;
		if (client != null) {
			client.close();
			client = null;
		}
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			thread = null;
		}
	}

	/**
	 * Process received events.
	 * 
	 * @param event - the received events.
	 */
	protected abstract void processEvent(JoinEvent event);

	/**
	 * Do loop for receiving events.
	 */
	public void run() {
		while (!stop) {
			try {
				DataPackage pack = client.receive();
				if (pack != null) {
					Object object = ConversionService.convert(pack);
					if (object instanceof JoinEvent) {
						processEvent((JoinEvent) object);
					}
				}
			} catch(IOException e) {
				break;
			}
		}
	}

}
