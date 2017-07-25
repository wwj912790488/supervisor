package com.arcsoft.supervisor.service.server.impl;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.server.ListSDIRequest;
import com.arcsoft.supervisor.cluster.action.server.ListSDIResponse;
import com.arcsoft.supervisor.cluster.action.server.StateReportRequest;
import com.arcsoft.supervisor.cluster.action.server.StateReportResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.cluster.node.RemoteNode;
import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.server.AccessDeniedException;
import com.arcsoft.supervisor.exception.server.ServerBusyException;
import com.arcsoft.supervisor.exception.server.ServerNotAvailableException;
import com.arcsoft.supervisor.exception.server.SwitchRoleFailedException;
import com.arcsoft.supervisor.model.domain.server.*;
import com.arcsoft.supervisor.model.vo.server.AgentDesc;
import com.arcsoft.supervisor.model.vo.server.ServerStateInfo;
import com.arcsoft.supervisor.repository.server.ServerComponentRepository;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.cluster.ClusterService;
import com.arcsoft.supervisor.service.server.ServerBindingService;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.server.event.ServerAliveChangedEvent;
import com.arcsoft.supervisor.service.server.event.ServerRemovedEvent;
import com.arcsoft.supervisor.service.server.event.ServerStateChangedEvent;
import com.arcsoft.supervisor.service.server.event.ServerTakeOverEvent;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The implementation class for ServerService.
 *
 * @author fjli
 */
@Service
public class ServerServiceImpl extends ServiceSupport implements ServerService, ActionHandler, TransactionSupport {

    private final Lock exclusiveLock;

    @Autowired
    private ServerJpaRepository serverRepository;

    @Autowired
    private ServerBindingService serverBindingService;
    
    @Autowired
    private ServerComponentRepository serverComponentRepository;

    @Autowired
    private ClusterService clusterService;

    public ServerServiceImpl() {
        this.exclusiveLock = new ReentrantLock();
    }

    @Override
    public Page<Server> list(int pageNo, int pageSize) {
        PageRequest request = new PageRequest(pageNo, pageSize);
        return serverRepository.findByJoinedTrue(request);
    }

    @Override
    public List<Server> getByIds(Collection<String> ids) {
        return serverRepository.findByIdIn(ids);
    }

    @Override
    public List<Server> getUnjoinedAgentServers() {
        return serverRepository.findByJoinedAndType(false, ServerType.AGENT.getValue());
    }

    @Override
    public List<Server> getJoinedAgentServers() {
        return serverRepository.findByJoinedAndType(true, ServerType.AGENT.getValue());
    }

//    @Override
//    public Server getServerWithIpAndPort(String ip, int port) {
//        return serverRepository.findByIpAndPort(ip, port);
//    }

    @Override
    public List<Server> findByJoinedTrueAndAliveTrueAndType(int type) {
        return serverRepository.findByJoinedTrueAndAliveTrueAndType(type);
    }
    
//    @Override
//    public Server findByJoinedTrueAndAliveTrueAndId(String id) {
//    	return serverRepository.findByJoinedTrueAndAliveTrueAndId(id);
//    }

