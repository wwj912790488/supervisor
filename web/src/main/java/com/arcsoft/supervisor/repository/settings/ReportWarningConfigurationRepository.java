package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.ReportWarningConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wwj on 2017/2/7.
 */
public interface ReportWarningConfigurationRepository  extends JpaRepository<ReportWarningConfiguration, Integer> {
}
