package com.hostelgrid.hostelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.hostelgrid.hostelservice", "com.hostelgrid.common"})
public class HostelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HostelServiceApplication.class, args);
    }
}