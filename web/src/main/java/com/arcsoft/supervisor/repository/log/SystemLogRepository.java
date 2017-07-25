package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Repository interface for <tt>Message</tt>
 *
 * @author jt.
 */
public interface SystemLogRepository extends JpaRepository<SystemLog, Integer>, QueryDslPredicateExecutor<SystemLog>, JpaSystemLogRepository {

}
