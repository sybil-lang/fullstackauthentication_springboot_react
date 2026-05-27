package com.authify.authenticationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for sending emails.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    // Spring Boot Mail Sender
    private final JavaMailSender mailSender;

    // Sender email loaded from application.properties
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    /**
     * Send Welcome Email to newly registered user
     *
     * @param toEmail Receiver Email Address
     * @param name    User Name
     */
    public void sendWelcomeMail(
            String toEmail,
            String name
    ) {

        // Create Email Message Object
        SimpleMailMessage message = new SimpleMailMessage();

        // Sender Email
        message.setFrom(fromEmail);

        // Receiver Email
        message.setTo(toEmail);

        // Email Subject
        message.setSubject("Welcome to Authify");

        // Email Body
        message.setText(
                "Hello " + name + ",\n\n" +
                        "Welcome to Authify Authentication System.\n" +
                        "Your account has been created successfully.\n\n" +
                        "Thank You,\n" +
                        "Authify Team"
        );

        // Send Email
        mailSender.send(message);
    }
}