    @Override
    public void addServer(Server server) {
        exclusiveLock.lock();
        try {
            if (getServer(server.getId()) == null) {
                serverRepository.addServer(server);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            exclusiveLock.unlock();
        }
    }

    @Override
    public void addServers(List<Server> servers) {
        for (Server server : servers) {
            addServer(server);
        }
    }

    @Override
    public List<Server> addAndScanServer() throws IOException {
        Enumeration<RemoteNode> nodes = clusterService.search(2000);
        List<Server> servers = new ArrayList<>();
        while (nodes.hasMoreElements()) {
            RemoteNode node = nodes.nextElement();
            Server server = new Server();
            server.setIp(node.getDescription().getIp());
            server.setPort(node.getDescription().getPort());
            server.setEth(node.getDescription().getEth());
            server.setNetmask(node.getDescription().getNetmask());
            server.setGateway(node.getDescription().getGateway());
            server.setId(node.getDescription().getId());
            server.setTypeFromEnum(ServerType.AGENT);
            server.setState(Server.STATE_FREE);
            server.setJoined(false);
            server.setFunctions(node.getDescription().getFunctions());
            servers.add(server);
        }
        for (Iterator<Server> iterator = servers.iterator(); iterator.hasNext(); ) {
            Server server = iterator.next();
            exclusiveLock.lock();
            try {
                AgentDesc agentDesc = serverBindingService.getAgentDesc(server);
                server.setGpus(agentDesc.getGpus());
            } catch (Exception e) {
                //The remote server is not available, remote it
                serverRepository.removeServer(server);
                iterator.remove();
            } finally {
                exclusiveLock.unlock();
            }
        }

        addServers(servers);

        List<Server> unJoinedServers = getUnjoinedAgentServers();
        for (Iterator<Server> iterator = unJoinedServers.iterator(); iterator.hasNext(); ) {
            Server unJoinServer = iterator.next();
            exclusiveLock.lock();
            try {
                serverBindingService.getAgentDesc(unJoinServer);
            } catch (Exception e) {
                //The remote server is not available, remote it
                serverRepository.removeServer(unJoinServer);
                iterator.remove();
            } finally {
                exclusiveLock.unlock();
            }
        }

        return unJoinedServers;
    }

    @Override
    public void updateToJoinAndSetNameAndRemarkAndActiveFunctions(Server server) {
        exclusiveLock.lock();
        try {
            Server persistServer = getServer(server.getId());
            if (persistServer != null && !persistServer.getJoined()) {
                persistServer.setName(server.getName());
                persistServer.setRemark(server.getRemark());
                persistServer.setAlive(true);
                persistServer.setJoined(true);
                persistServer.setActiveFunctions(server.getActiveFunctions());
                persistServer.setState(Server.STATE_FREE);
                serverBindingService.addAgent(persistServer);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            exclusiveLock.unlock();
        }
    }

    @Override
    public void updateNameAndRemarkAndActiveFunctions(Server server) {
        Server persistServer = getServer(server.getId());
        if (persistServer != null) {
            persistServer.setName(server.getName());
            persistServer.setRemark(server.getRemark());
            persistServer.setActiveFunctions(server.getActiveFunctions());
        }
    }


    @Override
    public List<Server> getJoinedAndAlivedAgentServersWithFunction(final ServerFunction function) {
        List<Server> agentServers = findByJoinedTrueAndAliveTrueAndType(ServerType.AGENT.getValue());
        return FluentIterable.from(agentServers).filter(new Predicate<Server>() {
            @Override
            public boolean apply(Server input) {
                return input.isInActiveFunctions(function);
            }
        }).toList();
    }

//    @Override
//    public void updateToJoin(String id) {
//        exclusiveLock.lock();
//        try {
//            Server server = getServer(id);
//            if (server != null) {
//                server.setJoined(true);
//                server.setAlive(true);
//                serverBindingService.addAgent(server);
//            }
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            exclusiveLock.unlock();
//        }
//    }


//    @Override
//    public void addServers(ServerGroup group) throws ObjectNotExistsException,
//            ObjectAlreadyExistsException, NameExistsException,
//            ServerNotAvailableException, RemoteException,
//            TooManyServerException, ServerIncompatibleException,
//            NoServerAvailableException {
//        // Skip empty servers.
//        List<Server> servers = group.getServers();
//        if (servers == null || servers.size() == 0)
//            return;
//
//        // Check the group is exist or not.
//        ServerGroup existGroup = getGroup(group.getId(), false);
//        if (existGroup == null)
//            throw new ObjectNotExistsException(group);
//
//        // The servers in 1+1 live group must less than or equals 2.
//        boolean isLiveGroup = (existGroup.getType() == ServerGroup.TYPE_LIVE);
//        if (isLiveGroup && servers.size() > 2)
//            throw new TooManyServerException();
//
//        // Get the exists servers.
//        List<Server> existServers = serverRepository.getServersInGroup(group.getId());
//
//        // The servers in 1+1 live group must less than or equals 2.
//        if (isLiveGroup) {
//            if (existServers.size() + servers.size() > 2)
//                throw new TooManyServerException();
//        }
//
//        // Save to database.
//        addServersToGroup(group);
//
//        if (isLiveGroup) {
//            // For the servers in 1+1 live group, bind each other first.
//            if (existServers.size() > 0) {
//                // The size must be 1 here.
//                Server master = existServers.get(0);
//                Server slave = servers.get(0);
//                serverBindingService.bind(master, slave);
//            } else if (servers.size() == 2) {
//                // If add two new servers.
//                boolean isSlave = servers.get(0).isBackup();
//                Server master = isSlave ? servers.get(1) : servers.get(0);
//                Server slave = isSlave ? servers.get(0) : servers.get(1);
//                serverBindingService.bind(master, slave);
//            }
//        } else if (existServers.size() + servers.size() > 1) {
//            // For the servers in m+n group, check the servers capabilities.
//            AgentDesc desc = null;
//            if (existServers.size() > 0) {
//                for (Server server : existServers) {
//                    try {
//                        desc = serverBindingService.getAgentDesc(server);
//                        break;
//                    } catch (ServerNotAvailableException e) {
//
//                    }
//                }
//                // If all servers are not available.
//                if (desc == null)
//                    throw new NoServerAvailableException();
//            }
//
//            // Check the capabilities is compatible or not.
//            for (Server server : servers) {
//                AgentDesc newDesc = serverBindingService.getAgentDesc(server);
//                if (desc == null) {
//                    desc = newDesc;
//                } else if (!desc.isCompatible(newDesc)) {
//                    throw new ServerIncompatibleException(server);
//                }
//            }
//        }
//
//        // For all servers, link with commander.
//        for (Server server : servers) {
//            serverBindingService.addAgent(server);
//        }
//
//        // Send server added event.
//        ServerGroup eventGroup = copy(existGroup);
//        eventGroup.setServers(copy(group.getServers(), group));
//        getEventManager().submit(new ServerAddedEvent(eventGroup));
//    }


    /**
     * Request the remote agent to remove from this commander.
     */
    private void removeAgent(Server server) {
        try {
            serverBindingService.removeAgent(server);
        } catch (ServerNotAvailableException e) {
            logger.warn("server is not available when remove agent " + server.getIp(), e);
        }
    }

    @Override
    public Server getServer(String id) {
        return serverRepository.getServer(id);
    }

    @Override
    public boolean isExistsServerName(String name) {
        return serverRepository.isExistsServerName(name);
    }

//    @Override
//    public void renameServer(Server server) throws ObjectNotExistsException, NameExistsException {
//        serverRepository.renameServer(server);
//    }

    @Override
    public void updateState(Server server) throws ObjectNotExistsException {
        serverRepository.updateState(server);

        // Send state change event.
        Server newServer = getServer(server.getId());
        if (newServer != null) {
            getEventManager().submit(new ServerStateChangedEvent(copy(newServer)));
        }
    }

//    @Override
//    public void updateAddress(Server server) throws ObjectNotExistsException {
//        serverRepository.updateAddress(server);
//    }

    @Override
    public void updateOnlineState(Server server) throws ObjectNotExistsException {
        serverRepository.updateOnlineState(server);

        // Send alive state change event.
        Server newServer = getServer(server.getId());
        if (newServer != null) {
            getEventManager().submit(new ServerAliveChangedEvent(copy(newServer)));
        }
    }


    @Override
    public void removeServer(Server server) throws ServerBusyException, SwitchRoleFailedException {
        // Check the server is exists or not, skip not exist server.
        Server newServer = null;
        exclusiveLock.lock();
        try {
            newServer = getServer(server.getId());
            if (newServer == null)
                return;
            // Remove from database.
            serverRepository.removeServer(server);

            // Remove link between the agent and commander.
            removeAgent(newServer);

        } catch (Exception e) {
            throw e;
        } finally {
            exclusiveLock.unlock();
        }

        if (newServer != null) {
            // Send server removed event.
            getEventManager().submit(new ServerRemovedEvent(copy(newServer)));
        }
    }

    @Override
    public void removeServers(List<Server> servers) {
        for (Server server : servers) {
            removeServer(server);
        }
    }

    @Override
    public void updateServersStatus() {
        serverRepository.resetServersStatus();
    }

    @Override
    public Server updateWorkerServer(String worker) throws ObjectNotExistsException, AccessDeniedException {
        // Get the worker server with the specified id.
        Server agent = getServer(worker);
        if (agent == null)
            throw new ObjectNotExistsException(agent);

        // Select a available backup server.
        List<Server> servers = getJoinedAgentServers();
//        List<Server> servers = serverRepository.listByType();
        for (Server server : servers) {
            // check the agent is available.
            if (!server.isAlive())
                continue;
            try {
                AgentDesc desc = serverBindingService.getAgentDesc(server);
                Map<String, Boolean> networkState = desc.getNetworkState();
                if (networkState.containsKey("input")) {
                    if (!networkState.get("input").booleanValue())
                        continue;
                }
                if (networkState.containsKey("output")) {
                    if (!networkState.get("output").booleanValue())
                        continue;
                }
            } catch (Exception e) {
                continue;
            }

            logger.info("The server " + server.getIp() + " is selected to take over the server " + agent.getIp());

            // Send take over event.
            Server oldServer = copy(agent);
            Server newServer = copy(server);
            getEventManager().submit(new ServerTakeOverEvent(oldServer, newServer));
            return server;
        }
        //Send take over event with newServer null.
        getEventManager().submit(new ServerTakeOverEvent(copy(agent), null));
        // No backup server is available.
        logger.info("NO backup server is available!");
        return null;
    }


    /**
     * Copy server and set the group.
     */
    private Server copy(Server server) {
        Server newServer = new Server();
        BeanUtils.copyProperties(server, newServer);
        return newServer;
    }

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.LIVE_ROLE_SWITCH_EVENT,
                Actions.STATE_REPORT,
                Actions.SDI_LIST
        };
    }

