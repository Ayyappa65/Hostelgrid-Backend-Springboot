package com.hostelgrid.hostelservice.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hostelgrid.common.security.JwtTokenProvider;
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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /*
     * Filter to validate JWT from Authorization header and set Spring Security context.
     * If the token is valid, extract user details and roles to create an authentication token.
     * If the token is missing or invalid, do not set the security context.
     * Proceed with the filter chain regardless of authentication outcome.
     * Log relevant information for debugging purposes.
     */
    @SuppressWarnings({ "null", "unused" })
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getUserId(token);
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
                log.debug("JWT: Set security context for user: {} with role: {}", userEmail, role.name());
                
            } catch (IllegalArgumentException e) {
                log.warn("JWT: Invalid role received: {}", userRole);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
