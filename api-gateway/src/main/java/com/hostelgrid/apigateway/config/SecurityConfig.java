package com.hostelgrid.apigateway.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Security configuration for API Gateway
     * This configuration sets up security filters for the API Gateway, including CORS and authentication.
     * It allows public access to certain APIs and requires authentication for others.
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        log.info("Configuring API Gateway security filter chain");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public APIs without authentication
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/public/**").permitAll()

                        // Actuator endpoints
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/*/actuator/**").permitAll()  // allow actuator endpoints through the gateway:

                        // Swagger / API Docs (optional)
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // All other APIs require authentication
                        .anyExchange().authenticated()
                )
                .build();
    }

    /**
     * Global CORS configuration for API Gateway
     * This configuration allows cross-origin requests to the API Gateway.
     * It sets the allowed origins, methods, and headers for CORS.
     */
    @Bean
    public CorsWebFilter corsWebFilter() {

        log.info("Configuring CORS and authentication for API Gateway");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // Allowed frontend origins
        config.setAllowedOrigins(List.of(
                "http://localhost:5173", // Local React Dev
                "https://your-production-frontend.com" // Production domain
        ));

        // Allowed methods
        config.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));

        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
