package com.hostelgrid.hostelservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hostelgrid.hostelservice.filter.UserContextFilter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
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

    @Autowired
    private UserContextFilter userContextFilter;


    /**
     * OpenAPI configuration.
     * Sets up API info, server, and security schemes for JWT Bearer authentication.
     * Applies security globally to all endpoints.
     * Configures OpenAPI documentation.
     * To access the Swagger UI, visit http://localhost:8083/swagger-ui/index.html
     */
    @Bean
    public OpenAPI v1OpenAPI() {
        // Define security scheme name
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Hostel Service API")
                        .version("v1")
                        .description("Version 1 of Hostel Service API"))
                .addServersItem(new Server().url("http://localhost:8080").description("Hostel Service API v1"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Apply globally
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }

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
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                //this is the correct approach for gateway-authenticated microservices.
                // All requests are permitted; method-level security still applies
                .anyRequest().permitAll()
            )
            // Add custom filter to extract user context from headers set by API Gateway
            .addFilterBefore(userContextFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
