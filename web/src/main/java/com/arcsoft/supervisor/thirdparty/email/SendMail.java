package com.arcsoft.supervisor.thirdparty.email;

import com.arcsoft.supervisor.model.domain.system.MailSenderInfoConfiguration;

import java.security.Security;
import java.util.*;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by wwj on 2017/2/16.
 */
public class SendMail {
    @SuppressWarnings("static-access")
    public boolean sendMessage(MailSenderInfoConfiguration mailInfo) throws MessagingException {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }

        //判断是否是ssl
        if (mailInfo.isChoosessl()) {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            pro.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            pro.setProperty("mail.smtp.socketFactory.fallback", "false");
            pro.setProperty("mail.smtp.socketFactory.port", "465");

        }

        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            for (String toEmail : mailInfo.getToAddress().split(";")) {
                toEmail = toEmail.trim();
                if (toEmail.length() > 0)
                    mailMessage.addRecipient(RecipientType.TO, new InternetAddress(toEmail));
            }

            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());

            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());

            // 设置邮件消息的主要内容
           // String mailContent = mailInfo.getContent();

            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();

            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();



            // 设置HTML内容
            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");

            mainPart.addBodyPart(html);

            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);

            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return  false;
        }
    }

   /* public static void main(String[] args) {
        MailSenderInfoConfiguration mailInfo = new MailSenderInfoConfiguration();
        mailInfo.setMailServerHost("smtp.qq.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("912790488@qq.com");
        mailInfo.setPassword("502035205");//邮箱密码
        mailInfo.setFromAddress("912790488@qq.com");
        mailInfo.setToAddress("912790488@qq.com");
        mailInfo.setSubject("manyto To Emial ");
        mailInfo.setContent("hello world");
        //这个类主要来发送邮件
        SendMail sms = new SendMail();
        try {
            sms.sendMessage(mailInfo);//是否ssl
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }*/
}

class MyAuthenticator extends Authenticator {
    String userName = "";
    String password = "";

    public MyAuthenticator() {

    }

    public MyAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
