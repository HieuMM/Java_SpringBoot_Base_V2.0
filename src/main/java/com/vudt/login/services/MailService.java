package com.vudt.login.services;

import javax.mail.MessagingException;
import java.util.Map;

public interface MailService {

    void sendMail(String template, String to, String subject, Map<String, Object> content) throws MessagingException;

    void sendMailWithAttachment(String template, String to, String subject, Map<String, Object> content, String... files) throws MessagingException;
}