    @Override
    @Transactional
    public Response execute(Request request) throws ActionException {
        if (request instanceof StateReportRequest) {
            return processStateReport((StateReportRequest) request);
        }
        if (request instanceof ListSDIRequest) {
        	return processSDIList((ListSDIRequest) request);
        }
        return null;
    }


    /**
     * Process state report request.
     */
    private StateReportResponse processStateReport(StateReportRequest request) {
        ServerStateInfo stateInfo = request.getStateInfo();
        Map<String, Boolean> networkState = stateInfo.getNetworkState();
        logger.info("process state report: " + request.getId() + ", " + networkState);
        boolean hasError = false;
        if (networkState != null) {
            if ((networkState.containsKey("input") && !networkState.get("input"))
                    || (networkState.containsKey("output") && !networkState.get("output"))) {
                logger.info("process state report: hasError=true");
                hasError = true;
            }
        }
        Server server = new Server();
        server.setId(request.getId());
        server.setState(hasError ? Server.STATE_ERROR : Server.STATE_FREE);
        try {
            updateState(server);
        } catch (ObjectNotExistsException e) {
            logger.error("update state failed", e);
        }
        StateReportResponse response = new StateReportResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        return response;
    }

    /**
     * Process sdi slots report request.
     */
    private ListSDIResponse processSDIList(ListSDIRequest request) {
    	List<String> sdiNames = request.getSdis();
    	Server server = getServer(request.getId());
    	if(server != null && sdiNames != null) {
	    	for(String name : sdiNames) {
	    		if(serverComponentRepository.findByServerAndName(server, name) == null) {
		    		ServerComponent sdi = new ServerComponent();
		    		sdi.setType(ComponentType.SDI);
		    		sdi.setName(name);
		    		sdi.setServer(server);
		    		serverComponentRepository.save(sdi);
	    		}
	    	}
    	}
    	ListSDIResponse response = new ListSDIResponse();
    	return response;
    }
    
}
