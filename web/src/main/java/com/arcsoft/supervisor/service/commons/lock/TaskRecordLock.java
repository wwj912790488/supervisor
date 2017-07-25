package com.arcsoft.supervisor.service.commons.lock;

import com.arcsoft.supervisor.exception.RecordLockedException;

/**
 * A record lock interface for task.
 *
 * @author zw.
 */
public interface TaskRecordLock extends GlobalRecordLock {


    /**
     * Acquire the channel lock with specified <code>channelId</code>
     *
     * @param channelId the identify value of channle object
     * @throws RecordLockedException if failed to acquire the lock
     */
    void acquireChannelLock(int channelId) throws RecordLockedException;

    /**
     * Acquire the screen lock with specified <code>screenId</code>
     *
     * @param screenId the identify value of screen object
     * @throws RecordLockedException if failed to acquire the lock
     */
    void acquireScreenLock(int screenId) throws RecordLockedException;

    /**
     * Release the channel lock.
     *
     * @param channelId the identify value of channle object
     */
    void releaseChannelLock(int channelId);

    /**
     * Release the screen lock.
     *
     * @param screenId the identify value of screen object
     */
    void releaseScreenLock(int screenId);


}
