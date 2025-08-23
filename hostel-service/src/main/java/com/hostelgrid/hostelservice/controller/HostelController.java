package com.hostelgrid.hostelservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hostelgrid.common.response.MessageResponse;
import com.hostelgrid.hostelservice.dto.HostelDto;
import com.hostelgrid.hostelservice.dto.HostelDto.HostelResponseDto;
import com.hostelgrid.hostelservice.service.HostelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Hostel management operations.
 * Provides CRUD endpoints with role-based access control.
 */
@RestController
@RequestMapping("/api/v1/hostels")
@RequiredArgsConstructor
@Slf4j
public class HostelController {
    
    private final HostelService hostelService;
    
    /**
     * Create a new hostel.
     * Only ADMIN and OWNER roles can create hostels.
     * 
     * @param createDto - Hostel creation data
     * @return Created hostel response
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageResponse> createHostel(@Valid @RequestBody HostelDto.CreateHostelDto createDto) {
        log.info("REST request to create hostel: {}", createDto.getName());
        hostelService.createHostel(createDto);
        log.info("Hostel created successfully via REST: {}", createDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Hostel Created Successfully!"));
    }
    
    
    /**
     * Update hostel information.
     * Only ADMIN and OWNER roles can update hostels.
     * 
     * @param updateDto - Update data
     * @return Updated hostel details
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<MessageResponse> updateHostel(@Valid @RequestBody HostelDto.HostelUpdateDto updateDto) {

        log.info("REST request to update hostel ID: {}", updateDto.getId());

        hostelService.updateHostel(updateDto.getId(), updateDto);
        return ResponseEntity.ok(new MessageResponse("Hostel updated successfully!"));
    }

    /**
     * Update hostel status.
     */
    @PutMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateHostelsByStatus(@RequestBody HostelDto.HostelStatusUpdate statusUpdate) {
        log.info("REST request to update hostel id {} status: {}", statusUpdate.getStatus(), statusUpdate.getId());

        hostelService.updateHostelStatus(statusUpdate);
        log.info("Updated hostel id {} status to {} via REST", statusUpdate.getId(), statusUpdate.getStatus());

        return ResponseEntity.ok(new MessageResponse("Hostel status updated successfully!"));
    }

    /**
     * Delete hostel (soft delete).
     * Only ADMIN role can delete hostels.
     * 
     * @param id - Hostel ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteHostel(@Valid @PathVariable Long id) {
        log.info("REST request to delete hostel ID: {}", id);
        hostelService.deleteHostel(id);
        log.info("Hostel deleted successfully via REST: {}", id);
        return ResponseEntity.ok(new MessageResponse("Hostel deleted successfully!"));

    }

    /**
     * Get hostel by ID.
     * All authenticated users can view hostel details.
     * 
     * @param id - Hostel ID
     * @return Hostel details or 404 if not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'USER')")
    public ResponseEntity<Optional<HostelResponseDto>> getHostelById(@PathVariable Long id) {
        if(id == null || id <= 0) {
            log.warn("Invalid hostel ID provided via REST: {}", id);
            return ResponseEntity.badRequest().build();
        }
        log.info("REST request to get hostel by ID: {}", id);

        return ResponseEntity.ok(hostelService.getHostelById(id));
    }
    
    /**
     * Get all hostels.
     * Only users with HOSTEL_VIEW permission can access.
     * 
     * @return List of all hostels
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<HostelResponseDto>> getAllHostels() {
        log.info("REST request to get all hostels");
        
        List<HostelResponseDto> hostels = hostelService.getAllHostels();
        log.info("Retrieved {} hostels via REST", hostels.size());
        
        return ResponseEntity.ok(hostels);
    }
    
    /**
     * Get all active hostels.
     * Public endpoint for active hostels.
     * 
     * API Gateway Level:
     * // In JwtAuthenticationFilter.java
     * if (path.startsWith("/api/v1/auth/") || 
     *     path.startsWith("/actuator/") ||
     *     path.endsWith("/active")) {  // <-- This line makes /active public
     *       log.debug("Bypassing authentication for public endpoint: {}", path);
     *       return chain.filter(exchange);
     *   }
     * 
     * Hostel Service Level:
     * // In HostelServiceSecurityConfig.java
     * .authorizeHttpRequests(auth -> auth
     *     .anyRequest().permitAll()  // <-- All HTTP requests permitted
     * );
     * 
     * Controller : 
     * No @PreAuthorize annotation → no method-level security
     * 
     * Result : 
     * Public access without any authentication/authorization
     * This api can be accessed by anyone without any restrictions.
     * This api can be accessed via apigateway and directly.
     *
     * 
     * If we keep any of these checks, it will block public access:
     * -----------
     * .authorizeHttpRequests(auth -> auth
     *     .anyRequest().authenticated() // <-- All HTTP requests authenticated
     * );
     * 
     * or 
     * 
     * @PreAuthorize("hasAnyRole('ADMIN')")
     * 
     * @return List of active hostels
     */
    @GetMapping("/active") 
    public ResponseEntity<List<HostelDto.HostelResponseDto>> getAllActiveHostels() {
        log.info("REST request to get all active hostels");
        return ResponseEntity.ok(hostelService.getAllActiveHostels());
    }
    
    
    /**
     * Search hostels by name.
     * Public endpoint for searching hostels.
     * 
     * @param keyword - Search keyword
     * @return List of matching hostels
     */
    @GetMapping("/search")
    public ResponseEntity<List<HostelResponseDto>> searchHostels(@RequestParam String name) {
        log.info("REST request to search hostels with name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            log.warn("Empty search name provided via REST");
            return ResponseEntity.badRequest().build();
        }

        List<HostelResponseDto> hostels = hostelService.searchHostelsByName(name.trim());
        log.info("Found {} hostels matching name via REST", hostels.size());
        
        return ResponseEntity.ok(hostels);
    }
    
}