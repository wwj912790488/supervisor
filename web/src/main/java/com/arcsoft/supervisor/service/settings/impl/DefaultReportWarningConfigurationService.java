package com.arcsoft.supervisor.service.settings.impl;


import com.arcsoft.supervisor.model.domain.system.ReportWarningConfiguration;
import com.arcsoft.supervisor.model.domain.system.WarningPushConfiguration;
import com.arcsoft.supervisor.repository.settings.ReportWarningConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


/**
 * Created by wwj on 2017/2/7.
 */
@Service("reportWarningConfigurationService")
public class DefaultReportWarningConfigurationService extends AbstractConfigurationService<ReportWarningConfiguration> implements ReportWarningConfigurationService, TransactionSupport {

    @Autowired
    protected DefaultReportWarningConfigurationService(ReportWarningConfigurationRepository repository) {
        super(repository);
    }

    @Override
    public void saveOrUpdateReport(ReportWarningConfiguration reportWarningConfiguration) {
        saveOrUpdate(reportWarningConfiguration);
    }
    /**
     * Returns the remote url.
     *
     * @return the url or empty string if url wasn't set
     */
    public String getRemoteUrl() {
        ReportWarningConfiguration cfg = getFromCache();
        return cfg == null ? StringUtils.EMPTY : cfg.getIp();
    }
}
