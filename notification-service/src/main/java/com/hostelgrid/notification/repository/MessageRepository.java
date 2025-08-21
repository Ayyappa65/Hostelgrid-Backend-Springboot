package com.hostelgrid.notification.repository;

import com.hostelgrid.notification.model.Message;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.hostelgrid.notification.model.MessageStatus;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    boolean existsByIdempotencyKey(String idempotencyKey);
    
    List<Message> findByStatus(MessageStatus status);
    
    List<Message> findByRecipient(String recipient);
    
    List<Message> findByStatusAndRetryCountLessThan(MessageStatus status, Integer maxRetries);
}