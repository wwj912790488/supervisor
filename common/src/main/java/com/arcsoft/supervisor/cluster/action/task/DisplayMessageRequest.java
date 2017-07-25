package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A display message request class to show or hide message of task.
 * <p>
 *     <strong>Note: This request just for IP-Stream compose task.</strong>
 * </p>
 *
 * @author zw.
 */
@XmlRootElement
public class DisplayMessageRequest extends BaseRequest {

    private Integer composeTaskId;

    private String message;

    private Integer taskType;

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getComposeTaskId() {
        return composeTaskId;
    }

    public void setComposeTaskId(Integer composeTaskId) {
        this.composeTaskId = composeTaskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DisplayMessageRequest{");
        sb.append("composeTaskId=").append(composeTaskId);
        sb.append(", taskType=").append(taskType);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
