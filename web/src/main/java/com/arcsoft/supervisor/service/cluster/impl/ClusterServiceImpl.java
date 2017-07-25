package com.arcsoft.supervisor.service.cluster.impl;

import com.arcsoft.supervisor.cluster.ClusterType;
import com.arcsoft.supervisor.cluster.RemoteNodeInfo;
import com.arcsoft.supervisor.cluster.action.*;
import com.arcsoft.supervisor.cluster.action.server.*;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.RequestHandler;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.cluster.heartbeat.HeartBeatSession;
import com.arcsoft.supervisor.cluster.heartbeat.HeartBeatSessionEvent;
import com.arcsoft.supervisor.cluster.heartbeat.HeartBeatSessionListener;
import com.arcsoft.supervisor.cluster.heartbeat.HeartBeatSessionTimeoutEvent;
import com.arcsoft.supervisor.cluster.net.ConnectOptions;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import com.arcsoft.supervisor.cluster.node.NodeListener;
import com.arcsoft.supervisor.cluster.node.NodeSearcher;
import com.arcsoft.supervisor.cluster.node.RemoteNode;
import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.server.AccessDeniedException;
import com.arcsoft.supervisor.exception.server.HeatBeatEventNotReceivedException;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.exception.server.ServerNotAvailableException;
import com.arcsoft.supervisor.exception.system.SystemNotInitializedException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.vo.server.AgentDesc;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.cluster.ClusterService;
import com.arcsoft.supervisor.service.remote.RemoteExecutorService;
import com.arcsoft.supervisor.service.server.ServerBindingService;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.system.SystemContextListener;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.SystemHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for cluster.
 *
 * @author fjli
 */
