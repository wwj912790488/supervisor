package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmPushedLogInfo;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository interface for <tt>AlarmPushedLogInfo</tt>
 *
 * @author jt.
 */

public interface AlarmPushedLogInfoRepository extends JpaRepository<AlarmPushedLogInfo, Integer> {


}


