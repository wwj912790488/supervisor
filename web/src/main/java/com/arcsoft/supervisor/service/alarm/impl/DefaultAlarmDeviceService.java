package com.arcsoft.supervisor.service.alarm.impl;


import com.arcsoft.supervisor.repository.alarm.AlarmDeviceRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.alarm.AlarmDeviceService;
import com.arcsoft.supervisor.model.domain.alarm.AlarmDevice;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author jt.
 */

@Service
public class DefaultAlarmDeviceService extends ServiceSupport implements AlarmDeviceService {

    @Autowired
    private AlarmDeviceRepository alarmDeviceRepository;

    @Override
    public void save(AlarmDevice alarmDevice) {
        alarmDeviceRepository.save(alarmDevice);
    }

    @Override
    public AlarmDevice getById(Integer id) {
        return alarmDeviceRepository.findOne(id);
    }

    @Override
    public List<AlarmDevice> findAll() {
        return alarmDeviceRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        AlarmDevice alarmDevice = getById(id);
        if (alarmDevice != null){
            alarmDeviceRepository.delete(id);
        }
    }

    @Override
    public AlarmDevice findDevByChannelId(String channelid)
    {
        return alarmDeviceRepository.findByChannelId(channelid);
    }
}
