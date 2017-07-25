package com.arcsoft.supervisor.sartf.service.layouttemplate.impl;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.sartf.repository.layouttemplate.LayoutTemplateRepository;
import com.arcsoft.supervisor.sartf.service.layouttemplate.LayoutTemplateService;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Sartf
public class DefaultLayoutTemplateService implements LayoutTemplateService, TransactionSupport {

	@Autowired
	private LayoutTemplateRepository layoutTemplateRepository;
	
	@Override
	public List<LayoutTemplate> getUpdated(Date date) {
		Date newest = layoutTemplateRepository.getNewest();
		if(date.before(newest)) {
			return layoutTemplateRepository.findAll();
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<LayoutTemplate> findAll() {
		return layoutTemplateRepository.findAll();
	}

}
