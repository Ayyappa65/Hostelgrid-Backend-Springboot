package com.hostelgrid.authservice.controller;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostelgrid.authservice.dto.LoginRequest;
import com.hostelgrid.authservice.dto.LoginResponse;
import com.hostelgrid.authservice.dto.SignUpRequest;
import com.hostelgrid.authservice.model.User;
import com.hostelgrid.authservice.repository.UserRepository;
import com.hostelgrid.authservice.service.KafkaProducerService;
import com.hostelgrid.common.response.MessageResponse;
import com.hostelgrid.common.security.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          KafkaProducerService kafkaProducerService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Registers a new user if email and phone number are unique.
     *
     * @param request DTO with user details
     * @return Response with success or error message
     *
     * sends a registration event to Kafka
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid SignUpRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        if(userRepository.existsByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber())) {
            log.warn("Registration failed: Email or phone number already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Email or phone number already registered"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
       
        // Publish user registration event to Kafka
        log.info("Publishing user registration event for email: {}", savedUser.getEmail());
        kafkaProducerService.publishUserRegistration(
            savedUser.getEmail(), 
            savedUser.getUsername(), 
            savedUser.getPhoneNumber()
        );

        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully"));
    }

    /**
     * Authenticates the user and returns a JWT token if successful.
     *
     * @param loginRequest JSON with email and password
     * @return JWT token or error response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
            }

            log.info("generating access and refresh tokens for user: {}", user.getEmail());
            String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().toString());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().toString());

            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken,
                    user.getEmail(),
                    user.getRole().toString()
            );

            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            log.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        } catch (Exception ex) {
            log.error("Unexpected error during login: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed due to server error"));
        }
    }
    
    /**
     * Refreshes the access token using the refresh token.
     *
     * @param request JSON with refresh token
     * @return New JWT token or error response
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
     
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.warn("Refresh token is missing or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Refresh token is required"));
        }

        try {
            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.warn("Invalid or expired refresh token: {}", refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
            }

            // Extract claims
            log.info("Extracting claims from refresh token: {}", refreshToken);
            String userId = jwtTokenProvider.getUserId(refreshToken);
            String email = jwtTokenProvider.getUserEmail(refreshToken);
            String role = jwtTokenProvider.getUserRole(refreshToken);

            // Check if user exists
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            // Check role and ID
            if(!role.equals(user.getRole().toString()) || !user.getId().toString().equals(userId)) {
                log.warn("Role mismatch: expected {}, got {}", role, user.getRole());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
            }

            // Generate new access token
            log.info("Generating new access token for user ID: {}", userId);
            String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString()
            );

            log.info("Token refresh successful for user ID: {}", userId);
            LoginResponse response = new LoginResponse(
                    newAccessToken,
                    refreshToken,
                    user.getEmail(),
                    user.getRole().toString()
            );
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Failed to refresh token: " + e.getMessage()));
        }
   }

}
