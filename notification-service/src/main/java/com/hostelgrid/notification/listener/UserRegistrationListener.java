package com.hostelgrid.notification.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.hostelgrid.common.events.UserRegistrationEvent;
import com.hostelgrid.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationListener {
    
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "user-registration", groupId = "notification-service")
    public void handleUserRegistration(UserRegistrationEvent event) {
        try {
            log.info("Received user registration event: {}", event);

            // Send welcome email
            notificationService.sendWelcomeEmail(event.getEmail(), event.getName());
            
            log.info("Processed user registration for: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Error processing user registration event: {}", event, e);
        }
    }
}