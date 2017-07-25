package com.arcsoft.supervisor.repository.layouttemplate;

import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutPositionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LayoutPositionTemplateRepository extends JpaRepository<LayoutPositionTemplate, Integer>{
    LayoutPositionTemplate findFirstByGuid(String guid);
}
