package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.system.ChannelRecordConfiguration;
import com.arcsoft.supervisor.repository.settings.ChannelRecordConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service("channelRecordConfigurationService")
public class DefaultChannelRecordConfigurationService extends AbstractConfigurationService<ChannelRecordConfiguration>
        implements TransactionSupport, ChannelRecordConfigurationService{

        @Autowired
        protected DefaultChannelRecordConfigurationService(ChannelRecordConfigurationRepository repository) {
                super(repository);
        }
}
