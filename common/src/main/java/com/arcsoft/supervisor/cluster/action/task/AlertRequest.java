package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.TaskAlertContent;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * A request class to holds the alert data structure.
 *
 * @author zw.
 */
@XmlRootElement
public class AlertRequest extends BaseRequest {

    /**
     * Contains all of alert relevant information of task.
     */
    private TaskAlertContent alertContent;

    public AlertRequest() {
    }

    public AlertRequest(TaskAlertContent alertContent) {
        this.alertContent = alertContent;
    }

    public TaskAlertContent getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(TaskAlertContent alertContent) {
        this.alertContent = alertContent;
    }
}
