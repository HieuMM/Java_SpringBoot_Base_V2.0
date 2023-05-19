package com.vudt.login.services.impl;

import com.vudt.login.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final String TEMPLATE_FOLDER = "/mail_template/";
    Logger mailLogger = LoggerFactory.getLogger(MailService.class);

    public MailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendMail(String template, String to, String subject, Map<String, Object> content) throws MessagingException {
        mailLogger.info("Begin send mail");
        MimeMessage message = javaMailSender.createMimeMessage(); // init new SimpleMailMessage
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        //add variable
        Context context = new Context();
        context.setVariables(content);

        String contentHtml = templateEngine.process(TEMPLATE_FOLDER + template, context);

        // set recipient, subject, content
        messageHelper.setFrom("noreply@CYglobal.net");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(contentHtml, true);

        System.out.println(contentHtml);
        javaMailSender.send(message); // send mail
        mailLogger.info("Send mail to ".concat(to.concat(" successfully!")));
    }

    // use MimeMessageHelper to send mail with attachment files
    @Override
    public void sendMailWithAttachment(String template, String to, String subject, Map<String, Object> content, String... files) throws MessagingException {
        mailLogger.info("Begin send mail attachment");
        MimeMessage message = javaMailSender.createMimeMessage(); // init new MimeMessage from JavaMailSender
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true); // init MineMessageHelper


        //add variable
        final Context context = new Context();
        content.forEach(context::setVariable);

        String contentHtml = templateEngine.process(this.TEMPLATE_FOLDER + template, context);
        // set recipient, subject, content
        messageHelper.setFrom("noreply@CYglobal.net");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(contentHtml, true);

        // walk through files and attachment
        if (files != null) {
            for (String filePath : files) {
                File file = new File(filePath);
                messageHelper.addAttachment(file.getName(), file);
            }
        }
        javaMailSender.send(message); // send mail
        mailLogger.info("Send mail attachment to ".concat(to.concat(" successfully!")));
    }
}