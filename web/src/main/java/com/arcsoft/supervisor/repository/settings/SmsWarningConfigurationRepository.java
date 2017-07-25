package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.SmsWarningConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link SmsWarningConfiguration}.
 *
 * @author zw.
 */
public interface SmsWarningConfigurationRepository extends JpaRepository<SmsWarningConfiguration, Integer> {
}
