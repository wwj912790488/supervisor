package com.arcsoft.supervisor.sartf.service.commons.lock;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.RecordLockedException;
import com.arcsoft.supervisor.service.commons.lock.AbstractTaskRecordLock;
import org.springframework.stereotype.Service;


@Service
@Sartf
public class SartfTaskRecordLockImpl extends AbstractTaskRecordLock implements SartfTaskRecordLock {

    private static final String USER_TASK_PREFIX = "u-";

    @Override
    public void acquireUserLock(int userId) throws RecordLockedException {
        acquire(getUserTaskLockedKey(userId), userId);
    }

    @Override
    public void releaseUserLock(int userId) {
        release(getUserTaskLockedKey(userId));
    }

    private String getUserTaskLockedKey(int userId) {
        return USER_TASK_PREFIX + userId;
    }
}
