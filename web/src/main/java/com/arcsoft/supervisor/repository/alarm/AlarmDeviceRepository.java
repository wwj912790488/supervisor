package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmDevice;
import com.arcsoft.supervisor.model.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The repository interface for <tt>AlarmDevice</tt>
 *
 * @author jt.
 */

public interface AlarmDeviceRepository extends JpaRepository<AlarmDevice, Integer> {

    AlarmDevice findByChannelId(String channelid);

    List<AlarmDevice> findByUserId(Integer id);

}


