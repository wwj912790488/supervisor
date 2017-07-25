package com.arcsoft.supervisor.thirdparty.email;

import com.arcsoft.supervisor.commons.profile.Voice;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.system.MailSenderInfoConfiguration;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.log.ServiceLogService;
import com.arcsoft.supervisor.service.settings.impl.DefaultReportWarningConfigurationService;
import com.arcsoft.supervisor.service.settings.impl.EmailWarningConfigurationService;
import com.arcsoft.supervisor.utils.DateHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * Reactor implementation for push content detect log to remote.
 *
 * @author zw.
 */
@Voice
@Service("emailContentDetectLogReactor")
public class EmailContentDetectLogReactor extends ServiceSupport implements ContentDetectLogReactor {

    //private final WarningPushConfigurationService warningPushConfigurationService;
    private final EmailWarningConfigurationService emailWarningConfigurationService;
    private final DefaultReportWarningConfigurationService defaultReportWarningConfigurationService;

    @Autowired
    public EmailContentDetectLogReactor(EmailWarningConfigurationService emailWarningConfigurationService, DefaultReportWarningConfigurationService defaultReportWarningConfigurationService, ServiceLogService serviceLogService) {
        this.emailWarningConfigurationService = emailWarningConfigurationService;
        this.defaultReportWarningConfigurationService = defaultReportWarningConfigurationService;
    }

    @Override
    public String getName() {
        return "EmailContentDetectLogReactor";
    }

    public boolean IsExit(String type, MailSenderInfoConfiguration mailInfo) {
        if (mailInfo.getTypeBlack() != null && StringUtils.equals(mailInfo.getTypeBlack(), type)) {
            return true;
        } else if (mailInfo.getTypeMute() != null && StringUtils.equals(mailInfo.getTypeMute(), type)) {
            return true;
        } else if (mailInfo.getTypeBass() != null && StringUtils.equals(mailInfo.getTypeBass(), type)) {
            return true;
        } else if (mailInfo.getTypePitch() != null && StringUtils.equals(mailInfo.getTypePitch(), type)) {
            return true;
        } else if (mailInfo.getTypeStatic() != null && StringUtils.equals(mailInfo.getTypeStatic(), type)) {
            return true;
        } else if (mailInfo.getTypeCc() != null && StringUtils.equals(mailInfo.getTypeCc(), type)) {
            return true;
        } else if (mailInfo.getTypeAudio() != null && StringUtils.equals(mailInfo.getTypeAudio(), type)) {
            return true;
        } else if (mailInfo.getTypeVideo() != null && StringUtils.equals(mailInfo.getTypeVideo(), type)) {
            return true;
        } else if (mailInfo.getTypeSignal() != null && StringUtils.equals(mailInfo.getTypeSignal(), type)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void react(ContentDetectLog contentDetectLog) {
        MailSenderInfoConfiguration cfg = emailWarningConfigurationService.find();
        if (cfg != null) {
            if (IsExit(String.valueOf(contentDetectLog.getType()), cfg)) {

                String guid = contentDetectLog.getGuid();
                String channelName = contentDetectLog.getChannelName();
                String logType = String.valueOf(contentDetectLog.getType());
                String startTime = DateHelper.formatDateTime(contentDetectLog.getStartTimeAsDate(), "yyyy-MM-dd HH:mm:ss");
                String endTime = DateHelper.formatDateTime(contentDetectLog.getEndTimeAsDate(), "yyyy-MM-dd HH:mm:ss");
                String taskId = String.valueOf(contentDetectLog.getTaskId());
                String descript = contentDetectLog.getTypeTranslate();

                MailSenderInfoConfiguration mailInfo = new MailSenderInfoConfiguration();
                mailInfo.setMailServerHost(cfg.getMailServerHost());
                mailInfo.setMailServerPort(cfg.getMailServerPort());
                mailInfo.setValidate(true);
                mailInfo.setUserName(cfg.getUserName());
                mailInfo.setPassword(cfg.getPassword());//邮箱密码
                mailInfo.setFromAddress(cfg.getUserName());
                mailInfo.setToAddress(cfg.getToAddress());
                mailInfo.setSubject("Arcvideo Supervisor Alarm");

                String htmlbody = "<table id=\"message-list-table\" border=1 cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\"  align=center>\n" +
                        "    <thead>\n" +
                        "    <tr bgcolor=\"#cccccc\">\n" +
                        "        <th >频道名称</th>\n" +
                        "        <th>告警类型</th>\n" +
                        "        <th >故障开始时间</th>\n" +
                        "        <th >故障结束时间</th>\n" +
                        "        <th>告警数据库id</th>\n" +
                        "    </tr>\n" +
                        "    </thead>\n" +
                        "    <tbody>\n" +
                        "    <tr>\n" +
                        "        <td>" + channelName + "</td>\n" +
                        "        <td>" + descript + "</td>\n" +
                        "        <td>" + startTime + "</td>\n" +
                        "        <td>" + endTime + "</td>\n" +
                        "        <td>" + taskId + "</td>\n" +
                        "        \n" +
                        "    </tr>\n" +
                        "    </tbody>\n" +
                        "    </table>";
                mailInfo.setContent(htmlbody);
                SendMail sms = new SendMail();
                try {
                    boolean flag = sms.sendMessage(mailInfo);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    logger.error("Failed to email content detect result with id=" + contentDetectLog.getId()
                            + " cause by: " + e.getMessage());
                }
            }
        }

    }
}
