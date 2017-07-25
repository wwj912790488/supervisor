package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmConfigChannel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The repository interface for <tt>AlarmDevice</tt>
 *
 * @author jt.
 */

public interface AlarmConfigChannelRepository extends JpaRepository<AlarmConfigChannel, Integer> {

    List<AlarmConfigChannel> findByChannelId(Integer channelid);
}


