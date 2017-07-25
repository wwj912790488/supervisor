package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.WarningPushConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link WarningPushConfiguration}.
 *
 * @author zw.
 */
public interface WarningPushConfigurationRepository extends JpaRepository<WarningPushConfiguration, Integer> {
}
