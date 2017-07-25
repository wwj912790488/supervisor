package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.TaskType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author zw.
 */
@XmlRootElement
public class GetTranscoderXmlRequest extends BaseRequest {

    private TaskType taskType;

    private Integer taskId;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
