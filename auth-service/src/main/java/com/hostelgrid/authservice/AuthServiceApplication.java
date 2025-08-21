package com.hostelgrid.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

/*
 * Main application class for the Auth Service.
 * Enables service discovery using Spring Cloud Discovery Client.
 * Enables Kafka messaging using Spring Kafka.
 * Scans for components in the specified packages.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@ComponentScan(basePackages = {"com.hostelgrid.authservice", "com.hostelgrid.common"})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}
