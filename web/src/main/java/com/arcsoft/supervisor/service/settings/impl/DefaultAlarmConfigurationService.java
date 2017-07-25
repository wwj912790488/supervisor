package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.system.AlarmConfiguration;
import com.arcsoft.supervisor.repository.settings.AlarmConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.settings.AlarmConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Service implementation for <tt>AlarmConfiguration</tt>.
 *
 * @author jt.
 */
@Service("alarmConfigurationService")
public class DefaultAlarmConfigurationService extends AbstractConfigurationService<AlarmConfiguration>
        implements TransactionSupport,AlarmConfigurationService {

    @Autowired
    protected DefaultAlarmConfigurationService(AlarmConfigurationRepository repository) {
        super(repository);
    }

    @Override
    public AlarmConfiguration saveOrUpdate(AlarmConfiguration alarmConfiguration) {
        return super.saveOrUpdate(alarmConfiguration);
    }

    @Override
    public String getAndroidApiKey() {
        AlarmConfiguration cfg = getFromCache();
        return cfg.getAndroidapikey();
    }

    @Override
    public String getAndroidSecertKey() {
        AlarmConfiguration cfg = getFromCache();
        return cfg.getAndroidsecretkey();
    }

    @Override
    public String getIOSApiKey(){
        AlarmConfiguration cfg = getFromCache();
        return cfg.getIosapikey();
    }

    @Override
    public String getIOSSecertKey(){
        AlarmConfiguration cfg = getFromCache();
        return cfg.getIossecretsey();
    }

 
}
