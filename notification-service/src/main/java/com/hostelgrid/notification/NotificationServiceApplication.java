package com.hostelgrid.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/*
 * Enables the Notification Service application with necessary configurations.
 * - @SpringBootApplication: Marks this as a Spring Boot application.
 * - @EnableDiscoveryClient: Enables service discovery for the application.
 * - @EnableKafka: Enables Kafka messaging for the application.
 * - @EnableAsync: Enables asynchronous processing for the application.(it is used for sending email notifications asynchronously)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableAsync
@ComponentScan(basePackages = {"com.hostelgrid.notification", "com.hostelgrid.common"})
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}