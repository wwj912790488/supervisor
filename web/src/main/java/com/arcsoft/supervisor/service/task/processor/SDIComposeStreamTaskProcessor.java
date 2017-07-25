package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import org.springframework.stereotype.Service;

@Service("sdiComposeStreamTaskProcessor")
public class SDIComposeStreamTaskProcessor extends AbstractComposeTaskProcessorSupport {


    @Override
    protected BaseResponse start(Task task, Server server) {
        Screen screen = screenRepository.findOne(task.getReferenceId());
        ComposeTaskAndResponse composeTaskAndResponse = startComposeTask(task, server, screen);
        screen.setAddress(screen.getWallPosition().getSdiOutput().getName());
        return composeTaskAndResponse.getResponse();
    }

    @Override
    protected Server getServer(Task task) {
        return serverRepository.findByJoinedTrueAndAliveTrueAndId(task.getServerId());
    }
}
