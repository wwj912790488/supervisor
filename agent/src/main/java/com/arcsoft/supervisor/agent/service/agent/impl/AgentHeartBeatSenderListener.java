package com.arcsoft.supervisor.agent.service.agent.impl;

import com.arcsoft.supervisor.agent.service.settings.ComponentService;
import com.arcsoft.supervisor.cluster.service.HeartBeatSenderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The listener implementation for heartbeat sender of agent node.
 *
 * @author zw.
 */
public class AgentHeartBeatSenderListener implements HeartBeatSenderListener {

    private ComponentService componentService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onStart() {
        log.info("HeartBeatSenderListener on start.");
        componentService.start();
    }

    @Override
    public void onStop() {
        log.info("HeartBeatSenderListener on stop.");
        componentService.stop();
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }
}
