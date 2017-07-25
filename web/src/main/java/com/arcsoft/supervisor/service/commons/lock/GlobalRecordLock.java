package com.arcsoft.supervisor.service.commons.lock;

import com.arcsoft.supervisor.exception.RecordLockedException;

/**
 * A exclusive acquire class to exclusive acquire an actual record.
 *
 * @author zw.
 */
public interface GlobalRecordLock {


    /**
     * Puts a key-value object to acquire the record acquire.
     *
     * @param key the unique value of record will be locked
     * @param value the actual value
     * @throws RecordLockedException if failed to acquire the lock
     */
    public void acquire(String key, Object value) throws RecordLockedException;

    /**
     * Release the acquire record lock with acquire key.
     *
     * @param key the unique value of record will be locked
     */
    public void release(String key);

}
