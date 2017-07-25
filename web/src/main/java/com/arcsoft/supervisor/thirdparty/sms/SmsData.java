package com.arcsoft.supervisor.thirdparty.sms;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Bean class for the node {@code SMS} of xml.
 *
 * @author zw.
 */
@XmlRootElement(name = "SMS")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmsData {

    public static final String VALIDATION = "POWERU-SMS";

    @XmlAttribute
    private String type = "send";

    @XmlElementRef
    private List<Message> messages;

    public SmsData() {
    }

    public SmsData(String type, List<Message> messages) {
        this.type = type;
        this.messages = messages;
    }

    public SmsData(List<Message> messages) {
        this.messages = messages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SmsData{");
        sb.append("type='").append(type).append('\'');
        sb.append(", messages=").append(messages);
        sb.append('}');
        return sb.toString();
    }
}
