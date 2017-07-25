package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The request class for start task action.
 *
 * @author zw.
 */
@XmlRootElement
public class StartRequest extends BaseRequest {

    private AbstractTaskParams task;

    public AbstractTaskParams getTask() {
        return task;
    }

    public void setTask(AbstractTaskParams task) {
        this.task = task;
    }
}
