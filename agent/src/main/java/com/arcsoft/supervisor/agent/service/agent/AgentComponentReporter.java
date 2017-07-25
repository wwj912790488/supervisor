package com.arcsoft.supervisor.agent.service.agent;

import com.arcsoft.supervisor.model.domain.settings.Component;

import java.util.List;

public interface AgentComponentReporter {
	public void reportSDI(List<String> sdiNames);
	public void reportComponent(List<Component> list);
}
