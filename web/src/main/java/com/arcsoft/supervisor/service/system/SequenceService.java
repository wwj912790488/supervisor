package com.arcsoft.supervisor.service.system;

/**
 *
 * @author zw.
 */
public interface SequenceService {

    /**
     *
     * Increment the value with given key.
     *
     * @param key the key
     * @return the newly sequence value or {@code -1} if failed to increment value
     */
    long updateIncrementAndGet(String key);
}
