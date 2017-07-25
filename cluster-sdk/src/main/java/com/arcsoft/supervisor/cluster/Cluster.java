package com.arcsoft.supervisor.cluster;

import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.cluster.event.Event;
import com.arcsoft.supervisor.cluster.event.EventListener;
import com.arcsoft.supervisor.cluster.net.ConnectOptions;
import com.arcsoft.supervisor.cluster.node.*;
import com.arcsoft.supervisor.cluster.service.HeartBeatSenderListener;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;

/**
 * This class represents a cluster. Cluster is responsible for maintain cluster nodes,
 * and process relation events.
 *
 * @author fjli
 */
public abstract class Cluster {

    private ClusterDescription desc;

    /**
     * Create instance of cluster.
     */
    public static Cluster createInstance(ClusterDescription desc) {
        String clusterClassName = System.getProperty(Cluster.class.getName());
        if (clusterClassName != null) {
            try {
                Class<?> clusterClass = Class.forName(clusterClassName);
                if (Cluster.class.isAssignableFrom(clusterClass)) {
                    Constructor<?> constructor = clusterClass.getConstructor(ClusterDescription.class);
                    return (Cluster) constructor.newInstance(desc);
                }
            } catch (Exception e) {
                Logger log = Logger.getLogger(Cluster.class);
                log.error(e.getMessage(), e);
            }
        }
        return new DefaultCluster(desc);
    }

    /**
     * Protected constructor that should not used by applications.
     *
     * @param desc
     */
    protected Cluster(ClusterDescription desc) {
        if (desc == null)
            throw new IllegalArgumentException("The cluster description is null.");
        if (desc != ClusterDescription.NO_BROAD_CAST) {
            if (desc.getIp() == null)
                throw new IllegalArgumentException("Invalid cluster description: ip is not set.");
            if (desc.getPort() <= 0)
                throw new IllegalArgumentException("Invalid cluster description: invalid port.");
            if (desc.getBindAddress() == null)
                throw new IllegalArgumentException("Invalid cluster description: bind ip is not set.");
        }
        this.desc = desc;
    }

    /**
     * Get description of this cluster.
     */
    public ClusterDescription getDescription() {
        return this.desc;
    }

    /**
     * Indicate this cluster supports broadcast or not.
     */
    public boolean isBroadcastSupported() {
        return desc != ClusterDescription.NO_BROAD_CAST;
    }

    /**
     * Start cluster.
     */
    public abstract void start() throws IOException;

    /**
     * Create node searcher.
     *
     * @param listener - the listener to receive node.
     * @return Returns new node searcher.
     */
    public abstract NodeSearcher createSeacher(NodeListener listener);

    /**
     * Refresh the cluster nodes.
     *
     * @param type - the node type to be refreshed.
     */
    public abstract void refresh(int type) throws IOException;

    /**
     * Create local node according to the specified node's description.
     *
     * @param desc - the specified description of node
     */
    public abstract LocalNode createNode(NodeDescription desc);

    /**
     * Create local node from specified desc and listener of {@link HeartBeatSenderListener}.
     *
     * @param desc the {@link NodeDescription} of node
     * @param heartBeatSenderListener the listener heartbeat sender
     * @return the instance of local node.
     */
    public abstract LocalNode createNode(NodeDescription desc, HeartBeatSenderListener heartBeatSenderListener);

    /**
     * Remote local node from cluster.
     *
     * @param node - the node to be removed
     */
    public abstract void removeNode(LocalNode node);

    /**
     * Returns all nodes which already joined in this cluster.
     */
    public abstract Collection<Node> getNodes();

    /**
     * Returns nodes which can be accepted by filter.
     *
     * @param filter - the filter
     * @return Returns nodes which can be accepted by filter.
     */
    public abstract Collection<Node> getNodes(NodeFilter filter);

    /**
     * Get cluster node according to the node id.
     * <p/>
     * NOTE: Only the node which joined the cluster can be found.
     *
     * @param id - the node id
     * @return Returns the node matches the node id, returns null if no node matches.
     */
    public abstract Node getNode(String id);

    /**
     * Send event to specified node.
     *
     * @param event    - the event to be sent
     * @param receiver - the event receiver
     * @param options  - the connect options
     */
    public abstract void sendEvent(Event event, Node receiver, ConnectOptions options) throws IOException;

    /**
     * Send event to specified node.
     *
     * @param event    - the event to be sent
     * @param receiver - the event receiver
     */
    public abstract void sendEvent(Event event, Node receiver) throws IOException;

    /**
     * Broadcast event to cluster network.
     *
     * @param event - the event to be broadcast
     */
    public abstract void broadcast(Event event) throws IOException;

    /**
     * Execute request on the specified node.
     *
     * @param request - the request to be executed
     * @param node    - the specified node
     * @param options - the connect options
     * @return Returns the response.
     * @throws ActionException - if dispatch or execute failed
     */
    public abstract Response execute(Request request, Node node, ConnectOptions options) throws ActionException;

    /**
     * Execute request on the specified node.
     *
     * @param request - the request to be executed
     * @param node    - the specified node
     * @return Returns the response.
     * @throws ActionException - if dispatch or execute failed
     */
    public abstract Response execute(Request request, Node node) throws ActionException;

    /**
     * Process events. All received events will pass through here.
     *
     * @param event - the received event
     */
    public abstract void processEvent(Event event);

    /**
     * Add event listener.
     *
     * @param listener - the listener to be added
     */
    public abstract void addListener(EventListener listener);

    /**
     * Remove event listener.
     *
     * @param listener - the listener to be removed
     */
    public abstract void removeListener(EventListener listener);

    /**
     * Close the cluster and release all resources.
     */
    public abstract void close();

}
