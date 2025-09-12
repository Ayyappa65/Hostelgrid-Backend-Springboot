package com.hostelgrid.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api-docs/v1")
public class ApiDocsController {

    private final RestTemplate restTemplate = new RestTemplate();


    //  Fetch API Docs from Auth Service
    @GetMapping("/auth-service")
    public ResponseEntity<String> getAuthServiceDocs() {
        String authServiceDocs = restTemplate.getForObject("http://localhost:8081/api-docs/v1", String.class);
        return ResponseEntity.ok(authServiceDocs);
    }

    //  Fetch API Docs from Billing Service
    @GetMapping("/billing-service")
    public ResponseEntity<String> getBillingServiceDocs() {
        String billingServiceDocs = restTemplate.getForObject("http://localhost:8082/api-docs/v1", String.class);
        return ResponseEntity.ok(billingServiceDocs);
    }

    //  Fetch API Docs from Hostel Service
    @GetMapping("/hostel-service")
    public ResponseEntity<String> getHostelServiceDocs() {
        String hostelServiceDocs = restTemplate.getForObject("http://localhost:8083/api-docs/v1", String.class);
        return ResponseEntity.ok(hostelServiceDocs);
    }

    //  Fetch API Docs from Notification Service
    @GetMapping("/notification-service")
    public ResponseEntity<String> getNotificationServiceDocs() {
        String notificationServiceDocs = restTemplate.getForObject("http://localhost:8084/api-docs/v1", String.class);
        return ResponseEntity.ok(notificationServiceDocs);
    }

    //  Fetch API Docs from Student Service
    @GetMapping("/student-service")
    public ResponseEntity<String> getStudentServiceDocs() {
        String studentServiceDocs = restTemplate.getForObject("http://localhost:8085/api-docs/v1", String.class);
        return ResponseEntity.ok(studentServiceDocs);
    }

}