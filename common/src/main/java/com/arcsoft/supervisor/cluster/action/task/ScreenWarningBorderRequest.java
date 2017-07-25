package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A request class for show or hide the warning border on screen.
 *
 * @author zw.
 */
@XmlRootElement
public class ScreenWarningBorderRequest extends BaseRequest {

    private int taskId;

    /**
     * The index of screen.
     */
    private int index;

    /**
     * Show or hide the warning border.true will show otherwise will hide.
     */
    private boolean isShow;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScreenWarningBorderRequest{");
        sb.append("taskId=").append(taskId);
        sb.append(", index=").append(index);
        sb.append(", isShow=").append(isShow);
        sb.append('}');
        return sb.toString();
    }
}
