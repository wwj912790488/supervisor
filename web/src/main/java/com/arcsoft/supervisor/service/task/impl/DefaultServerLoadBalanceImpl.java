package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerFunction;
import com.arcsoft.supervisor.model.domain.server.ServerType;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.ServerLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zw.
 */
@Service
public class DefaultServerLoadBalanceImpl implements ServerLoadBalance {

    @Autowired
    private ServerService serverService;
    @Autowired
    private TaskRepository taskRepository;

    private final Object lock = new Object();

    @Override
    public Server getServer(ServerType type, ServerFunction function) {
        synchronized (lock) {
            return calcLowerUsedServer(
                    findSameFunctionServers(
                            serverService.findByJoinedTrueAndAliveTrueAndType(
                                    ServerType.AGENT.getValue()
                            ),
                            function)
            );
        }
    }

    private Server calcLowerUsedServer(List<Server> sameFunctionServers) {
        Map<Server, Long> serverIdPair = new HashMap<>();
        long low = -1;
        for (Server server : sameFunctionServers) {
            long useCount = taskRepository.countByServerId(server.getId());
            low = low == -1 ? useCount : (low > useCount ? useCount : low);
            serverIdPair.put(server, useCount);
        }
        if (low != -1) {
            for (Map.Entry<Server, Long> entry : serverIdPair.entrySet()) {
                if (low == entry.getValue()) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private List<Server> findSameFunctionServers(List<Server> joinedAndAliveServers, ServerFunction function) {
        List<Server> sameFunctionServers = new ArrayList<>();
        for (Server server : joinedAndAliveServers) {
            if (server.getActiveFunctionsAsEnum().contains(function)) {
                sameFunctionServers.add(server);
            }
        }
        return sameFunctionServers;
    }


}
