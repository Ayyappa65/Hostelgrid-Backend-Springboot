package com.hostelgrid.apigateway.filter;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.hostelgrid.common.security.JwtTokenProvider;

import reactor.core.publisher.Mono;

/**
 * API Gateway Filter that verifies JWT token authentication and propagates user identity.
 * 
 * <h3>Authentication Flow:</h3>
 * <ul>
 *   <li>WebFilter intercepts all requests before routing</li>
 *   <li>Validates JWT tokens using JwtTokenProvider from common-module</li>
 *   <li>Extracts user context (ID, email, role) from token</li>
 *   <li>Sets Security Context with user authentication</li>
 *   <li>Forwards headers to downstream services:</li>
 *   <ul>
 *     <li>X-User-Id</li>
 *     <li>X-User-Email</li>
 *     <li>X-User-Role</li>
 *     <li>X-Authenticated</li>
 *   </ul>
 * </ul>
 * 
 * <h3>Public Endpoints:</h3>
 * <ul>
 *   <li>/api/v1/auth/** - Authentication endpoints</li>
 *   <li>/swagger-ui/** - API documentation</li>
 *   <li>/actuator/** - Health endpoints</li>
 * </ul>
 * 
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Uses existing JwtTokenProvider from common-module</li>
 *   <li>Sets reactive security context</li>
 *   <li>Forwards user context to all downstream services</li>
 *   <li>Hostel-service can use method-level security with forwarded user info</li>
 * </ul>
 */
@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    /* JWT Authentication Filter 
     * Validates JWT tokens and sets user context for downstream services.
     * Bypasses authentication for public endpoints.
     * Logs authentication attempts and errors.
    */
    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.info("Request path: {}", path);

        // Allow public endpoints to bypass authentication
        if (path.startsWith("/api/v1/auth/") || 
            path.startsWith("/actuator/") ||
            path.endsWith("/active")) {
            log.debug("Bypassing authentication for public endpoint: {}", path);
            return chain.filter(exchange);
        }
        if (path.startsWith("/webjars") || path.startsWith("/swagger-ui") || path.startsWith("/api-docs/v1") || path.equals("/swagger-ui.html")) {
            log.debug("Bypassing authentication for documentation endpoint: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader==null || !authHeader.startsWith("Bearer ")) {
            log.warn("Unauthorized request - Missing or invalid Authorization header for path: {}", path);
            return unauthorizedResponse(exchange.getResponse(), "Missing or invalid Authorization header");
        }

        try {
            // Extract and validate token
            String token = authHeader.substring(7);
            
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("Unauthorized request - Invalid JWT token");
                return unauthorizedResponse(exchange.getResponse(), "Invalid JWT token");
            }

            // Extract user information from token
            String userId = jwtTokenProvider.getUserId(token);
            String userEmail = jwtTokenProvider.getUserEmail(token);
            String userRole = jwtTokenProvider.getUserRole(token);

            if (userId==null || userEmail==null || userRole==null) {
                log.warn("Unauthorized request - Missing user details in token. UserId: {}, Email: {}, Role: {}", 
                    userId, userEmail, userRole);
                return unauthorizedResponse(exchange.getResponse(), "Missing required user details in token");
            }

            log.info("Authenticated request - User: {}, User ID: {}, Role: {}, Path: {}", 
                userEmail, userId, userRole, path);

            // Create authentication with authorities
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userEmail,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole))
            );

            // Set the authentication in the security context
            SecurityContext securityContext = new SecurityContextImpl(authentication);

            // Propagate user identity to downstream services
            /*
             * Here we add custom headers to the request to forward user context to downstream services(client services).
             */
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .header("X-User-Id", userId)
                    .header("X-User-Email", userEmail)
                    .header("X-User-Role", userRole)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        } catch (Exception e) {
            log.error("Authentication processing error: ", e);
            return unauthorizedResponse(exchange.getResponse(), "Authentication processing error: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String errorMessage) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("X-Error-Message", errorMessage);
        return response.setComplete();
    }
}