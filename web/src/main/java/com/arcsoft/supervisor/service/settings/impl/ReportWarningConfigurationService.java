package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.system.ReportWarningConfiguration;
import com.arcsoft.supervisor.service.settings.ConfigurationService;

/**
 * Created by wwj on 2017/2/7.
 */
public interface ReportWarningConfigurationService  extends ConfigurationService<ReportWarningConfiguration> {
    void saveOrUpdateReport(ReportWarningConfiguration reportWarningConfiguration);
}
