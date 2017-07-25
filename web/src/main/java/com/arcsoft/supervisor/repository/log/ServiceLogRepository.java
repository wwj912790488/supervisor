package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Repository interface for {@link ServiceLog}.
 *
 * @author zw.
 */
public interface ServiceLogRepository extends JpaRepository<ServiceLog, Integer>, QueryDslPredicateExecutor<ServiceLog>, JpaServiceLogRepository {

}
