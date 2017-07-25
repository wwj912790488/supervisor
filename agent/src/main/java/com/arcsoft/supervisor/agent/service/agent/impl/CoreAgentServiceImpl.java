package com.arcsoft.supervisor.agent.service.agent.impl;

import com.arcsoft.supervisor.agent.service.agent.AgentComponentReporter;
import com.arcsoft.supervisor.agent.service.agent.AgentServer;
import com.arcsoft.supervisor.agent.service.agent.CoreAgent;
import com.arcsoft.supervisor.agent.service.agent.CoreAgentConfiguration;
import com.arcsoft.supervisor.agent.service.settings.SDIService;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.server.ListComponentRequest;
import com.arcsoft.supervisor.cluster.action.server.ListSDIRequest;
import com.arcsoft.supervisor.cluster.service.HeartBeatSenderListener;
import com.arcsoft.supervisor.model.domain.settings.Component;

import java.io.IOException;
import java.util.List;

/**
 * Core agent service.
 *
 * @author fjli
 */
public class CoreAgentServiceImpl extends BaseAgentService implements AgentComponentReporter {

    private CoreAgentConfiguration agentConfiguration;

    private SDIService sdiService;

    private HeartBeatSenderListener heartBeatSenderListener;

    @Override
    protected AgentServer createAgent() throws IOException {
        CoreAgentConfiguration config = new CoreAgentConfiguration();
        loadAgentConfig(config);
        config.setHeartBeatSenderListener(heartBeatSenderListener);
        this.agentConfiguration = config;
        return new CoreAgent(config);
    }


    @Override
    protected void onAddToCommander(boolean isFirstAdd) {
        super.onAddToCommander(isFirstAdd);
        if (sdiService != null) {
            sdiService.reportSDI();
        }
    }

    @Override
    protected void processNetworkError(boolean inputError, boolean outputError, boolean haError) {
        if (inputError || outputError || haError) {
            noitfyStopAllTasksAsync();
        }
    }

    @Override
    public CoreAgentConfiguration getAgentConfiguration() {
        return agentConfiguration;
    }

    public SDIService getSdiService() {
        return sdiService;
    }

    public void setSdiService(SDIService sdiService) {
        this.sdiService = sdiService;
    }

    @Override
    public void reportSDI(final List<String> sdiNames) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    log.info("start send sdi report.");
                    ListSDIRequest request = new ListSDIRequest();
                    request.setId(agent.getNode().getDescription().getId());
                    request.setSdis(sdiNames);
                    BaseResponse response = (BaseResponse) remoteExecute(request);
                    log.info("send sdi report end, ret=" + response.getErrorCode());
                } catch (Exception e) {
                    log.error("send sdi report failed.", e);
                }

            }
        });
    }

    @Override
    public void reportComponent(final List<Component> list) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    log.debug("start send component report of length " + list.size());
                    ListComponentRequest request = new ListComponentRequest();
                    request.setId(agent.getNode().getDescription().getId());
                    request.setComponents(list);
                    BaseResponse response = (BaseResponse) remoteExecute(request);
                    log.debug("send component report end, ret=" + response.getErrorCode());
                } catch (Exception e) {
                    log.error("send component report failed.", e);
                }

            }
        });
    }


    public HeartBeatSenderListener getHeartBeatSenderListener() {
        return heartBeatSenderListener;
    }

    public void setHeartBeatSenderListener(HeartBeatSenderListener heartBeatSenderListener) {
        this.heartBeatSenderListener = heartBeatSenderListener;
    }
}
