package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.system.GpuConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zw.
 */
public interface GpuConfigurationRepository extends JpaRepository<GpuConfiguration, Integer> {
}
