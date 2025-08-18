package com.hostelgrid.common.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Utility class for generating and validating JWT tokens.
*/

@Component
public class JwtTokenProvider {

    private static final String USER_EMAIL = "user_email";
    private static final String USER_ROLE = "user_role";

    @Value("${jwt.secret:2SgX1+NxT1Zje9p6qR1aOYa3+fZK9KZd6SeZXYMgzo4=}") // Base64-encoded secret key
    private String secretKey;

    private SecretKey key;

    private static final long ACCESS_TOKEN_EXPIRATION = 1800000; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new JwtConfigurationException("JWT secret key is missing! Check application.yml or env variables.");
        }
        try {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (IllegalArgumentException e) {
            throw new JwtConfigurationException("Invalid Base64-encoded secret key: " + e.getMessage());
        }
    }

    /** Generate JWT Access Token */
    public String generateToken(Long userId, String email, String role) {
        try {
            Claims claims = Jwts.claims().setSubject(userId.toString());
            claims.put(USER_EMAIL, email);
            claims.put(USER_ROLE, role);

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (InvalidKeyException e) {
            throw new JwtTokenGenerationException("Error generating JWT token: " + e.getMessage(), e);
        }
    }

    /** Generate Refresh Token */
    public String generateRefreshToken(Long userId, String email, String role) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put(USER_EMAIL, email);
        claims.put(USER_ROLE, role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extract all claims */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Extract subject */
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    /** Extract email */
    public String getUserEmail(String token) {
        return getClaims(token).get(USER_EMAIL, String.class);
    }

    /** Extract role */
    public String getUserRole(String token) {
        return getClaims(token).get(USER_ROLE, String.class);
    }

    /** Validate token */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        }
        return false;
    }

    // ---------- Custom Exceptions ----------
    public static class JwtConfigurationException extends RuntimeException {
        public JwtConfigurationException(String message) {
            super(message);
        }
    }

    public static class JwtTokenGenerationException extends RuntimeException {
        public JwtTokenGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class JwtTokenParsingException extends RuntimeException {
        public JwtTokenParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
