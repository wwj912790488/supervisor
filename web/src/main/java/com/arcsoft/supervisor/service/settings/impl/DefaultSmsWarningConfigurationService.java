package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.system.SmsWarningConfiguration;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.repository.settings.SmsWarningConfigurationRepository;
import com.arcsoft.supervisor.repository.user.UserRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.user.ProductionUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service implementation for <tt>SmsWarningConfiguration</tt>.
 *
 * @author zw.
 */
@Service("smsWarningConfigurationService")
public class DefaultSmsWarningConfigurationService extends AbstractConfigurationService<SmsWarningConfiguration>
        implements SmsWarningConfigurationService, TransactionSupport {

    private final ProductionUserService userService;

    private final UserRepository userRepository;

    @Autowired
    protected DefaultSmsWarningConfigurationService(
            SmsWarningConfigurationRepository repository,
            ProductionUserService userService,
            UserRepository userRepository) {
        super(repository);
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void saveOrUpdateSmsConfigurationAndUserPhoneNumber(SmsWarningConfiguration cfg, int userId, String phoneNumber) {
        User user = userRepository.findOne(userId);
        if (user != null) {
            if (user.isSuperAdmin()) {
                if (StringUtils.isNotBlank(cfg.getUrl())) {
                    validateUrl(cfg.getUrl());
                }
                saveOrUpdate(cfg);
            }
            userService.updateUserPhoneNumber(userId, phoneNumber);
        }
    }

    @Override
    public Pair<Boolean, SmsWarningConfiguration> isEnableSend() {
        SmsWarningConfiguration cfg = getFromCache();
        return Pair.of(cfg != null && StringUtils.isNotBlank(cfg.getUrl()), cfg);
    }

    private void validateUrl(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        get.setConfig(RequestConfig.custom().setConnectTimeout(5000).build());
        try {
            client.execute(get);
        } catch (IOException e) {
            throw BusinessExceptionDescription.URL_CONNECT_TIMEOUT.exception();
        } finally {
            try {
                client.close();
            } catch (IOException e) {}
        }
    }
}
