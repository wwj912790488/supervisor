package com.arcsoft.supervisor.repository.task;

import com.arcsoft.supervisor.model.domain.channel.TaskChannelAssociatedScreenPosition;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;

import java.util.List;

/**
 * A jpa functions interface for <code>Task</code>.
 *
 * @author zw.
 */
public interface JpaTaskRepository {

    /**
     * Returns the IP-Stream compose task's channel associated screen positions with specified <code>channelId</code>.
     *
     * @param channelId the identify value of channel
     * @return a <code>TaskChannelAssociatedScreenPosition</code> list
     */
    public List<TaskChannelAssociatedScreenPosition> findIPSteamComposeTaskChannelAssociatedScreenPositionByChannelId(int channelId);


    /**
     * Returns all of compose task's channel associated screen positions with specified <code>channelId</code> and
     * {@code types}.
     *
     * @param channelId the given identify value of channel
     * @param types the types of task
     * @return the <code>TaskChannelAssociatedScreenPosition</code> list
     */
    public List<TaskChannelAssociatedScreenPosition> findComposeTaskChannelAssociatedScreenPositionByChannelIdAndTypes(int channelId, Integer... types);

    /**
     * Returns all of running IP-Stream tasks.
     */
    public List<Task> findRunningIPStreamComposeTasks();

    /**
     * Returns all of running Compose tasks with given {@code types}.
     * @param types the types of task
     */
    public List<Task> findRunningComposeTasksByTypes(Integer... types);


    /**
     * Returns all of running IP-Stream tasks with specific <code>wallId</code>.
     *
     * @param wallId the identify value of wall object
     * @return all of running IP-Stream tasks.
     */
    public List<Task> findRunningIPStreamComposeTasksByWallId(int wallId);

    /**
     * Returns all of running compose tasks with specific <code>wallId</code> and given {@code types}.
     *
     * @param wallId the identify value of wall object
     * @param types the types of task
     * @return all of running compose tasks.
     */
    public List<Task> findRunningComposeTasksByWallIdAndTypes(int wallId, Integer... types);

    /**
     *
     * Finds all of compose task associate to wallId and status and task types.
     *
     * @param wallId the wall id
     * @param status the status of task
     * @param types the types of task
     * @return all of compose task associate to
     */
    List<Task> findComposeTasksByWallIdAndTypesAndStatus(int wallId, TaskStatus[] status, Integer... types);

}
