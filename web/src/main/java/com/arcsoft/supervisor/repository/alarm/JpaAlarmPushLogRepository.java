package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmPushLog;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.service.alarm.impl.CustomAlarmLogQueryParams;
import com.arcsoft.supervisor.service.log.impl.ChannelsContentDetectQueryParams;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;

import java.util.List;


/**
 * The extend repository of jpa implementation for content detect.
 *
 * @author jt.
 */
public interface JpaAlarmPushLogRepository {

	 List<AlarmPushLog> findAll(CustomAlarmLogQueryParams params);

}
