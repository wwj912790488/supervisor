package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.AlarmConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link AlarmConfiguration}.
 *
 * @author zw.
 */
public interface AlarmConfigurationRepository extends JpaRepository<AlarmConfiguration, Integer> {
}
