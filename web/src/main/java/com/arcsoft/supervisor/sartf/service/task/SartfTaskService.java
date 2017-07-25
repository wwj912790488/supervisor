package com.arcsoft.supervisor.sartf.service.task;

import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.service.task.TaskService;


public interface SartfTaskService extends TaskService {

    void updateTaskStatus(int taskId, TaskStatus fromStatus, TaskStatus toStatus);

    boolean isUserRelatedTaskHasOutput(int userId);

    void switchAudioByChannel(int composeTaskId, int videoSettingId, int channelId);

}
