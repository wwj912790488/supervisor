package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;

/**
 * A executor to manage start or stop task.
 *
 * @author zw.
 */
public interface TaskExecutor {

    /**
     * Starts the task with given {@code taskId}.
     *
     * @param taskId the identify value of task
     * @throws BusinessException <ul><li>{@link BusinessExceptionDescription#TASK_NOT_EXIST} if the task is not existed</li>
     * <li>others see {@link TaskProcessor#start(Task)}</li></ul>
     * @see TaskProcessor#start(Task)
     *
     */
    public void start(int taskId);

    /**
     * Stops the given {@code task}.
     *
     * @param task the instance of task
     * @throws BusinessException <ul><li>{@link BusinessExceptionDescription#TASK_NOT_EXIST} if the task is not existed</li>
     * <li>others see {@link TaskProcessor#start(Task)}</li></ul>
     * @see TaskProcessor#start(Task)
     */
    public void start(Task task);

    /**
     * Stops the task with given {@code taskId}.
     *
     * @param taskId the identify value of task
     * @throws BusinessException <ul><li>{@link BusinessExceptionDescription#TASK_NOT_EXIST} if the task is not existed</li>
     * <li>others see {@link TaskProcessor#stop(int)}</li></ul>
     * @see TaskProcessor#stop(int)
     */
    public void stop(int taskId);

    /**
     * Stop the given {@code task}.
     *
     * @param task the instance of task
     * @throws BusinessException <ul><li>{@link BusinessExceptionDescription#TASK_NOT_EXIST} if the task is not existed</li>
     * <li>others see {@link TaskProcessor#stop(int)}</li></ul>
     * @see TaskProcessor#stop(int)
     */
    public void stop(Task task);

    void stop(int taskId, Server server);

    void reload(Task task);

    void reload(int taskId);

    /**
     * Checks the <code>taskId</code> representation <code>task</code> is running or not.
     *
     * @param taskId the identify value of <code>task</code>
     * @return <code>true</code> if the <tt>taskId</tt> representation <tt>task</tt> is running otherwise is <tt>false</tt>
     */
    public boolean isTaskRunning(int taskId);

}
