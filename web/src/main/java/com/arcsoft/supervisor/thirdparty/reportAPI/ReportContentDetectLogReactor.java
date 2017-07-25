package com.arcsoft.supervisor.thirdparty.reportAPI;

import com.arcsoft.supervisor.commons.HttpClientUtils;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.log.ServiceLogService;
import com.arcsoft.supervisor.service.settings.impl.DefaultReportWarningConfigurationService;
import com.arcsoft.supervisor.service.settings.impl.WarningPushConfigurationService;
import com.arcsoft.supervisor.thirdparty.push.voice.ContentDetectData;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service("ReportContentDetectLogReactor")
public class ReportContentDetectLogReactor extends ServiceSupport implements ContentDetectLogReactor {
    private final WarningPushConfigurationService warningPushConfigurationService;
    private final DefaultReportWarningConfigurationService defaultReportWarningConfigurationService;
    private final ServiceLogService serviceLogService;

    @Autowired
    public ReportContentDetectLogReactor(WarningPushConfigurationService warningPushConfigurationService, DefaultReportWarningConfigurationService defaultReportWarningConfigurationService, ServiceLogService serviceLogService) {
        this.warningPushConfigurationService = warningPushConfigurationService;
        this.defaultReportWarningConfigurationService = defaultReportWarningConfigurationService;
        this.serviceLogService = serviceLogService;
    }

    @Override
    public String getName() {

        return "ReportContentDetectLogReactor";
    }

    @Override
    public void react(ContentDetectLog contentDetectLog) {

        //report post json
        String ReportUrl = defaultReportWarningConfigurationService.getRemoteUrl();
        if (StringUtils.isNotBlank(ReportUrl)) {
            ContentDetectData detectData = new ContentDetectData(
                    contentDetectLog.getChannelId(),
                    contentDetectLog.getChannelName(),
                    contentDetectLog.getType(),
                    contentDetectLog.getStartTimeAsDate(),
                    contentDetectLog.getEndTimeAsDate(),
                    contentDetectLog.getGuid(),
                    contentDetectLog.getId(),"yyyy-MM-dd HH:mm:ss.SSS"
            );

            try {
                String result = HttpClientUtils.doPostJSON(ReportUrl, detectData);
                JSONObject jsonObject = JSONObject.fromObject(result);
                if ((Boolean)jsonObject.get("success") == false) {
                    //byte level, byte module, String description, String ip
                    ServiceLog serviceLog = new ServiceLog((byte) 10, (byte)3, "id=" + contentDetectLog.getId() + " " + jsonObject.get("message").toString(), ReportUrl);
                    serviceLogService.save(serviceLog);
                }

            } catch (IOException e) {
                logger.error("Failed to ReportUrl content detect result with id=" + contentDetectLog.getId()
                        + " cause by: " + e.getMessage());
            }
        } else {
    //        logger.error("Failed to ReportUrl content detect result with id=" + contentDetectLog.getId()
  //                  + " cause by: empty url");
        }

    }
}
