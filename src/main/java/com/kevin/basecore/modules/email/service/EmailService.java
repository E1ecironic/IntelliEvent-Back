package com.kevin.basecore.modules.email.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送简单文本邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 批量发送简单文本邮件
     * @param tos 收件人列表
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleMailBatch(List<String> tos, String subject, String content);

    /**
     * 发送HTML邮件
     * @param to 收件人
     * @param subject 主题
     * @param content HTML内容
     */
    void sendHtmlMail(String to, String subject, String content);

    /**
     * 批量发送HTML邮件
     * @param tos 收件人列表
     * @param subject 主题
     * @param content HTML内容
     */
    void sendHtmlMailBatch(List<String> tos, String subject, String content);

    /**
     * 发送带附件的邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param filePath 附件路径
     */
    void sendAttachmentsMail(String to, String subject, String content, String filePath);
}
