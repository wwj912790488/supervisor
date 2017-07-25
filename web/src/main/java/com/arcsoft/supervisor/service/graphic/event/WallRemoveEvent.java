package com.arcsoft.supervisor.service.graphic.event;

import com.arcsoft.supervisor.model.domain.task.Task;

import java.util.List;

/**
 * A event class indicate the wall is removed.
 *
 * @author zw.
 */
public class WallRemoveEvent{

    /**
     * The all of tasks associate to the wall.
     */
    private final List<Task> runningTasks;

    /**
     * The currently id of wall.
     */
    private final int wallId;


    public WallRemoveEvent(List<Task> runningTasks, int wallId) {
        this.runningTasks = runningTasks;
        this.wallId = wallId;
    }

    public List<Task> getRunningTasks() {
        return runningTasks;
    }

    public int getWallId() {
        return wallId;
    }
}
