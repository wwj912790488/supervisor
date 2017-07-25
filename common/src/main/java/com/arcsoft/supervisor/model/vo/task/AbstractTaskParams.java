package com.arcsoft.supervisor.model.vo.task;


import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectTaskParams;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ContentDetectTaskParams.class, AbstractRtspParams.class})
public abstract class AbstractTaskParams {

    private Integer id;
    private TaskType taskType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName() + "{");
        sb.append("id=").append(id);
        sb.append(", taskType=").append(taskType);
        sb.append('}');
        return sb.toString();
    }
}
