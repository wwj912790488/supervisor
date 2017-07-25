package com.arcsoft.supervisor.agent.service.task.converter;

import com.arcsoft.supervisor.agent.service.task.TaskStateChangeFactory;
import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.resource.ComposeTaskTranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.resource.TaskResourceHolder;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;

/**
 * Default implementation of {@link TaskStateChangeFactory}.
 *
 * @author zw.
 */
public class DefaultTaskStateChangeFactory implements TaskStateChangeFactory {

    /**
     *
     * @param taskId the id of task
     * @param status the status of task.
     * @return the converted TaskStateChange or {@code null} if we don't care the others status
     */
    @Override
    public TaskStateChange create(int taskId, TaskStatus status, ITranscodingTracker tracker) {
        com.arcsoft.supervisor.model.vo.task.TaskStatus convertedStatus = null;
        switch (status) {
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

        if (convertedStatus != null) {
            ComposeTaskTranscodingTrackerResource taskResource = getComposeTaskResource(tracker);
            Integer pid = tracker.getPid();
            if (taskResource != null) {
                return TaskStateChange.from(taskId, convertedStatus, taskResource.getUdpPort(), taskResource.getRtmpOpsFileName(), pid);
            }

            return TaskStateChange.from(taskId, convertedStatus, pid);
        }
        return null;
    }

    /**
     * Retrieves ComposeTaskTranscodingTrackerResource from tracker.
     *
     * @param tracker the transcoding tracker object
     * @return ComposeTaskTranscodingTrackerResource in tracker or {@code null}
     */
    private ComposeTaskTranscodingTrackerResource getComposeTaskResource(ITranscodingTracker tracker) {
        Object data = tracker.getUserData();
        if (data != null && data instanceof TaskResourceHolder) {
            TranscodingTrackerResource transcodingTrackerResource = ((TaskResourceHolder) data)
                    .getByType(ComposeTaskTranscodingTrackerResource.class);
            return transcodingTrackerResource != null
                    ? (ComposeTaskTranscodingTrackerResource) transcodingTrackerResource : null;
        }
        return null;
    }
}
