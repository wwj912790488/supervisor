package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmConfig;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository interface for <tt>AlarmConfig</tt>
 *
 * @author jt.
 */

public interface AlarmConfigRepository extends JpaRepository<AlarmConfig, Integer> {


}


