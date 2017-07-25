package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.RtspConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zw.
 */
public interface RtspConfigurationRepository extends JpaRepository<RtspConfiguration, Integer> {
}
