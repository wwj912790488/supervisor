package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.system.AlarmConfiguration;

/**
 * Service implementation for <tt>AlarmConfiguration</tt>.
 *
 * @author jt.
 */

public interface AlarmConfigurationService extends ConfigurationService<AlarmConfiguration> {

    String getAndroidApiKey();
    String getAndroidSecertKey();
    String getIOSApiKey();
    String getIOSSecertKey();

}
