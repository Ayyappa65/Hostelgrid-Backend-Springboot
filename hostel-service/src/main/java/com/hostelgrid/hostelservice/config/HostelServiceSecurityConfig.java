package com.hostelgrid.hostelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

/*
 * Security configuration for the Hostel Service.
 * Authentication: Handled at API Gateway (JWT validation, user identity)
 * HTTP Security: All requests permitted (no authentication check at service level)
 * Method Authorization: Still enforced via @PreAuthorize annotations
 * Method Security: Enabled with @EnableMethodSecurity(prePostEnabled = true)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class HostelServiceSecurityConfig {

    /**
     * Security filter chain for HTTP requests.
     * Authentication is handled at API Gateway level.
     * Method-level authorization still applies via @PreAuthorize.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.debug("Configuring security filter chain of hostel-service");
        
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll() // Allow access to actuator endpoints
                .requestMatchers("/api/public/**").permitAll() // Allow access to public API endpoints

                //this is the correct approach for gateway-authenticated microservices.
                // All requests are permitted; method-level security still applies
                .anyRequest().permitAll()
            );
        return http.build();
    }

}
