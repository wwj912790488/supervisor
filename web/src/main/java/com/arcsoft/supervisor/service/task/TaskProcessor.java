package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;

/**
 * A processor interface to start or stop task.
 *
 * @author zw.
 */
public interface TaskProcessor {

    /**
     * Starts the given {@code task}.
     *
     * @param task the task to be started
     * @throws BusinessException Thrown with below<ul><li>{@link BusinessExceptionDescription#TASK_OPS_SERVER_NOT_EXIST}
     *                           if the task type is compose task and there is no any ops server exist</li><li>
     *                           {@link BusinessExceptionDescription#TASK_WALL_POSITION_NOT_EXIST} if the wall position is not exist.
     *                           </li><li>{@link BusinessExceptionDescription#TASK_NO_AVAILABLE_SERVER} if there is no any
     *                           available server can be use</li><li>{@link BusinessExceptionDescription#TASK_NO_SCREEN_CONFIG} if
     *                           the config of screen is not exist</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_STORAGE_NOT_EXIST} if the storage of record
     *                           task is not exist.</li><li>{@link BusinessExceptionDescription#ERROR} failed to start
     *                           task</li></ul>
     */
    public void start(Task task);

    /**
     * Stops the task with given {@code taskId}.
     *
     * @param taskId the identify value of task
     * @throws BusinessException With below<ul><li>{@link BusinessExceptionDescription#ERROR} failed to stop
     *                           task</li></ul>
     */
    public void stop(int taskId);

    void stop(int taskId, Server server);

    void reload(Task task);

}
