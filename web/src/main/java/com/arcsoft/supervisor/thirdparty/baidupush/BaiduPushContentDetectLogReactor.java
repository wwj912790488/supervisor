package com.arcsoft.supervisor.thirdparty.baidupush;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.alarm.AlarmBaiduPushService;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.settings.AlarmConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * Reactor implementation for push content detect log to remote.
 *
 * @author zw.
 */

@Service("BaiduPushContentDetectLogReactor")
public class BaiduPushContentDetectLogReactor extends ServiceSupport implements ContentDetectLogReactor {

    private final AlarmConfigurationService alarmConfigurationService;

    private final AlarmBaiduPushService alarmBaiduPushService;

    @Autowired
    public BaiduPushContentDetectLogReactor(AlarmConfigurationService alarmConfigurationService,
                                            AlarmBaiduPushService alarmBaiduPushService) {
        this.alarmConfigurationService = alarmConfigurationService;
        this.alarmBaiduPushService = alarmBaiduPushService;
    }

    @Override
    public String getName() {
        return "BaiduPushContentDetectLogReactor";
    }

    @Override
    public void react(ContentDetectLog contentDetectLog) {
        if(contentDetectLog.getEndTime() != 0) {
            ContentDetectLogData detectData = new ContentDetectLogData(
                    contentDetectLog.getChannelId(),
                    contentDetectLog.getChannelName(),
                    contentDetectLog.getType(),
                    contentDetectLog.getStartTimeAsDate(),
                    contentDetectLog.getEndTimeAsDate()
            );

            try {
                alarmBaiduPushService.updateAndPush(detectData);
            } catch (Exception e) {
                logger.error("Failed to baidu push content detect log with id=" + contentDetectLog.getId()
                        + " cause by: " + e.getMessage());
            }
        }
    }
}
