package com.arcsoft.supervisor.repository.system;

import com.arcsoft.supervisor.model.domain.system.TranscoderTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zw.
 */
public interface TranscoderTemplateRepository extends JpaRepository<TranscoderTemplate, Integer> {
}
