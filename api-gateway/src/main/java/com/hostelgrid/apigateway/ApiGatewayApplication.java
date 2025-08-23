package com.hostelgrid.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/*
 * Main entry point for the API Gateway application.
 * Excludes Hibernate and JDBC auto-configuration to avoid conflicts.
 * Enables service discovery for the API Gateway to communicate with other services.
 */
@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.hostelgrid.apigateway", "com.hostelgrid.common"})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}