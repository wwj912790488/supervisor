package com.arcsoft.supervisor.sartf.repository.layouttemplate;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import org.springframework.data.jpa.repository.JpaRepository;

@Sartf
public interface LayoutTemplateCellRepository extends JpaRepository<LayoutTemplateCell, Integer> {

    LayoutTemplateCell findByTemplateAndCellIndex(LayoutTemplate template, Integer cellIndex);

}
