package com.arcsoft.supervisor.service.alarm;


import com.arcsoft.supervisor.model.domain.alarm.AlarmConfig;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.AlarmConfigBean;


/**
 * Defines functional logic for ops.
 *
 * @author jt.
 */
public interface AlarmConfigService {

    AlarmConfig findById(Integer id);

    void updateAlarmConfig(AlarmConfig alarmconfig, AlarmConfigBean bean);

    void saveAlarmConfig(User user, AlarmConfigBean bean);

}