@Service
public class ClusterServiceImpl extends ServiceSupport implements ClusterService,
        RemoteExecutorService, ServerBindingService, ActionHandler, SystemContextListener {
    private CommanderServer server;
    @Autowired
    private ServerService serverService;
    private Hashtable<String, HeartBeatSession> heartBeatSessions = new Hashtable<>();
    private ScheduledExecutorService executor;
    @Autowired
    private List<ActionHandler> actionHandlers;
    private ExecutorService queueExecutor;

    @Override
    public synchronized void contextInit() {
        logger.info("initialize cluster service.");

        // create configuration from system settings.
        int clusterType = systemSettings.getClusterType();
        CommanderConfiguration config = new CommanderConfiguration(clusterType);
        config.setClusterIp(systemSettings.getClusterIp());
        config.setClusterPort(systemSettings.getClusterPort());
        config.setServerId(SystemHelper.os.getSystemUUID());
        config.setServerName(SystemHelper.os.getHostName());
        config.setBindAddr(systemSettings.getBindAddr());
        config.setTimeToLive(systemSettings.getTimeToLive());

        // create commander server and start it.
        server = new CommanderServer(config);

        // register action handlers.
        registerActions(this);
        if (actionHandlers != null) {
            for (ActionHandler handler : actionHandlers)
                registerActions(handler);
        }

        // start commander.
        try {
            server.start();
            logger.info("cluster service started.");
        } catch (IOException e) {
            logger.error("start cluster service failed.", e);
        }

        // start queue executor
        queueExecutor = Executors.newSingleThreadExecutor(NamedThreadFactory.create("ClusterService:queueExecutor"));

        // reconnect all agents.
        reconnectAgents();
    }

    @Override
    public synchronized void contextDestory() {
        logger.info("destroy cluster service.");

        // shutdown executor.
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }

        stopHeartBeatSessions();

        if (server != null) {
            server.stop();
            server = null;
        }

        // shutdown queue executor
        if (queueExecutor != null) {
            queueExecutor.shutdown();
            try {
                queueExecutor.awaitTermination(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Register all actions with the specified handler.
     *
     * @param handler - the specified handler
     */
    private void registerActions(final ActionHandler handler) {
        int[] actions = handler.getActions();
        if (actions != null && actions.length > 0) {
            RequestHandler proxy = new RequestHandler() {
                @Override
                public Response execute(Request request) throws ActionException {
                    return actionInterceptor(request, handler);
                }
            };
            for (int action : actions) {
                server.addHandler(action, proxy);
            }
        }
    }

    /**
     * Action intercepter, all actions will pass through here.
     *
     * @param request - the request to be processed
     */
    private Response actionInterceptor(Request request, ActionHandler handler) throws ActionException {
        return handler.execute(request);
    }

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.ERROR_REPORT,
        };
    }

    @Override
    public Response execute(Request request) throws ActionException {
        if (request instanceof ErrorReportRequest) {
            return processErrorReport((ErrorReportRequest) request);
        }
        return null;
    }

    /**
     * Process error report request.
     */
    private ErrorReportResponse processErrorReport(ErrorReportRequest request) {
        int errorCode = request.getErrorCode();
        if (errorCode == ActionErrorCode.NETWORK_ERROR_DETECTED) {
            logger.error("network error dectected for the agent " + request.getId());
            updateWorkerServer(request.getId());
        }
        ErrorReportResponse response = new ErrorReportResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        return response;
    }

    private void updateWorkerServer(final String serverId) {
        try {
            final ExecutorService executor = this.queueExecutor;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (executor.isShutdown())
                        return;
                    try {
                        serverService.updateWorkerServer(serverId);
                    } catch (ObjectNotExistsException e) {
                        logger.warn("switch role failed: server not exist. serverId=" + serverId);
                    } catch (AccessDeniedException e) {
                        logger.warn("switch role failed: it is a slave or 1+1 server. serverId=" + serverId);
                    } catch (Exception e) {
                        logger.error("switch role failed: " + e.getMessage(), e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("switch role failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Enumeration<RemoteNode> search(final long timeout) throws IOException {
        if (server == null)
            throw new SystemNotInitializedException();
        final Object lock = new Object();
        final LinkedList<RemoteNode> list = new LinkedList<>();
        final NodeSearcher searcher = server.searchAgents(new NodeListener() {
            @Override
            public void nodeReceived(RemoteNode node) {
                // ensure the node is valid.
                NodeDescription desc = node.getDescription();
                if (desc == null || desc.getId() == null)
                    return;
                // filter the same node.
                String nodeId = desc.getId();
                for (RemoteNode existNode : list) {
                    if (nodeId.equals(existNode.getDescription().getId()))
                        return;
                }
                // add new node to the list.
                list.add(node);
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        return new Enumeration<RemoteNode>() {
            private void waitForNext() {
                synchronized (lock) {
                    try {
                        lock.wait(timeout);
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public boolean hasMoreElements() {
                if (!list.isEmpty())
                    return true;
                waitForNext();
                if (!list.isEmpty())
                    return true;
                searcher.stop();
                return false;
            }

            @Override
            public RemoteNode nextElement() {
                if (!list.isEmpty())
                    return list.removeFirst();
                waitForNext();
                if (!list.isEmpty())
                    return list.removeFirst();
                searcher.stop();
                return null;
            }
        };
    }

    /**
     * Reconnect all agents when commander restart.
     */
    private void reconnectAgents() {
        // reset all servers status to no connected.
        serverService.updateServersStatus();

        // connect each server in the database.
        executor = Executors.newScheduledThreadPool(1, NamedThreadFactory.create("ClusterService:executor"));

        List<Server> servers = serverService.getJoinedAgentServers();
        if (servers != null && !servers.isEmpty()) {
            for (final Server server : servers) {
                final ScheduledExecutorService executor = this.executor;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (executor.isShutdown())
                            return;
                        addAgentWithRetry(server);
                    }
                });
            }
        }

        logger.info("reconnect agents finished!");
    }

    /**
     * Add agent to this commander. If add failed, retry it later.
     *
     * @param server - the specified agent server
     */
    private void addAgentWithRetry(Server server) {
        try {
            AddAgentResponse addAgentResponse=(AddAgentResponse)addAgent(server);
            updateOnlineState(server.getId(), true,addAgentResponse.getGpus());
        } catch (Exception e) {
            logger.error("Cannot add agent " + server.getIp() + " to this commander.", e);
            retryLater(server.getId());
        }
    }

    /**
     * Retry connection to server.
     *
     * @param serverId - the specified server id
     */
    private void retryLater(final String serverId) {
        logger.debug("Test agent " + serverId + " is available 5s later.");
        try {
            final ScheduledExecutorService executor = this.executor;
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    if (executor.isShutdown())
                        return;
                    Server server = serverService.getServer(serverId);
                    if (server != null) {
                        addAgentWithRetry(server);
                    } else {
                        logger.debug("The server " + serverId + " is already removed, stop retry.");
                    }
                }
            }, 5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("schedule retry failed.", e);
        }
    }

    /**
     * Update online state.
     *
     * @param nodeId - the node id
     * @param alive  - alive state
     */
    private void updateOnlineState(String nodeId, boolean alive) {
        try {
            Server server = new Server();
            server.setId(nodeId);
            server.setAlive(alive);
            serverService.updateOnlineState(server);
        } catch (ObjectNotExistsException e) {
        }
    }



    private void updateOnlineState(String nodeId, boolean alive,Integer gpus) {
        try {
            Server server = new Server();
            server.setId(nodeId);
            server.setAlive(alive);
            server.setGpus(gpus);
            serverService.updateOnlineState(server);
        } catch (ObjectNotExistsException e) {
        }
    }

    /**
     * Create heart beat session for live agent.
     *
     * @param agent - the target node
     * @return created heart beat session.
     */
    private HeartBeatSession createLiveHeartBeatSession(Server agent) {
        RemoteNode target = server.createRemoteNode(agent);
        HeartBeatSession session = new HeartBeatSession(server.getNode(), target);
        ConnectOptions options = new ConnectOptions();
        options.setString(ConnectOptions.OPTION_BIND_ADDR, server.getNode().getDescription().getIp());
        options.setInt(ConnectOptions.OPTION_CONNECT_TIMEOUT, 1000);
        options.setInt(ConnectOptions.OPTION_READ_TIMEOUT, 2000);
        session.setConnectOptions(options);
        session.setInterval(systemSettings.getHeartbeatInterval());
        session.setTimeout(systemSettings.getHeartbeatTimeout());
        return session;
    }

    /**
     * Stop heart beat of the specified server.
     *
     * @param nodeId - the specified server id
     */
    private void stopHeartBeat(String nodeId) {
        HeartBeatSession session = heartBeatSessions.remove(nodeId);
        if (session != null) {
            session.stop();
            logger.debug("heart beat session stopped, nodeId=" + nodeId);
        }
    }

    /**
     * Stop all heart beat sessions.
     */
    private void stopHeartBeatSessions() {
        Enumeration<String> keys = heartBeatSessions.keys();
        while (keys.hasMoreElements()) {
            stopHeartBeat(keys.nextElement());
        }
    }

    /**
     * Update the server state when the heart beat session timeout.
     *
     * @param event - the timeout event
     */
    private void processHeartBeatTimeoutEvent(HeartBeatSessionTimeoutEvent event) {
        HeartBeatSession session = event.getSession();
        NodeDescription desc = session.getTarget().getDescription();
        String nodeId = desc.getId();
        logger.error("heart beat session timeout, ip=" + desc.getIp());
        heartBeatSessions.remove(nodeId);
        updateOnlineState(nodeId, false);
        retryLater(nodeId);
        //Select a slave service to take over the master server.
        updateWorkerServer(nodeId);
    }

    @Override
    public AgentDesc getAgentDesc(Server agent) {
        GetAgentDescRequest request = new GetAgentDescRequest();
        GetAgentDescResponse response = (GetAgentDescResponse) remoteExecute(request, agent);
        if (!response.isSuccess())
            throw new RemoteException(agent);
        return response.getAgentDesc();
    }

    @Override
    public BaseResponse addAgent(Server agent) {
        if (server == null)
            throw new SystemNotInitializedException();

        // create heart beat session.
        HeartBeatSession session = createLiveHeartBeatSession(agent);
        heartBeatSessions.put(agent.getId(), session);
        try {
            session.start();
        } catch (IOException e) {
            stopHeartBeat(agent.getId());
            throw new RemoteException(agent);
        }

        // create add agent request.
        AddAgentRequest request = new AddAgentRequest();
        RemoteNodeInfo commander = new RemoteNodeInfo();
        NodeDescription desc = server.getNode().getDescription();
        commander.setId(desc.getId());
        commander.setIp(desc.getIp());
        commander.setPort(desc.getPort());
        request.setCommander(commander);

        // send add agent request to the agent.
        BaseResponse baseResponse=null;
        try {
           baseResponse= remoteExecute(request, agent);
        } catch (RuntimeException e) {
            stopHeartBeat(agent.getId());
            throw e;
        }

        try {
            // NOTE: to avoid process timeout loggeric if the agent has problem on heart beat at adding agent period or
            // retry connection period.
            if (session.getInterval() <= 5000) {
                long maxWaitingTime = Math.min(session.getTimeout(), 5000);
                if (session.waitForEvent(maxWaitingTime)) {
                    session.addListener(new HeartBeatSessionListener() {
                        @Override
                        public void sessionEventReceived(HeartBeatSessionEvent event) {
                            if (event instanceof HeartBeatSessionTimeoutEvent) {
                                processHeartBeatTimeoutEvent((HeartBeatSessionTimeoutEvent) event);
                            }
                        }
                    });
                } else {
                    stopHeartBeat(agent.getId());
                    throw new HeatBeatEventNotReceivedException(agent);
                }
            } else {
                logger.debug("The heart beat interval is too larger, so skip check heart beat event.");
            }
        } catch (InterruptedException e) {
            stopHeartBeat(agent.getId());
            throw new RemoteException(agent);
        }
        return baseResponse;
    }

    @Override
    public void removeAgent(Server agent) {
        // stop heart beat session
        stopHeartBeat(agent.getId());

        // send remove agent request to the agent.
        RemoveAgentRequest request = new RemoveAgentRequest();
        remoteExecute(request, agent);
    }

    @Override
    public void bind(Server master, Server slave) {
        if (systemSettings.getClusterType() != ClusterType.LIVE)
            throw new UnsupportedOperationException();
        RemoteNodeInfo slaveInfo = new RemoteNodeInfo();
        slaveInfo.setId(slave.getId());
        slaveInfo.setIp(slave.getIp());
        slaveInfo.setPort(slave.getPort());
        GroupBindRequest request = new GroupBindRequest();
        request.setSlave(slaveInfo);
        GroupBindResponse response = (GroupBindResponse) remoteExecute(request, master);
        if (!response.isSuccess()) {
            switch (response.getErrorCode()) {
                case ActionErrorCode.SLAVE_NOT_AVAILABLE:
                    throw new ServerNotAvailableException(slave);
                default:
                    throw new RemoteException(master);
            }
        }
    }

    @Override
    public void unbind(Server agent) {
        if (systemSettings.getClusterType() != ClusterType.LIVE)
            throw new UnsupportedOperationException();
        UnbindRequest request = new UnbindRequest();
        UnbindResponse response = (UnbindResponse) remoteExecute(request, agent);
        if (!response.isSuccess())
            throw new RemoteException(agent);
    }

    @Override
    public void switchRole(Server master) {
        if (systemSettings.getClusterType() != ClusterType.LIVE)
            throw new UnsupportedOperationException();
        SwitchRoleRequest request = new SwitchRoleRequest();
        request.setReason(RoleSwitchReason.REASON_USER);
        SwitchRoleResponse response = (SwitchRoleResponse) remoteExecute(request, master);
        if (!response.isSuccess())
            throw new RemoteException(master);
    }

    @Override
    public BaseResponse remoteExecute(BaseRequest request, Server agent) {
        if (server == null)
            throw new SystemNotInitializedException();
        return server.execute(request, agent);
    }

    @Override
    public BaseResponse remoteExecute(BaseRequest request, Server agent, int connectTimeout, int readTimeout) {
        if (server == null)
            throw new SystemNotInitializedException();
        return server.execute(request, agent, connectTimeout, readTimeout);
    }
}
