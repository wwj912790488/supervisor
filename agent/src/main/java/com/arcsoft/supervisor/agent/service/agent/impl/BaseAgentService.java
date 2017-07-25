package com.arcsoft.supervisor.agent.service.agent.impl;

import com.arcsoft.supervisor.agent.config.AppConfig;
import com.arcsoft.supervisor.agent.service.agent.AgentConfiguration;
import com.arcsoft.supervisor.agent.service.agent.AgentServer;
import com.arcsoft.supervisor.agent.service.agent.AgentService;
import com.arcsoft.supervisor.agent.service.remote.RemoteExecutorService;
import com.arcsoft.supervisor.agent.service.settings.EthSettingsListener;
import com.arcsoft.supervisor.agent.service.task.TaskManager;
import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.server.*;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.RequestHandler;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.cluster.net.NetworkStateListener;
import com.arcsoft.supervisor.cluster.net.NetworkStateMonitor;
import com.arcsoft.supervisor.cluster.node.RemoteNode;
import com.arcsoft.supervisor.model.vo.server.AgentDesc;
import com.arcsoft.supervisor.model.vo.server.AgentVersion;
import com.arcsoft.supervisor.model.vo.server.ServerStateInfo;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.NetworkHelper;
import com.arcsoft.supervisor.utils.StringHelper;
import com.arcsoft.supervisor.utils.SystemHelper;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This service maintains the base agent life cycle and the request handlers.
 * 
 * @author fjli
 */
public abstract class BaseAgentService implements AgentService, ActionHandler, RemoteExecutorService, NetworkStateListener, EthSettingsListener {

	protected static final String DEFAULT_CLUSTER_IP = "239.8.8.1";
	protected static final int DEFAULT_CLUSTER_PORT = 8901;
	protected static final int DEFAULT_SERVER_PORT = 5000;
	protected static final int DEFAULT_TIME_TO_LIVE = 1;

	protected Logger log = Logger.getLogger(getClass());
	private List<ActionHandler> actionHandlers;
	protected AgentServer agent;
	protected ExecutorService executor;
	private NetworkStateMonitor networkMonitor = new NetworkStateMonitor();
	private Map<String, Boolean> eths = new ConcurrentHashMap<>();
	private List<String> inputEths = new ArrayList<>();
	private List<String> outputEths = new ArrayList<>();
	private String clusterEth;
    private TaskManager taskManager;

	public BaseAgentService() {
		networkMonitor.addListener(this);
	}

	public void setActionHandlers(List<ActionHandler> actionHandlers) {
		this.actionHandlers = actionHandlers;
	}

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
	 * Load live agent configuration.
	 * 
	 * @throws java.io.IOException if load configuration failed.
	 */
	protected void loadAgentConfig(AgentConfiguration config) throws IOException {
		// set cluster IP
		String clusterIp = AppConfig.getString("cluster.ip", DEFAULT_CLUSTER_IP);
		config.setClusterIp(clusterIp);

		// set cluster port
		int port = AppConfig.getInt("cluster.port", DEFAULT_CLUSTER_PORT);
		config.setClusterPort(port);

		// set cluster bind address.
		String bind = AppConfig.getString("cluster.bind");
		String ip = null;
		if (StringHelper.isBlank(bind)) {
			log.warn("clusster.bind is not set, use local ip.");
			ip = NetworkHelper.getLocalIp();
		} else if (bind.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
			ip = bind;
		} else {
			ip = NetworkHelper.getHostAddress(NetworkInterface.getByName(bind));
		}
		if (ip == null) {
			log.error("Cannot get bind address from " + bind);
			throw new IOException("Cannot get bind address from " + bind);
		}
		config.setBindAddr(ip);

		// set cluster ttl.
		int timeToLive = AppConfig.getInt("cluster.ttl", DEFAULT_TIME_TO_LIVE);
		config.setTimeToLive(timeToLive);

		// set server id
		String serverId = AppConfig.getString("server.id");
		if (serverId == null)
			serverId = SystemHelper.os.getSystemUUID();
		config.setServerId(serverId);

		// set server name
		String name = AppConfig.getString("server.name");
		if (name == null)
			name = SystemHelper.os.getHostName();
		config.setServerName(name);

		// set server port
		port = AppConfig.getInt("server.port", DEFAULT_SERVER_PORT);
		config.setServerPort(port);

        String functions = AppConfig.getString("cluster.functions");
        if (StringUtils.isNotBlank(functions)){
            config.setFunctions(functions);
        }

	}

	protected abstract AgentServer createAgent() throws IOException;

