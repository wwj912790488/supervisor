package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.MailSenderInfoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wwj on 2017/2/16.
 */
public interface MailWarningConfigurationRepository extends JpaRepository<MailSenderInfoConfiguration, Integer> {
}
