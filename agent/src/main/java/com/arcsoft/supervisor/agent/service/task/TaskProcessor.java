package com.arcsoft.supervisor.agent.service.task;

import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;

/**
 * Defines contracts for handling tasks.
 *
 * @author zw.
 */
public interface TaskProcessor {

    /**
     * Start the specify task.
     *
     * @param task the actual task of {@link com.arcsoft.supervisor.model.vo.task.AbstractTaskParams}
     */
    void start(AbstractTaskParams task) throws StartTaskException;

    /**
     * Stop a task with specify {@code taskId}.
     *
     * @param taskId the identify value of task
     */
    void stop(int taskId);

    /**
     * Retrieves the thumbnail with specified task id.
     *
     * @param taskId the identify value of task
     * @return the tranacoder task's image byte data
     * @throws java.lang.UnsupportedOperationException if the processor do not support to get thumbnail
     */
    byte[] getThumbnail(int taskId) throws UnsupportedOperationException;

    /**
     * Checks the specified <code>taskId</code> is running or not.
     *
     * @param taskId the identify value of task
     * @return <code>true</code> if the <code>taskId</code> representation task is running otherwise is <code>false</code>
     */
    boolean isRunning(int taskId);

    /**
     * Retrieves the transcoder xml with given task id.
     *
     * @param taskId the identify value of task
     * @return the transcoder xml correspond to task id
     */
    String getTranscoderXml(int taskId);

    /**
     * Retrieves the xml as string of progress of task with given <code>taskId</code>
     *
     * @param taskId the id of task
     * @return the xml of progress of task
     */
    String getProgressXml(int taskId);

}
