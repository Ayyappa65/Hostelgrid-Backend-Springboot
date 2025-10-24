package com.hostelgrid.studentservice.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtContextFilter extends OncePerRequestFilter {

    private static final ThreadLocal<String> jwtTokenHolder = new ThreadLocal<>();

    @SuppressWarnings("null")
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtTokenHolder.set(authHeader);
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            jwtTokenHolder.remove();
        }
    }

    public static String getCurrentJwtToken() {
        return jwtTokenHolder.get();
    }
}