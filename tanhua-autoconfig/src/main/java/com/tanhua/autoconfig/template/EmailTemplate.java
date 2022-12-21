package com.tanhua.autoconfig.template;

import com.tanhua.autoconfig.properties.EmailProperties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailTemplate {
//    private static final String USER = "776239423@qq.com"; // 发件人称号，同邮箱地址※
//    private static final String PASSWORD = "ysdimzjzxfqmbdeg"; // 授权码，开启SMTP时显示※

    private EmailProperties emailProperties;

    public EmailTemplate(EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    private boolean sendMailByQQMail(String to, String text, String title) {

        try {
            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
//            注意发送邮件的方法中，发送给谁的，发送给对应的app，※
//            要改成对应的app。扣扣的改成qq的，网易的要改成网易的。※
//            props.put("mail.smtp.host", "smtp.qq.com");
            props.put("mail.smtp.host", "smtp.qq.com");

            // 发件人的账号
            props.put("mail.user", emailProperties.getUser());
            //发件人的密码
            props.put("mail.password", emailProperties.getPassword());

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendCode(String recevier, String code) {
        String text = "欢迎使用探花交友软件，您本次的验证码为" + code + "。(本验证码在1分钟之内有效，请勿将验证码泄露给他人)";
        String title = "探花交友登录验证码";
        sendMailByQQMail(recevier, text, title);
    }
}
