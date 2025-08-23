package com.hostelgrid.hostelservice.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hostelgrid.common.security.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter to extract user context from API Gateway headers and set Spring Security context.
 */
@Component
@Slf4j
public class UserContextFilter extends OncePerRequestFilter {

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String userId = request.getHeader("X-User-Id");
        String userEmail = request.getHeader("X-User-Email");
        String userRole = request.getHeader("X-User-Role");
        String authenticated = request.getHeader("X-Authenticated");
        
        /*
         * Validate user context headers
         * Only set security context if user is authenticated and all required headers are present
         * If any header is missing or invalid, log a warning and do not set the security context
         */
        if ("true".equals(authenticated) && userId != null && userEmail != null && userRole != null) {
            try {
                Role role = Role.valueOf(userRole.toUpperCase());  // Parse enum safely

                // Build authentication token
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Set security context for user: {} with role: {}", userEmail, role.name());
                
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role received: {}", userRole);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
