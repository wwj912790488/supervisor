package com.arcsoft.supervisor.thirdparty.push.voice;

import com.arcsoft.supervisor.commons.HttpClientUtils;
import com.arcsoft.supervisor.commons.profile.Voice;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.log.ServiceLogService;
import com.arcsoft.supervisor.service.settings.impl.DefaultReportWarningConfigurationService;
import com.arcsoft.supervisor.service.settings.impl.WarningPushConfigurationService;
import com.arcsoft.supervisor.web.JsonResult;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Reactor implementation for push content detect log to remote.
 *
 * @author zw.
 */
@Voice
@Service("pushContentDetectLogReactor")
public class PushContentDetectLogReactor extends ServiceSupport implements ContentDetectLogReactor {

    private final WarningPushConfigurationService warningPushConfigurationService;

    @Autowired
    public PushContentDetectLogReactor(WarningPushConfigurationService warningPushConfigurationService, DefaultReportWarningConfigurationService defaultReportWarningConfigurationService, ServiceLogService serviceLogService) {
        this.warningPushConfigurationService = warningPushConfigurationService;

    }

    @Override
    public String getName() {
        return "PushContentDetectLogReactor";
    }

    @Override
    public void react(ContentDetectLog contentDetectLog) {

        String url = warningPushConfigurationService.getRemoteUrl();
        if (StringUtils.isNotBlank(url)) {
            ContentDetectData detectData = new ContentDetectData(
                    contentDetectLog.getChannelId(),
                    contentDetectLog.getChannelName(),
                    contentDetectLog.getType(),
                    contentDetectLog.getStartTimeAsDate(),
                    contentDetectLog.getEndTimeAsDate(),
                    contentDetectLog.getGuid(),
                    contentDetectLog.getId()
            );

            try {
                HttpClientUtils.doPostJSON(url, detectData);
                logger.error("push content detect result with id=" + contentDetectLog.getId()
                        + " to: " + url);
            } catch (IOException e) {
                logger.error("Failed to push content detect result with id=" + contentDetectLog.getId()
                        + " cause by: " + e.getMessage());
            }
        } else {
            logger.error("Failed to push content detect result with id=" + contentDetectLog.getId()
                    + " cause by: empty url");
        }



    }
}
