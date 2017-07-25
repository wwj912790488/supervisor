package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.system.SmsWarningConfiguration;
import com.arcsoft.supervisor.service.settings.ConfigurationService;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Interface for {@code SmsWarningConfiguration}.
 *
 * @author zw.
 */
public interface SmsWarningConfigurationService extends ConfigurationService<SmsWarningConfiguration>{

    /**
     * Save or update {@code SmsWarningConfiguration} and update the phone number of given user id.
     *
     * @param cfg the {@link SmsWarningConfiguration}
     * @param userId the user identifier
     * @param phoneNumber the phone number of user
     * @throws BusinessException with below
     * <ul>
     *     <li>{@link BusinessExceptionDescription#URL_CONNECT_TIMEOUT} if the url can't connect or timeout</li>
     * </ul>
     */
    void saveOrUpdateSmsConfigurationAndUserPhoneNumber(SmsWarningConfiguration cfg, int userId, String phoneNumber);

    /**
     * Checks mobile message send functionality is enable or not.
     *
     * @return A pair object, the left value is boolean which indicates mobile send functionality is enable or not,
     * the right value is the SmsWarningConfiguration object if enable mobile send
     */
    Pair<Boolean, SmsWarningConfiguration> isEnableSend();
}
