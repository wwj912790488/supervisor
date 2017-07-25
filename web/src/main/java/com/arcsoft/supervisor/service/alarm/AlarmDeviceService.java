package com.arcsoft.supervisor.service.alarm;


import com.arcsoft.supervisor.model.domain.alarm.AlarmDevice;

import java.util.List;

/**
 * Defines functional logic for ops.
 *
 * @author jt.
 */
public interface AlarmDeviceService {

     void save(AlarmDevice alarmDevice);

     AlarmDevice getById(Integer id);

     List<AlarmDevice> findAll();

     void delete(Integer id);

    AlarmDevice findDevByChannelId(String channelid);


}
