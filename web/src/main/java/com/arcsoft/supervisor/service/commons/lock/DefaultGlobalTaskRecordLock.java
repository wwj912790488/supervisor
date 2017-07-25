package com.arcsoft.supervisor.service.commons.lock;

import com.arcsoft.supervisor.commons.profile.Production;
import org.springframework.stereotype.Service;

/**
 * The default global exclusive record acquire implementation.It use a ConcurrentHashMap object to
 * hold the locked records to decision which record currently is locked or not.
 *
 * @author zw.
 */
@Service
@Production
public class DefaultGlobalTaskRecordLock extends AbstractTaskRecordLock {

}
