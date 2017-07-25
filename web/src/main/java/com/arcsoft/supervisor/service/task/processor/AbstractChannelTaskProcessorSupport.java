package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zw.
 */
public abstract class AbstractChannelTaskProcessorSupport extends AbstractTaskProcessorSupport {

    @Autowired
    private ChannelRepository channelRepository;

    @Override
    protected BaseResponse start(Task task, Server server) {
        return start(channelRepository.findOne(task.getReferenceId()), server, task.getId());
    }

    protected abstract BaseResponse start(Channel channel, Server server, int taskId);
}
