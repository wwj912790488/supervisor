package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.system.GpuConfiguration;
import com.arcsoft.supervisor.repository.settings.GpuConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for <tt>GpuConfiguration</tt>.
 *
 * @author zw.
 */
@Service("gpuConfigurationService")
public class GpuConfigurationService extends AbstractConfigurationService<GpuConfiguration> implements TransactionSupport{

    @Autowired
    protected GpuConfigurationService(GpuConfigurationRepository repository) {
        super(repository);
        if(find()==null){
            GpuConfiguration configuration = new GpuConfiguration();
            configuration.setEnableSpan(true);
            saveOrUpdate(configuration);
        }
    }

    @Override
    public GpuConfiguration find() {
        GpuConfiguration configuration = super.find();
       /* if(configuration == null) {
            configuration = new GpuConfiguration();
        }*/
        return  configuration;
    }
}
