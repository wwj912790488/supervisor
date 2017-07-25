package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.system.WarningPushConfiguration;
import com.arcsoft.supervisor.repository.settings.WarningPushConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Service implementation for <tt>WarningPushConfiguration</tt>.
 *
 * @author zw.
 */
@Service("warningPushConfigurationService")
public class WarningPushConfigurationService extends AbstractConfigurationService<WarningPushConfiguration>
        implements TransactionSupport {

    @Autowired
    protected WarningPushConfigurationService(WarningPushConfigurationRepository repository) {
        super(repository);
    }

    /**
     * {@inheritDoc}
     *
     * @param warningPushConfiguration the WarningPushConfiguration object
     * @return
     * @throws BusinessExceptionDescription with below
     *                                      <ul>
     *                                      <li>{@link BusinessExceptionDescription#HOST_UNREACHABLE} if the remote host unreachable</li>
     *                                      </ul>
     */
    @Override
    public WarningPushConfiguration saveOrUpdate(WarningPushConfiguration warningPushConfiguration) {
        String ipaddress = warningPushConfiguration.getIp();
        if (StringUtils.isNotBlank(ipaddress)) {
            try {
                boolean isReachable = false;
                if (ipaddress.contains(":")) {
                    String strIp = StringUtils.split(ipaddress, ':')[0];
                    isReachable = InetAddress.getByName(strIp).isReachable(5000);

                } else {
                    isReachable = InetAddress.getByName(ipaddress).isReachable(5000);
                }

                if (!isReachable) {
                    throw BusinessExceptionDescription.HOST_UNREACHABLE.exception();
                }
            } catch (IOException e) {
                throw BusinessExceptionDescription.HOST_UNREACHABLE.exception();
            }
        }
        return super.saveOrUpdate(warningPushConfiguration);
    }

    /**
     * Returns the remote url.
     *
     * @return the url or empty string if url wasn't set
     */
    public String getRemoteUrl() {
        WarningPushConfiguration cfg = getFromCache();
        return cfg == null ? StringUtils.EMPTY : cfg.getRemoteUrl();
    }
}
