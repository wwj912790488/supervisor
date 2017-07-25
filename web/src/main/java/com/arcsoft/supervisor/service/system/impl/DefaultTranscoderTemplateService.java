package com.arcsoft.supervisor.service.system.impl;

import com.arcsoft.supervisor.model.domain.system.TranscoderTemplate;
import com.arcsoft.supervisor.repository.system.TranscoderTemplateRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.system.TranscoderTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation for {@link TranscoderTemplateService}.
 *
 * @author zw.
 */
@Service
public class DefaultTranscoderTemplateService implements TranscoderTemplateService, TransactionSupport {

    private final TranscoderTemplateRepository repository;

    @Autowired
    public DefaultTranscoderTemplateService(TranscoderTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void update(TranscoderTemplate template) {
        TranscoderTemplate transcoderTemplate = repository.findOne(template.getId());
        if (transcoderTemplate != null) {
            repository.save(transcoderTemplate);
        }
    }

    @Override
    public TranscoderTemplate find() {
        return repository.findOne(1); //Always get the first one
    }

}
