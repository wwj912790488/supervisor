package com.arcsoft.supervisor.agent.service.task;

/**
 * A manager interface to control and handle task.
 *
 * @author zw.
 */
public interface TaskManager {

    /**
     * Initialize with <code>functions</code>.
     *
     * @param functions which function support
     */
    void init(String functions);


    /**
     * Destroy the task manager.Will stop all of task.
     *
     */
    void destroy();

    /**
     * Specify a factory for create TaskStateChange object.
     *
     * @param stateChangeFactory the TaskStateChangeFactory
     */
    void setStateChangeFactory(TaskStateChangeFactory stateChangeFactory);


}
