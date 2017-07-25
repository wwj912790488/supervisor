package com.arcsoft.supervisor.repository.system;

import com.arcsoft.supervisor.model.domain.system.Sequence;

/**
 *
 * Repository interface for {@link Sequence}.
 *
 * @author zw.
 */
public interface SequenceRepository {

    /**
     * Increment value by one with given key.
     *
     * @param key the key
     * @return the newly sequence value or {@code -1} if failed to increment value
     */
    long incrementAndGet(String key);
}
