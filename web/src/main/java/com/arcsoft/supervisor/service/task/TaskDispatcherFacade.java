package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.task.Task;

/**
 * A interface for dispatch all of tasks.The interface is a facade for
 * simplify to do web controller logic.
 *
 * @author zw.
 */
public interface TaskDispatcherFacade {

    /**
     * Restarts the channel type of task.
     *
     * @param channelId the identify value of channel object
     * @throws BusinessException thrown with below<ul><li>{@link BusinessExceptionDescription#TASK_NOT_EXIST} if the
     *                           channel is not exist</li><li>{@link BusinessExceptionDescription#RECORD_LOCKED} if
     *                           the instance of {@code channelId} is locked by others.</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_START_OR_STOP_TIMEOUT} if timeout to start
     *                           or stop channel task.</li><li> others see {@link TaskExecutor#start(Task)} and
     *                           {@link TaskExecutor#stop(int)} </li></ul>
     */
    void restartChannelTask(int channelId);

    /**
     * Restarts the screen type of task.
     *
     * @param screenId the identify value of screen object
     * @throws BusinessException thrown with below<ul><li>{@link BusinessExceptionDescription#SCREEN_NOT_EXISTS}
     *                           if the screen is not exist</li><li>{@link BusinessExceptionDescription#RECORD_LOCKED}
     *                           if the instance of {@code channelId} is locked by others.</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_START_OR_STOP_TIMEOUT} if timeout to start
     *                           or stop channel task.</li><li> others see {@link TaskExecutor#start(Task)} and
     *                           {@link TaskExecutor#stop(int)} </li></ul>
     */
    void restartScreenTask(int screenId, int taskProfileId);

    /**
     * Starts the screen type of task.
     *
     * @param screenId the identify value of screen object
     * @param taskProfileId the id of task profile
     * @throws BusinessException thrown with below<ul><li>{@link BusinessExceptionDescription#SCREEN_NOT_EXISTS}
     *                           if the screen is not exist</li><li>{@link BusinessExceptionDescription#RECORD_LOCKED}
     *                           if the instance of {@code channelId} is locked by others.</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_START_OR_STOP_TIMEOUT} if timeout to start
     *                           or stop channel task.</li><li> others see {@link TaskExecutor#start(Task)} and
     *                           {@link TaskExecutor#stop(int)} </li></ul>
     */
    void startScreenTask(int screenId, int taskProfileId);

    void startScreenTask(int screenId, int taskProfileId, String serverId, int gpuStartIndex);

    /**
     * update the current running task
     * */
    void reloadOrStartScreenTask(int screenId, int taskProfileId, String serverId, int gpuStartIndex);

    /**
     * Stops the screen type of task.
     *
     * @param screenId the identify value of screen object
     * @throws BusinessException thrown with below<ul><li>{@link BusinessExceptionDescription#SCREEN_NOT_EXISTS}
     *                           if the screen is not exist</li><li>{@link BusinessExceptionDescription#RECORD_LOCKED}
     *                           if the instance of {@code channelId} is locked by others.</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_START_OR_STOP_TIMEOUT} if timeout to start
     *                           or stop channel task.</li><li> others see {@link TaskExecutor#start(Task)} and
     *                           {@link TaskExecutor#stop(int)} </li></ul>
     */
    void stopScreenTask(int screenId);

    /**
     * Stops the channel type of task.
     *
     * @param channelId the identify value of screen object
     * @throws BusinessException thrown with below<ul><li>{@link BusinessExceptionDescription#CHANNEL_NOT_EXIST}
     *                           if the screen is not exist</li><li>{@link BusinessExceptionDescription#RECORD_LOCKED}
     *                           if the instance of {@code channelId} is locked by others.</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_START_OR_STOP_TIMEOUT} if timeout to start
     *                           or stop channel task.</li><li> others see {@link TaskExecutor#start(Task)} and
     *                           {@link TaskExecutor#stop(int)} </li></ul>
     */
    void stopChannelTask(int channelId);

    boolean isTaskRunning(int channelId);
}
