package com.arcsoft.supervisor.sartf.service.layouttemplate;

import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;

import java.util.Date;
import java.util.List;

public interface LayoutTemplateService {
    List<LayoutTemplate> getUpdated(Date date);

    List<LayoutTemplate> findAll();
}
