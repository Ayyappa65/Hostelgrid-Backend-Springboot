package com.hostelgrid.authservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hostelgrid.common.events.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Publishes a user registration event to Kafka.
     * 
     * @param email
     * @param name
     * @param phone
     */
    public void publishUserRegistration(String email, String name, String phone) {
        try {
            UserRegistrationEvent event = new UserRegistrationEvent(email, name, phone);
            
            kafkaTemplate.send("user-registration", event);
            log.info("Published user registration event for: {}", email);
            
        } catch (Exception e) {
            log.error("Error publishing user registration event", e);
        }
    }
}