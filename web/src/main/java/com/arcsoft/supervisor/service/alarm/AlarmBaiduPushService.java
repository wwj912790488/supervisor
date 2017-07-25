package com.arcsoft.supervisor.service.alarm;


import com.arcsoft.supervisor.model.domain.alarm.AlarmPushLog;
import com.arcsoft.supervisor.service.alarm.impl.CustomAlarmLogQueryParams;
import com.arcsoft.supervisor.thirdparty.baidupush.ContentDetectLogData;

import java.util.List;


/**
 * Defines functional logic for ops.
 *
 * @author jt.
 */
public interface AlarmBaiduPushService {

     void updateAndPush(ContentDetectLogData contentDetectData);

     List<AlarmPushLog> findAll(CustomAlarmLogQueryParams params);

     void pushToAllDevice(String pushMsg);

}
