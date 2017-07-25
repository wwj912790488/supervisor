package com.arcsoft.supervisor.agent.service.agent;

import com.arcsoft.supervisor.cluster.Configuration;

/**
 * Agent configuration.
 *
 * @author fjli
 */
public abstract class AgentConfiguration extends Configuration {

    /**
     * Checks the <code>function</code> is in {@link com.arcsoft.supervisor.cluster.Configuration#functions}
     * or not.
     *
     * @param function the specified function to be checks
     * @return <code>true</code> the <code>function</code> is in {@link com.arcsoft.supervisor.cluster.Configuration#functions}
     * otherwise false
     */
    public boolean isSupportFunction(String function) {
        return getFunctions().contains(function);
    }

}
