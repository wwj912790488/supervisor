package com.arcsoft.supervisor.thirdparty.sms;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * Bean class for node {@code Message} of xml.
 *
 * @author zw.
 */
@XmlRootElement(name = "Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message {

    public static final String FORMAT = "%1$s 出现%2$s,开始时间:%3$s,结束时间:%4$s";

    @XmlAttribute(name = "SmsID")
    private String id;

    @XmlAttribute(name = "Bid")
    private String businessId = "ghyunpingtai";

    @XmlAttribute(name = "RecvNum")
    private String receivePhoneNumber;

    @XmlAttribute(name = "Content")
    private String content;

    public Message() {
    }

    public Message(long id, String receivePhoneNumber, String content) {
        this.setId(id);
        this.receivePhoneNumber = receivePhoneNumber;
        this.content = content;
    }

    public Message(long id, String businessId, String receivePhoneNumber, String content) {
        this.setId(id);
        this.businessId = businessId;
        this.receivePhoneNumber = receivePhoneNumber;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(long seq) {
        this.id = "UE08" + StringUtils.leftPad(String.valueOf(seq), 14, '0') + "0";
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getReceivePhoneNumber() {
        return receivePhoneNumber;
    }

    public void setReceivePhoneNumber(String receivePhoneNumber) {
        this.receivePhoneNumber = receivePhoneNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("id='").append(id).append('\'');
        sb.append(", businessId='").append(businessId).append('\'');
        sb.append(", receivePhoneNumber='").append(receivePhoneNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
