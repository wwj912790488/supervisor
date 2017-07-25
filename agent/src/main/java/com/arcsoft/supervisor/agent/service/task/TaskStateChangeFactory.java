package com.arcsoft.supervisor.agent.service.task;

import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;

/**
 * Callback interface used within {@link TaskManager} for create {@link TaskStateChange} object
 * with id and status of task and {@link ITranscodingTracker}.
 *
 * @author zw.
 */
public interface TaskStateChangeFactory {

    /**
     * Create the {@link TaskStateChange} with the {@code status} of given {@code taskId} and tracker.
     *
     * @param taskId the id of task
     * @param status the status of task
     * @param tracker the transcoder tracker
     * @return the converted TaskStateChange
     */
    TaskStateChange create(int taskId, TaskStatus status, ITranscodingTracker tracker);
}
