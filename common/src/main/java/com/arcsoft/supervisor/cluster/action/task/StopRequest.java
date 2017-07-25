package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.TaskType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Request class for stop task action.
 *
 * @author zw.
 */
@XmlRootElement
public class StopRequest extends BaseRequest {

    private List<Integer> taskIds;
    private TaskType taskType;

    public List<Integer> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Integer> taskIds) {
        this.taskIds = taskIds;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}
