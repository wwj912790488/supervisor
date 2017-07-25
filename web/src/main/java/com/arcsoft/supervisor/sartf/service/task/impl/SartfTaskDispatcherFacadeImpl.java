package com.arcsoft.supervisor.sartf.service.task.impl;

import com.arcsoft.supervisor.commons.spring.SessionTemplate;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.sartf.service.commons.lock.SartfTaskRecordLock;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskDispatcherFacade;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskService;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.impl.AbstractTaskDispatcherSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Sartf
public class SartfTaskDispatcherFacadeImpl extends AbstractTaskDispatcherSupport implements SartfTaskDispatcherFacade {

    private final SartfTaskRecordLock sartfTaskRecordLock;

    private final SartfTaskService sartfTaskService;

    @Autowired
    public SartfTaskDispatcherFacadeImpl(
            TaskExecutor taskExecutor,
            ScreenService screenService,
            SartfTaskService sartfTaskService,
            ChannelService channelService,
            SartfTaskRecordLock sartfTaskRecordLock,
            SessionTemplate sessionTemplate) {
        super(taskExecutor, screenService, sartfTaskService, channelService, sartfTaskRecordLock, sessionTemplate);
        this.sartfTaskRecordLock = sartfTaskRecordLock;
        this.sartfTaskService = sartfTaskService;
    }

    @Override
    public void startUserTask(int userId) {
        sartfTaskRecordLock.acquireUserLock(userId);
        try {
            startUserCurrentConfig(userId);
        } finally {
            sartfTaskRecordLock.releaseUserLock(userId);
        }
    }

    @Override
    public void stopUserTask(int userId) {
        sartfTaskRecordLock.acquireUserLock(userId);
        try {
            stopUserCurrentConfig(userId);
        } finally {
            sartfTaskRecordLock.releaseUserLock(userId);
        }
    }

    @Override
    public void switchAudioChannel(int userId, int cell_index) {
        Task userTask = sartfTaskService.createOrGetTask(userId, TaskType.USER_RELATED_COMPOSE);
        if(userTask.isStatusEqual(TaskStatus.RUNNING)) {
            sartfTaskService.switchAudioByChannel(userTask.getId(), 1, cell_index);
        }
    }

    private void startUserCurrentConfig(int userId) {
        Task userTask = sartfTaskService.createOrGetTask(userId, TaskType.USER_RELATED_COMPOSE);
        sartfTaskService.updateTaskStatus(userTask.getId(), TaskStatus.RUNNING, TaskStatus.UPDATING);
        getTaskExecutor().start(userTask.getId());
        waitForStatus(userTask.getId(), TaskStatus.RUNNING);
    }

    private void stopUserCurrentConfig(int userId) {
        Task userTask = sartfTaskService.createOrGetTask(userId, TaskType.USER_RELATED_COMPOSE);
        if (userTask.isStatusEqual(TaskStatus.RUNNING)) {
            getTaskExecutor().stop(userTask.getId());
            waitForStatus(userTask.getId(), TaskStatus.STOP);
        }
    }

}
