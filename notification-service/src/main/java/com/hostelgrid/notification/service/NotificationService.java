package com.hostelgrid.notification.service;

import java.time.LocalDateTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hostelgrid.notification.model.Channel;
import com.hostelgrid.notification.model.Message;
import com.hostelgrid.notification.model.MessageStatus;
import com.hostelgrid.notification.repository.MessageRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {
    
    private final JavaMailSender mailSender;
    private final MessageRepository messageRepository;

    public NotificationService(JavaMailSender mailSender, MessageRepository messageRepository) {
        this.mailSender = mailSender;
        this.messageRepository = messageRepository;
    }

    private static final String WELCOME_SUBJECT = "🎉 Welcome to HostelGrid!";
    
    private static final String WELCOME_BODY = """
        Hello %s,

        Welcome to HostelGrid! 🎉
        
        Your account has been created successfully. You can now:
        - Book your hostel bed online
        - Track your occupancy and payments
        - Manage check-ins and check-outs easily

        👉 Login here: https://hostelgrid.com/login

        Cheers,
        The HostelGrid Team
        """;


    /** 
    * Send Welcome Email using template
    * Async method to send welcome email here async means that the method will be executed in a separate thread,
    * allowing the main thread to continue processing without waiting for the email to be sent.
    */
    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void sendWelcomeEmail(String email, String name) {
        String idempotencyKey = "welcome-" + email + "-" + System.currentTimeMillis();
        
        // Check idempotency
        if (messageRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.info("Message already sent for key: {}", idempotencyKey);
            return;
        }

        Message message = new Message();
        message.setIdempotencyKey(idempotencyKey);
        message.setRecipient(email);
        message.setSubject(WELCOME_SUBJECT);
        message.setBody(String.format(WELCOME_BODY, name, email, "https://hostelgrid.com/login"));
        message.setChannel(Channel.EMAIL);
        
        // try catch block for sending email
        try {
            sendEmail(message);
            message.setStatus(MessageStatus.SENT);
            message.setSentAt(LocalDateTime.now());
            log.info("Welcome email sent to: {}", email);
        } catch (Exception e) {

            message.setStatus(MessageStatus.FAILED);
            message.setErrorMessage(e.getMessage());
            message.setRetryCount(message.getRetryCount() + 1);
            log.error("Failed to send welcome email to: {}", email, e);

            throw e;
        } finally {
            messageRepository.save(message);
        }
    }

    // Method to send email
    private void sendEmail(Message message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(message.getRecipient());
        mailMessage.setSubject(message.getSubject());
        mailMessage.setText(message.getBody());
        // mailMessage.setFrom("noreply@hostelgrid.com");

        mailSender.send(mailMessage);
    }
}