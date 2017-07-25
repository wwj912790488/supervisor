package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.task.Task;

/**
 * Created by wwj on 2017/6/29.
 */
public class ScreenWebBeanAndTask {
    ScreenWebBean screenWebBean;
    Task task;

    public ScreenWebBeanAndTask(ScreenWebBean screenWebBean, Task task) {
        this.screenWebBean = screenWebBean;
        this.task = task;
    }

    public ScreenWebBean getScreenWebBean() {
        return screenWebBean;
    }

    public void setScreenWebBean(ScreenWebBean screenWebBean) {
        this.screenWebBean = screenWebBean;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