	/**
	 * Initialize live agent service.
	 * 
	 * @throws java.io.IOException if load configuration failed, or start agent failed.
	 */
	public void init() throws IOException {
		// load configuration and create agent.
		agent = createAgent();

		// get cluster network interface name
		NetworkInterface netif = NetworkHelper.getInterfaceByHostAddr(agent.getCluster().getDescription().getBindAddress());
		if (netif != null) {
			clusterEth = netif.getName();
			networkMonitor.startMonitor(clusterEth);
		}

		// register action handlers.
		registerActions(this);
		if (actionHandlers != null) {
			for (ActionHandler handler : actionHandlers)
				registerActions(handler);
		}

		// start agent.
		try {
			agent.start();
		} catch(IOException e) {
			log.error("Start agent failed.", e);
			agent.stop();
			throw e;
		}

        taskManager.init(agent.getNode().getDescription().getFunctions());

        // create thread pool
        executor = Executors.newCachedThreadPool(NamedThreadFactory.create("AgentService"));

		// start network monitor
		networkMonitor.start();
	}

	/**
	 * Destroy agent service.
	 */
	public void destroy() {
		// stop network monitor
		networkMonitor.stop();
		eths.clear();
		inputEths.clear();
		outputEths.clear();
		taskManager.destroy();

		onBeforeDestroy();

		// stop agent.
		if (agent != null) {
			agent.stop();
			agent = null;
		}


		// shutdown thread pool.
		if (executor != null) {
			executor.shutdown();
            try {
                if (executor.awaitTermination(5, TimeUnit.SECONDS)){
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor = null;
		}
        log.info("Destroy agent service complete.");
	}

    /**
     * Extend method to do some destroy operator before agent destroy
     */
    protected void onBeforeDestroy(){

    }

	@Override
	public AgentServer getAgent() {
		return agent;
	}

	/**
	 * Returns all live agent actions.
	 */
	@Override
	public int[] getActions() {
		return new int[] {
				Actions.GET_AGENT_DESC,
				Actions.ADD_AGENT,
				Actions.REMOVE_AGENT,
			};
	}

	/**
	 * Receive agent relation requests, and dispatch request to process methods.
	 * 
	 * @param request - the received request
	 * @return returns the response
	 * @throws ActionException if process request failed.
	 */
	@Override
	public Response execute(Request request) throws ActionException {
		if (request instanceof GetAgentDescRequest) {
			return getAgentDesc();
		} else if (request instanceof AddAgentRequest) {
			return addAgent((AddAgentRequest) request);
		} else if (request instanceof RemoveAgentRequest) {
			return removeAgent();
		}
		return null;
	}

	/**
	 * Send the specified request to the commander this agent added to.
	 * 
	 * @param request - the specified request
	 * @return returns the response
	 * @throws ActionException if process request failed.
	 */
	@Override
	public Response remoteExecute(Request request) throws ActionException {
		RemoteNode target = agent.getCommander();
		return agent.execute(request, target);
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
				agent.addHandler(action, proxy);
			}
		}
	}

	/**
	 * Action intercepter, all actions will pass through here.
	 * 
	 * @param request - the request to be processed
	 */
	protected Response actionInterceptor(Request request, ActionHandler handler) throws ActionException {
		log.debug("Request received: " + request.getClass().getName());
		return handler.execute(request);
	}

	/**
	 * Get agent description.
	 * 
	 * @return returns response describing this agent.
	 */
	private GetAgentDescResponse getAgentDesc() {
		GetAgentDescResponse response = new GetAgentDescResponse();
		response.setErrorCode(ActionErrorCode.SUCCESS);
		AgentDesc desc = new AgentDesc();
		desc.setNetworkState(getNetworkState());
		desc.setGpus(getGpuCores());
//		desc.setVersion(getVersions());
		response.setAgentDesc(desc);
		return response;
	}

	/**
	 * Add agent to the specified commander.
	 * 
	 * @param request - the add agent request
	 * @return returns response indicating the action is success or not.
	 */
	protected AddAgentResponse addAgent(AddAgentRequest request) {
		boolean isFirstAdd = agent.getCommander() == null;
		RemoteNode commander = agent.createRemoteNode(request.getCommander(),getGpuCores());
		agent.setCommander(commander);
		agent.setServerType(request.getAgentType());

		onAddToCommander(isFirstAdd);
		AddAgentResponse response = new AddAgentResponse();
		response.setErrorCode(ActionErrorCode.SUCCESS);
		response.setGpus(getGpuCores());
		return response;
	}

	protected void onAddToCommander(boolean isFirstAdd) {
		// Report agent state.
		reportAgentState();
		// report agent capabilities.
	}

	/**
	 * Remove agent from the commander.
	 *
	 * @return returns response indicating the action is success or not.
	 */
	protected RemoveAgentResponse removeAgent() {
		onRemoveFromCommander();
		agent.resetAll();
		RemoveAgentResponse response = new RemoveAgentResponse();
		response.setErrorCode(ActionErrorCode.SUCCESS);
		return response;
	}

	protected void onRemoveFromCommander() {
	}

