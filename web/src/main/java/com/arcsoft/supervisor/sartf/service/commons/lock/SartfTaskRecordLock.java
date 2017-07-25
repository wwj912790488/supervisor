package com.arcsoft.supervisor.sartf.service.commons.lock;

import com.arcsoft.supervisor.exception.RecordLockedException;
import com.arcsoft.supervisor.service.commons.lock.TaskRecordLock;


public interface SartfTaskRecordLock extends TaskRecordLock {

    void acquireUserLock(int userId) throws RecordLockedException;

    void releaseUserLock(int userId);
}
