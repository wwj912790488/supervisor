package com.arcsoft.supervisor.agent.service.task.converter;

import com.arcsoft.supervisor.agent.service.task.TaskStateChangeFactory;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;

/**
 * Implementation used in {@code sartf} profile for converter staus of task.
 *
 * @author zw.
 */
public class SartfTaskStateChangeFactory implements TaskStateChangeFactory {

    /**
     *
     * @param taskId the id of task
     * @param status the status of task.
     * @return the converted TaskStateChange or {@code null} if
     * we don't care others status of task
     */
    @Override
    public TaskStateChange create(int taskId, TaskStatus status, ITranscodingTracker tracker) {
        com.arcsoft.supervisor.model.vo.task.TaskStatus convertedStatus = null;
        switch (status) {
            case READY:
                convertedStatus = com.arcsoft.supervisor.model.vo.task.TaskStatus.READY;
                break;
            case RUNNING:
                convertedStatus = com.arcsoft.supervisor.model.vo.task.TaskStatus.RUNNING;
                break;
            case CANCELLED:
            case COMPLETED:
                convertedStatus = com.arcsoft.supervisor.model.vo.task.TaskStatus.STOP;
                break;
            case ERROR:
                convertedStatus = com.arcsoft.supervisor.model.vo.task.TaskStatus.ERROR;
                break;
            default:
                break;
        }
        Integer pid = tracker.getPid();
        return convertedStatus == null ? null : TaskStateChange.from(taskId, convertedStatus, pid);
    }
}
