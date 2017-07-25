package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request class for reload compose task.
 *
 * @author zw.
 */
@XmlRootElement
public class ReloadRequest extends BaseRequest {

    private AbstractTaskParams taskParams;

    public AbstractTaskParams getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(AbstractTaskParams taskParams) {
        this.taskParams = taskParams;
    }
}
