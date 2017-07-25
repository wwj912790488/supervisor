package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmPushLog;

import com.arcsoft.supervisor.repository.log.JpaContentDetectLogRepository;
import com.arcsoft.supervisor.service.alarm.impl.CustomAlarmLogQueryParams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The repository interface for <tt>AlarmPushLog</tt>
 *
 * @author jt.
 */

public interface AlarmPushLogRepository extends JpaRepository<AlarmPushLog, Integer>,
        JpaAlarmPushLogRepository {

    List<AlarmPushLog> findAll(CustomAlarmLogQueryParams params);
}


