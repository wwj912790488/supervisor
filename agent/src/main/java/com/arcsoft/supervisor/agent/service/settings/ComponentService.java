package com.arcsoft.supervisor.agent.service.settings;

import com.arcsoft.supervisor.agent.service.agent.AgentComponentReporter;
import com.arcsoft.supervisor.model.domain.settings.Component;
import com.arcsoft.supervisor.service.settings.LocalComponentService;
import com.arcsoft.supervisor.utils.NamedThreadFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComponentService implements Runnable {

    private ScheduledExecutorService executor;
    private AgentComponentReporter reporter;
    private LocalComponentService localComponentService;
    private final Object lock = new Object();

    public AgentComponentReporter getReporter() {
        return reporter;
    }

    public void setReporter(AgentComponentReporter reporter) {
        this.reporter = reporter;
    }

    public LocalComponentService getLocalComponentService() {
        return localComponentService;
    }

    public void setLocalComponentService(LocalComponentService localComponentService) {
        this.localComponentService = localComponentService;
    }

    public void start() {
        synchronized (lock) {
            stopExecutor();
            executor = Executors.newSingleThreadScheduledExecutor(NamedThreadFactory.create("ComponentService"));
            executor.scheduleWithFixedDelay(this, 0, 5, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        synchronized (lock) {
            stopExecutor();
        }
    }

    private void stopExecutor() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public void run() {
        List<Component> list = localComponentService.list();
        reporter.reportComponent(list);
    }
}
