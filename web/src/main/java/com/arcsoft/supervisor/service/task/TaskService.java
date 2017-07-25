package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.vo.task.TaskType;

import java.util.List;

/**
 * A task service interface to defines business method for task.
 *
 * @author zw.
 */
public interface TaskService {

    public void saveOrUpdate(Task task);

    public Task getById(int id);

    public void delete(int id);

    public Task getByTypeAndReferenceId(int referenceId, TaskType type);


    /**
     * Returns all of running compose tasks.
     */
    public List<Task> getRunningComposeTasks();

    /**
     * Persists or retrieves the task.
     *
     * @param referenceId the id of reference record id
     * @param type        the type of task
     * @return the persists task
     */
    Task createOrGetTask(int referenceId, TaskType type);

    /**
     * Persists or retrieves the task.
     *
     * @param referenceId   the id of reference object
     * @param type          the type of task
     * @param taskProfileId the id of {@link TaskProfile}.Use {@link TaskProfile} of persists object
     *                      if the value is {@code -1}.
     * @return the persists task
     */
    Task createOrGetTask(int referenceId, TaskType type, int taskProfileId);

    /**
     * Persists or retrieves the task.
     *
     * @param screenId      the identify value of screen
     * @param type          the type of task
     * @param serverId      the id of server
     * @param taskProfileId the id of {@link TaskProfile}.Use {@link TaskProfile} of persists object
     *                      if the value is {@code -1}.
     * @return the persists task
     */
    Task createOrGetTask(int screenId, TaskType type, String serverId, int taskProfileId, int gpuStartIndex);


    /**
     * Retrieves the thumbnail of specified <code>channelId</code>.
     *
     * @param channelId the id of channel object
     * @return the thumbnail as byte
     * @throws SystemNotInitializedException if system has not initialized.
     * @throws ServerNotAvailableException   if the agent server is not available.
     * @throws RemoteException               if invoke failed.
     */
    public byte[] getRtspTaskThumbnail(int channelId);

    /**
     * Display or hide the <code>message</code> on the specified <code>composeTaskId</code> represents task.
     *
     * @param composeTaskId the identify value of task
     * @param message       the message will be show. a <code>null</code> or empty string to clear the display message.
     * @throws SystemNotInitializedException if system has not initialized.
     * @throws ServerNotAvailableException   if the agent server is not available.
     * @throws RemoteException               if invoke failed.
     */
    public void displayMessageOnComposeTask(int composeTaskId, String message);

    public void displayStyledMessageOnScreen(int screenId, MessageStyle style, String message);

    public String getProgress(int composeTaskId);


    public boolean isIPStreamComposeTaskHasOutput(int screenId);

    public void recognize(Server server, ServerComponent sdi, int number);

    /**
     * Retrieves channel tasks by given {@code channelIds}.
     *
     * @param channelIds the ids of channel
     * @return channel tasks
     */
    public List<Task> getChannelTasksByChannelIds(List<Integer> channelIds);


    /**
     * Retrieves screen task with given {@code screenId}.
     *
     * @param screenId the identify value of screen
     * @return the screen represents task
     * @throws NullPointerException if the screen or wall position or wall is not exists
     */
    public Task getScreenTask(int screenId);

   /* public String getScreenName(Integer channelId);*/

    Integer getUsedTaskProfileIdByScreenId(int screenId);

    String getTranscoderXml(int taskId);

    List<String> getRunningTasksChannelNameOnServer(String serverId);

    public void resetTaskStatusByRefid(Integer screenId);
}
