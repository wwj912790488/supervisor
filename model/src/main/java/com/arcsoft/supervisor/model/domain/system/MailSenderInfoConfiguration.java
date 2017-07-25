package com.arcsoft.supervisor.model.domain.system;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Properties;

/**
 * Created by wwj on 2017/2/16.
 */
@Entity
@Table(name = "configuration_warning_email")
@DiscriminatorValue("5")
public class MailSenderInfoConfiguration extends Configuration {
    // 发送邮件的服务器的IP和端口
    private String mailServerHost;
    private String mailServerPort = "25";

    // 邮件发送者的地址
    private String fromAddress;

    // 邮件接收者的地址 多个
    private String toAddress;

    // 登陆邮件发送服务器的用户名和密码
    private String userName;
    private String password;

    // 是否需要身份验证
    private boolean validate = false;

    // 邮件主题
    private String subject;

    private boolean choosessl = false;

    // 邮件的文本内容
    private String content;

    // 邮件附件的文件名
    private String[] attachFileNames;


    private String typeBlack;

    private String typeMute;

    private String typeBass;

    private String typePitch;

    private String typeStatic;

    private String typeCc;

    private String typeAudio;

    private String typeVideo;

    private String typeSignal;


    public MailSenderInfoConfiguration() {

    }

    public MailSenderInfoConfiguration(String[] attachFileNames, boolean choosessl, String content, String fromAddress, String mailServerHost, String mailServerPort, String password, String subject, String toAddress, String typeAudio, String typeBass, String typeBlack, String typeCc, String typeMute, String typePitch, String typeSignal, String typeStatic, String typeVideo, String userName, boolean validate) {
        this.attachFileNames = attachFileNames;
        this.choosessl = choosessl;
        this.content = content;
        this.fromAddress = fromAddress;
        this.mailServerHost = mailServerHost;
        this.mailServerPort = mailServerPort;
        this.password = password;
        this.subject = subject;
        this.toAddress = toAddress;
        this.typeAudio = typeAudio;
        this.typeBass = typeBass;
        this.typeBlack = typeBlack;
        this.typeCc = typeCc;
        this.typeMute = typeMute;
        this.typePitch = typePitch;
        this.typeSignal = typeSignal;
        this.typeStatic = typeStatic;
        this.typeVideo = typeVideo;
        this.userName = userName;
        this.validate = validate;
    }

    /**
     * 获得邮件会话属性
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isChoosessl() {
        return choosessl;
    }

    public boolean getChoosessl() {
        return choosessl;
    }

    public void setChoosessl(boolean choosessl) {
        this.choosessl = choosessl;
    }

    public String[] getAttachFileNames() {
        return attachFileNames;
    }

    public void setAttachFileNames(String[] fileNames) {
        this.attachFileNames = fileNames;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String textContent) {
        this.content = textContent;
    }

    public String getTypeAudio() {
        return typeAudio;
    }

    public void setTypeAudio(String typeAudio) {
        this.typeAudio = typeAudio;
    }

    public String getTypeBass() {
        return typeBass;
    }

    public void setTypeBass(String typeBass) {
        this.typeBass = typeBass;
    }

    public String getTypeBlack() {
        return typeBlack;
    }

    public void setTypeBlack(String typeBlack) {
        this.typeBlack = typeBlack;
    }

    public String getTypeCc() {
        return typeCc;
    }

    public void setTypeCc(String typeCc) {
        this.typeCc = typeCc;
    }

    public String getTypeMute() {
        return typeMute;
    }

    public void setTypeMute(String typeMute) {
        this.typeMute = typeMute;
    }

    public String getTypePitch() {
        return typePitch;
    }

    public void setTypePitch(String typePitch) {
        this.typePitch = typePitch;
    }

    public String getTypeSignal() {
        return typeSignal;
    }

    public void setTypeSignal(String typeSignal) {
        this.typeSignal = typeSignal;
    }

    public String getTypeStatic() {
        return typeStatic;
    }

    public void setTypeStatic(String typeStatic) {
        this.typeStatic = typeStatic;
    }

    public String getTypeVideo() {
        return typeVideo;
    }

    public void setTypeVideo(String typeVideo) {
        this.typeVideo = typeVideo;
    }

    public String ToStringExit(){
        return this.isEmpty(this.getTypeBlack())+";"+this.getTypeCc();

    }
    public String isEmpty(String obj){
        return  StringUtils.isEmpty(obj)?"":obj;
    }
}
