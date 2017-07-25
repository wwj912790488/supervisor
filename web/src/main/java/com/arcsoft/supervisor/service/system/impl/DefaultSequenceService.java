package com.arcsoft.supervisor.service.system.impl;

import com.arcsoft.supervisor.repository.system.SequenceRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.system.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zw.
 */
@Service
public class DefaultSequenceService implements SequenceService, TransactionSupport {

    private final SequenceRepository sequenceRepository;

    @Autowired
    public DefaultSequenceService(SequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Override
    public long updateIncrementAndGet(String key) {
        return sequenceRepository.incrementAndGet(key);
    }
}
