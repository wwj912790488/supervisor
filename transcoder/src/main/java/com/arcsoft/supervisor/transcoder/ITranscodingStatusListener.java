package com.arcsoft.supervisor.transcoder;


import com.arcsoft.supervisor.transcoder.type.TaskStatus;

/**
 * ITranscodingStatusListener
 *
 * @author Bing
 */
public interface ITranscodingStatusListener {
    /**
     * @param transcodingNotifier
     * @param status
     */
    void handleTaskStatusChanged(ITranscodingNotifier transcodingNotifier, TaskStatus status);
}
