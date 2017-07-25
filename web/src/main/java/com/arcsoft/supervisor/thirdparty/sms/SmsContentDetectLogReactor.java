package com.arcsoft.supervisor.thirdparty.sms;

import com.arcsoft.supervisor.commons.profile.Sms;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.system.Sequence;
import com.arcsoft.supervisor.model.domain.system.SmsWarningConfiguration;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.settings.impl.SmsWarningConfigurationService;
import com.arcsoft.supervisor.service.system.SequenceService;
import com.arcsoft.supervisor.service.user.ProductionUserService;
import com.arcsoft.supervisor.thirdparty.sms.ws.SMS;
import com.arcsoft.supervisor.utils.DateHelper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.net.URL;
import java.util.List;

/**
 * Reactor implementation of content detect for send msg to mobile phone.
 *
 * @author zw.
 */
@Sms
@Service("smsContentDetectLogReactor")
public class SmsContentDetectLogReactor extends ServiceSupport implements ContentDetectLogReactor {

    private final SmsWarningConfigurationService smsWarningConfigurationService;
    private final ProductionUserService userService;
    private final SequenceService sequenceService;

    @Autowired
    public SmsContentDetectLogReactor(
            SmsWarningConfigurationService smsWarningConfigurationService,
            ProductionUserService userService,
            SequenceService sequenceService) {
        this.smsWarningConfigurationService = smsWarningConfigurationService;
        this.userService = userService;
        this.sequenceService = sequenceService;
    }

    @Override
    public String getName() {
        return "SmsContentDetectLogReactor";
    }

    @Override
    public void react(final ContentDetectLog contentDetectLog) {
        if(contentDetectLog.getEndTime() != null && contentDetectLog.getEndTime() > 0) {
            Pair<Boolean, SmsWarningConfiguration> pair = smsWarningConfigurationService.isEnableSend();
            if (pair.getLeft()) {
                try {
                    String xml = convertToXml(contentDetectLog, pair.getRight().getAccount());
                    logger.debug("Ready to send sms with content: " + xml);
                    SMS sms = new SMS(new URL(pair.getRight().getUrl()));
                    String result = sms.getSMSHttpPort().addSMSList(SmsData.VALIDATION, xml);
                    logger.debug("Get result {}", result);
                } catch (Exception e) {
                    logger.error("Failed to send sms cause by: {}", e.getMessage());
                }
            }
        }
    }

    private String convertToXml(final ContentDetectLog log, final String account) {
        List<Message> messages = Lists.transform(userService.getAllPhoneNumbers(), new Function<String, Message>() {
            @Nullable
            @Override
            public Message apply(String phoneNumber) {
                return convertFrom(log, phoneNumber, account);
            }
        });
        SmsData data = new SmsData(messages);
        try {
            JAXBContext jaxb = JAXBContext.newInstance(SmsData.class);
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringBuilderWriter writer = new StringBuilderWriter();
            marshaller.marshal(data, writer);
            return writer.toString();
        } catch (JAXBException e) {
            logger.error("Failed to generate xml content for " + data, e);
        }
        return StringUtils.EMPTY;
    }

    private Message convertFrom(ContentDetectLog log, String phoneNumber, String account) {
        String content = String.format(Message.FORMAT,
                log.getChannelName(),
                log.getTypeTranslate(),
                DateHelper.formatDateTime(log.getStartTimeAsDate()),
                DateHelper.formatDateTime(log.getEndTimeAsDate())
        );
        return new Message(
                sequenceService.updateIncrementAndGet(Sequence.KEY_SMS_ID),
                account,
                phoneNumber,
                content
        );
    }

}
