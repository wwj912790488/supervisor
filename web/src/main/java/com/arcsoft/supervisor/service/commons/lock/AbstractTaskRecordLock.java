package com.arcsoft.supervisor.service.commons.lock;

import com.arcsoft.supervisor.exception.RecordLockedException;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zw.
 */
public abstract class AbstractTaskRecordLock implements TaskRecordLock{

    private final ConcurrentHashMap<String, Object> lockedRecords;
    /**
     * The prefix of channel task lock key
     */
    private static final String CHANNEL_TASK_PREFIX = "c-";

    /**
     * The prefix of screen task lock key
     */
    private static final String SCREEN_TASK_PREFIX = "s-";

    public AbstractTaskRecordLock() {
        this.lockedRecords = new ConcurrentHashMap<>();
    }

    @Override
    public void acquire(String key, Object value) throws RecordLockedException {
        if (lockedRecords.putIfAbsent(key, value) != null) {
            throw new RecordLockedException();
        }
    }

    @Override
    public void release(String key) {
        lockedRecords.remove(key);
    }

    @Override
    public void acquireChannelLock(int channelId) throws RecordLockedException {
        acquire(getChannelTaskLockedKey(channelId), channelId);
    }

    @Override
    public void acquireScreenLock(int screenId) throws RecordLockedException {
        acquire(getScreenTaskLockedKey(screenId), screenId);
    }

    @Override
    public void releaseChannelLock(int channelId) {
        release(getChannelTaskLockedKey(channelId));
    }

    @Override
    public void releaseScreenLock(int screenId) {
        release(getScreenTaskLockedKey(screenId));
    }


    private String getChannelTaskLockedKey(int channelId) {
        return CHANNEL_TASK_PREFIX + channelId;
    }


    private String getScreenTaskLockedKey(int screenId) {
        return SCREEN_TASK_PREFIX + screenId;
    }
}
