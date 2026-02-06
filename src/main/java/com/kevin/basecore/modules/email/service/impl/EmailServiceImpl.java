package com.kevin.basecore.modules.email.service.impl;

import com.kevin.basecore.modules.email.service.EmailService;
import com.kevin.basecore.modules.system.service.SysConfigService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private SysConfigService sysConfigService;

    private JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        String host = sysConfigService.getValue("spring.mail.host", "smtp.qq.com");
        String port = sysConfigService.getValue("spring.mail.port", "465");
        String username = sysConfigService.getValue("spring.mail.username");
        String password = sysConfigService.getValue("spring.mail.password");
        
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new RuntimeException("邮件配置缺失：请在系统配置中设置 spring.mail.username 和 spring.mail.password");
        }

        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "true");
        
        return mailSender;
    }

    private String getFrom() {
        return sysConfigService.getValue("spring.mail.username");
    }

    @Async
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            JavaMailSender mailSender = getJavaMailSender();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("简单邮件已经发送。指派人：{}", to);
        } catch (MailAuthenticationException e) {
            log.error("邮件认证失败：请检查 spring.mail.username 是否正确，以及 spring.mail.password 是否为有效的授权码（非登录密码），并确认邮箱已开启 SMTP 服务。", e);
        } catch (Exception e) {
            log.error("发送简单邮件时发生异常！", e);
        }
    }

    @Async
    @Override
    public void sendSimpleMailBatch(List<String> tos, String subject, String content) {
        if (tos == null || tos.isEmpty()) {
            return;
        }
        log.info("开始批量发送简单邮件，人数：{}", tos.size());
        for (String to : tos) {
            sendSimpleMail(to, subject, content);
        }
    }

    @Async
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            JavaMailSender mailSender = getJavaMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            // true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("html邮件发送成功");
        } catch (MailAuthenticationException e) {
            log.error("邮件认证失败：请检查 spring.mail.username 是否正确，以及 spring.mail.password 是否为有效的授权码（非登录密码），并确认邮箱已开启 SMTP 服务。", e);
        } catch (Exception e) {
            log.error("发送html邮件时发生异常！", e);
        }
    }

    @Async
    @Override
    public void sendHtmlMailBatch(List<String> tos, String subject, String content) {
        if (tos == null || tos.isEmpty()) {
            return;
        }
        log.info("开始批量发送HTML邮件，人数：{}", tos.size());
        for (String to : tos) {
            sendHtmlMail(to, subject, content);
        }
    }

    @Async
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        try {
            JavaMailSender mailSender = getJavaMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);

            mailSender.send(message);
            log.info("带附件的邮件已经发送。");
        } catch (MailAuthenticationException e) {
            log.error("邮件认证失败：请检查 spring.mail.username 是否正确，以及 spring.mail.password 是否为有效的授权码（非登录密码），并确认邮箱已开启 SMTP 服务。", e);
        } catch (Exception e) {
            log.error("发送带附件的邮件时发生异常！", e);
        }
    }
}
