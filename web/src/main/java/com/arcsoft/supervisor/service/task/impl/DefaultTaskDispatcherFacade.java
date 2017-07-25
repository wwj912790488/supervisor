package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.commons.spring.SessionTemplate;
import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.commons.lock.TaskRecordLock;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Defaults implementation of <code>TaskDispatcherFacade</code> for dispatch all of tasks.
 *
 * @author zw.
 */
@Service
@Production
public class DefaultTaskDispatcherFacade extends AbstractTaskDispatcherSupport {

    @Autowired
    public DefaultTaskDispatcherFacade(
            TaskExecutor taskExecutor,
            ScreenService screenService,
            TaskService taskService,
            ChannelService channelService,
            TaskRecordLock taskRecordLock,
            SessionTemplate sessionTemplate) {
        super(taskExecutor, screenService, taskService, channelService, taskRecordLock, sessionTemplate);
    }
}
