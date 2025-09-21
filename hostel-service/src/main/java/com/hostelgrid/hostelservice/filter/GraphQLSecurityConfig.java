package com.hostelgrid.hostelservice.filter;
 
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hostelgrid.common.security.JwtTokenProvider;
import com.hostelgrid.common.security.Role;
 
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/*
 * GraphQL Security configuration for the Hostel Service.
 * Authentication: Handled at API Gateway (JWT validation, user identity)
 * GraphQL Security: All requests permitted (no authentication check at service level)
 * Method Authorization: Still enforced via @PreAuthorize annotations
 * Method Security: Enabled with @EnableMethodSecurity(prePostEnabled = true)
 */
@Configuration
@Slf4j
public class GraphQLSecurityConfig {
 
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    @SuppressWarnings("Convert2Lambda")
    public WebGraphQlInterceptor authenticationInterceptor() {
       
        return new WebGraphQlInterceptor() {
           
            @SuppressWarnings("null")
            @Override
            public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
                return chain.next(request)
                    .doFirst(() -> {
                        String token = getTokenFromRequest(request);

                        if (token != null && jwtTokenProvider.validateToken(token)) {
                            String userEmail = jwtTokenProvider.getUserEmail(token);
                            String userRole = jwtTokenProvider.getUserRole(token);
                
                            try {
                                Role role = Role.valueOf(userRole.toUpperCase());

                                UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                        userEmail,
                                        null,
                                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                                    );
                               
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                log.debug("GraphQL: Set security context for user: {} with role: {}", userEmail, role.name());
                               
                            } catch (IllegalArgumentException e) {
                                log.warn("GraphQL: Invalid role received: {}", userRole);
                            }
                        }
                    });
            }
        };
    }

    private String getTokenFromRequest(WebGraphQlRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

