package com.ek.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${alert.recipients}")
    private String recipients;

    public void notifyAdmin(String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(recipients.split(","));
            mail.setFrom(System.getProperty("mail.from", "noreply@yourdomain.com"));
            mail.setSubject("Flipkart Agent Alert");
            mail.setText(message);
            mailSender.send(mail);
            log.info("Notification sent: {}", message);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }
}
