package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.system.MailSenderInfoConfiguration;
import com.arcsoft.supervisor.repository.settings.MailWarningConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Created by wwj on 2017/2/16.
 */
@Service("emailWarningConfigurationService")
public class EmailWarningConfigurationServiceImpl extends AbstractConfigurationService<MailSenderInfoConfiguration> implements EmailWarningConfigurationService,TransactionSupport {
    @Autowired
    protected EmailWarningConfigurationServiceImpl(MailWarningConfigurationRepository repository) {
        super(repository);
    }

    @Override
    public void saveOrUpdateReport(MailSenderInfoConfiguration mailSenderInfoConfiguration) {
        saveOrUpdate(mailSenderInfoConfiguration);
    }
}
