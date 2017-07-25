package com.arcsoft.supervisor.cluster.service;

import com.arcsoft.supervisor.cluster.Cluster;
import com.arcsoft.supervisor.cluster.ClusterDescription;
import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.event.Event;
import com.arcsoft.supervisor.cluster.event.JoinEvent;
import com.arcsoft.supervisor.cluster.event.SearchEvent;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.net.EventHandler;
import com.arcsoft.supervisor.cluster.net.MulticastServer;
import com.arcsoft.supervisor.cluster.node.NodeSearcher;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * The node service is responsible for receiving events from cluster.
 * 
 * @author fjli
 */
public class ClusterService extends Service {

	private MulticastServer server;
	private NodeSearcher searcher;
	private static final Logger log = Logger.getLogger(ClusterService.class);

	/**
	 * Construct new cluster service for the specified cluster.
	 * 
	 * @param cluster - the specified cluster.
	 */
	public ClusterService(Cluster cluster) {
		super(cluster);
		server = new MulticastServer();
		searcher = new NodeSearcher(cluster) {
			@Override
			protected void processEvent(JoinEvent event) {
				getCluster().processEvent(event);
			}
		};
	}

	@Override
	public void start() throws IOException {
		ClusterDescription desc = cluster.getDescription();
		server.init(desc.getIp(), desc.getPort(), desc.getBindAddress(), desc.getTimeToLive(), 5, new EventHandler() {
			public void process(SocketAddress from, DataPackage pack) {
				processPack(from, pack);
			}
		});
	}

	/**
	 * Search the specified type nodes in this cluster.
	 * 
	 * @param type - the specified type
	 * @throws java.io.IOException
	 */
	public void search(int type) throws IOException {
		searcher.stop();
		searcher.start(type);
	}

	/**
	 * Broadcast event to all cluster members.
	 * 
	 * @param event - the event to be broadcast.
	 * @throws java.io.IOException - if convert failed or send event failed
	 */
	public void broadcast(Event event) throws IOException {
		DataPackage pack = ConversionService.convert(event);
		server.send(pack);
	}

	@Override
	public void stop() {
		searcher.stop();
		try {
			if (server != null)
				server.uninit();
		} catch (IOException e) {
			log.error("Stop service failed.", e);
		}
	}

	/**
	 * Process data package received from cluster.
	 * 
	 * @param from - the sender of the received data package
	 * @param pack - the received data package
	 */
	private void processPack(SocketAddress from, DataPackage pack) {
		// convert data package to object.
		Object object = null;
		try {
			object = ConversionService.convert(pack);
		} catch (IOException e) {
			log.error("invalid package received.", e);
			return;
		}

		// process event.
		if (object instanceof Event) {
			Event event = (Event) object;
			// if it is search event, construct new search event with client address.
			if (event instanceof SearchEvent)
				event = new SearchEvent(from, ((SearchEvent) event).getType());
			// process event.
			cluster.processEvent(event);
		} else {
			log.error("Unsupport object received: " + object.getClass().getName());
		}
	}

}
