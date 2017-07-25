package com.arcsoft.supervisor.service.profile.impl;

import com.arcsoft.supervisor.model.domain.task.OutputProfile;
import com.arcsoft.supervisor.model.vo.task.profile.OutputProfileDto;
import com.arcsoft.supervisor.repository.profile.OutputProfileRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.converter.impl.OutputProfileAndTemplateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author zw.
 */
@Service("defaultOutputProfileService")
public class DefaultOutputProfileService extends AbstractProfileService<OutputProfile, OutputProfileDto>
        implements TransactionSupport {

    @Autowired
    public DefaultOutputProfileService(OutputProfileRepository outputProfileRepository,
                                       OutputProfileAndTemplateConverter outputProfileAndTemplateConverter) {
        super(outputProfileRepository, outputProfileAndTemplateConverter);
    }

}