	/**
	 * Async notify stop all tasks.
	 */
	protected void noitfyStopAllTasksAsync() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@Override
	public void ethStateChanged(String eth, boolean active) {
		boolean inputError = false;
		boolean outputError = false;
		boolean haError = false;
		log.debug("eth state changed, eth: " + eth + ", active: " + active);

		// update network states map.
		synchronized (eths) {
			eths.put(eth, active);
			if (!active) {
				if (inputEths.contains(eth))
					inputError = isAllNetworkInactive(inputEths);
				if (outputEths.contains(eth))
					outputError = isAllNetworkInactive(outputEths);
				if (clusterEth != null && clusterEth.equals(eth))
					haError = true;
			}
		}

		// Report network error.
		if (inputError || outputError)
			reportError(ActionErrorCode.NETWORK_ERROR_DETECTED);

		// If input or output network state changed, report the agent state.
		if (inputEths.contains(eth) || outputEths.contains(eth)) {
			reportAgentState();
		}

		// process network error.
		processNetworkError(inputError, outputError, haError);
	}

	/**
	 * Process network error.
	 * 
	 * @param inputError - if true, indicate all input networks are inactive
	 * @param outputError - if true, indicate all output networks are inactive
	 * @param haError - if true, indicate the cluster network is inactive
	 */
	protected void processNetworkError(boolean inputError, boolean outputError, boolean haError) {
	}

	/**
	 * Test whether all the specified network interfaces are inactive.
	 * 
	 * @param networks - the specified network interfaces
	 * @return returns true if all network interfaces are inactive.
	 */
	private boolean isAllNetworkInactive(List<String> networks) {
		for (String eth : networks) {
			if (eths.get(eth))
				return false;
		}
		return true;
	}

	/**
	 * Test the network has error.
	 */
	protected boolean hasNetworkError() {
		if (!inputEths.isEmpty() && isAllNetworkInactive(inputEths))
			return true;
		if (!outputEths.isEmpty() && isAllNetworkInactive(outputEths))
			return true;
		return false;
	}

	@Override
	public void ethSettingsChanged(Properties settings) {
		String input = settings.getProperty("input");
		String[] inEths = input == null || input.isEmpty() ? new String[0] : input.split(",");
		String output = settings.getProperty("output");
		String[] outEths = output == null || output.isEmpty() ? new String[0] : output.split(",");
		List<String> newEths = new ArrayList<>();
		synchronized (eths) {
			log.debug("network settings changed, input: " + input + ", output: " + output);
			inputEths.clear();
			outputEths.clear();
			for (String s : inEths) {
				inputEths.add(s);
				if (!newEths.contains(s))
					newEths.add(s);
			}
			for (String s : outEths) {
				outputEths.add(s);
				if (!newEths.contains(s))
					newEths.add(s);
			}
			for (String s : eths.keySet()) {
				if (!newEths.contains(s)) {
					eths.remove(s);
					// don't stop cluster network monitor
					if (!s.equals(clusterEth))
						networkMonitor.stopMonitor(s);
				}
			}
			newEths.removeAll(eths.keySet());
			for (String s : newEths) {
				eths.put(s, true);
				networkMonitor.startMonitor(s);
			}
		}
	}

	/**
	 * Get current network states.
	 */
	private Map<String, Boolean> getNetworkState() {
		Map<String, Boolean> networkMap = new HashMap<>();
		if (!inputEths.isEmpty())
			networkMap.put("input", !isAllNetworkInactive(inputEths));
		if (!outputEths.isEmpty())
			networkMap.put("output", !isAllNetworkInactive(outputEths));
		log.debug("get network state: " + networkMap);
		return networkMap;
	}

	private Integer getGpuCores() {
		try {
			List<String> gpus = App.runShell("nvidia-smi -L");
			return gpus.size();
		} catch (ShellException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Notify error event to commander.
	 */
	protected void reportError(final int errorCode) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					log.info("start send error report.");
					ErrorReportRequest request = new ErrorReportRequest();
					request.setId(agent.getNode().getDescription().getId());
					request.setErrorCode(errorCode);
					BaseResponse response = (BaseResponse) remoteExecute(request);
					log.info("send error report end, ret=" + response.getErrorCode());
				} catch (ActionException e) {
					log.error("send error report failed.", e);
				}
			}
		});
	}

	/**
	 * Report agent state to commander.
	 */
	protected void reportAgentState() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					log.info("start send state report.");
					StateReportRequest request = new StateReportRequest();
					request.setId(agent.getNode().getDescription().getId());
					ServerStateInfo stateInfo = new ServerStateInfo();
					stateInfo.setNetworkState(getNetworkState());
					request.setStateInfo(stateInfo);
					BaseResponse response = (BaseResponse) remoteExecute(request);
					log.info("send state report end, ret=" + response.getErrorCode());
				} catch (ActionException e) {
					log.error("send state report failed.", e);
				}
			}
		});
	}

	/**
	 * Get all versions.
	 */
	private AgentVersion getVersions() {
		AgentVersion version = new AgentVersion();
		version.setAgentVersion(getAgentVersion());
		return version;
	}

	/**
	 * Get agent version.
	 */
	private String getAgentVersion() {
		InputStream inStream = null;
		try {
			inStream = getClass().getResourceAsStream("/META-INF/maven/com.arcsoft/supervisor-agent/pom.properties");
			Properties p = new Properties();
			p.load(inStream);
			return p.getProperty("version");
		} catch (IOException e1) {
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

}
