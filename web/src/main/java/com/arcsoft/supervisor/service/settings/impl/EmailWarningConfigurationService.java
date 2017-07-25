package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.system.MailSenderInfoConfiguration;
import com.arcsoft.supervisor.service.settings.ConfigurationService;


public interface EmailWarningConfigurationService extends ConfigurationService<MailSenderInfoConfiguration> {
    void saveOrUpdateReport(MailSenderInfoConfiguration mailSenderInfoConfiguration);
}
