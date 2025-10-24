package com.hostelgrid.hostelservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostelgrid.common.exception.ResourceNotFoundException;
import com.hostelgrid.hostelservice.dto.HostelDto;
import com.hostelgrid.hostelservice.enums.Status;
import com.hostelgrid.hostelservice.model.Hostel;
import com.hostelgrid.hostelservice.repository.HostelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Hostel operations.
 * Handles business logic for hostel management with DTO conversions.   
 * Transactional methods ensure data integrity.(create, update, delete) it is used to manage transactions in the service layer.
 * it ensures that a series of operations either complete successfully as a whole or are rolled back in case of an error.
 * Asynchronous methods improve performance for non-blocking operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HostelService {
    
    private final HostelRepository hostelRepository;
    
    /**
     * Create a new hostel.
     * @param createDto - Hostel creation data
     * @return Created hostel response DTO
     * @throws IllegalArgumentException if hostel name already exists
     */
    public void  createHostel(HostelDto.CreateHostelDto createDto) {
        log.info("Creating new hostel: {}", createDto.getName());
        
        // Check if email already exists (if provided)
        if (hostelRepository.existsByEmailAndContactNumber(createDto.getEmail(), createDto.getContactNumber())) {
            log.warn("Hostel creation failed - email and contact number already exists: {} - {}", createDto.getEmail(), createDto.getContactNumber());
            throw new IllegalArgumentException("Email and contact number already exists: " + createDto.getEmail() + " - " + createDto.getContactNumber());
        }
        
        // Create hostel entity
        Hostel hostel = new Hostel();
        hostel.setName(createDto.getName().trim());
        hostel.setEmail(createDto.getEmail() != null ? createDto.getEmail().trim() : null);
        hostel.setContactNumber(createDto.getContactNumber() != null ? createDto.getContactNumber().trim() : null);
        hostel.setDescription(createDto.getDescription());
        
        Hostel savedHostel = hostelRepository.save(hostel);
        log.info("Hostel created successfully with ID: {}", savedHostel.getId());
    }
    
    
    /**
     * Update hostel information.
     * @param id - Hostel ID
     * @param updateDto - Update data
     * @return Updated hostel response DTO
     * @throws IllegalArgumentException if hostel not found or validation fails
     */
    public void updateHostel(Long id, HostelDto.HostelUpdateDto updateDto) {
        log.info("Updating hostel ID: {}", id);
        
        Hostel existingHostel = hostelRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Hostel not found for update: {}", id);
                return new ResourceNotFoundException("Hostel not found: " + id);
            });
        
        // Update name if provided
        if (updateDto.getName() != null && !updateDto.getName().trim().isEmpty()) {
            String newName = updateDto.getName().trim();
            existingHostel.setName(newName);
        }
        
        // Update email if provided
        if (updateDto.getEmail() != null) {
            String newEmail = updateDto.getEmail().trim();
            if (!newEmail.isEmpty() && existingHostel.getEmail().equalsIgnoreCase(newEmail) == false) {
                
                // Check if new email conflicts with existing hostel
                Optional<Hostel> conflictingHostel = hostelRepository.findByEmail(newEmail);
                if (conflictingHostel.isPresent() && !conflictingHostel.get().getId().equals(id)) {
                    log.warn("Hostel update failed - email already exists: {}", newEmail);
                    throw new IllegalArgumentException("Email already exists: " + newEmail);
                }
            }
            existingHostel.setEmail(newEmail.isEmpty() ? null : newEmail);
        }
        
        // Update other fields if provided
        if (updateDto.getContactNumber() != null) {
            existingHostel.setContactNumber(updateDto.getContactNumber().trim().isEmpty() ?
                null : updateDto.getContactNumber().trim());
        }
        
        if (updateDto.getDescription() != null) {
            existingHostel.setDescription(updateDto.getDescription());
        }

        hostelRepository.save(existingHostel);
        log.info("Hostel updated successfully: {}", existingHostel.getId());
    }


    /**
     * update hostel status.
     * @param statusUpdate - Status update data
     */
    public void updateHostelStatus(HostelDto.HostelStatusUpdate statusUpdate) {
        log.info("Updating Hostel {} status to {}", statusUpdate.getId(), statusUpdate.getStatus());

        Hostel hostel = hostelRepository.findById(statusUpdate.getId())
                .orElseThrow(() -> {
                    log.error("Hostel not found for status update: {}", statusUpdate.getId());
                    return new ResourceNotFoundException("Hostel not found: " + statusUpdate.getId());
                });

        hostel.setStatus(statusUpdate.getStatus());
        hostelRepository.save(hostel);
        log.info("Updated {} hostel to status {}", hostel.getId(), statusUpdate.getStatus());
    }
    

    /**
     * Delete hostel (soft delete by setting status to DELETED).
     * @param id - Hostel ID
     * @throws IllegalArgumentException if hostel not found
     */
    public void deleteHostel(Long id) {
        log.info("Deleting hostel ID: {}", id);
        
        Hostel hostel = hostelRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Hostel not found for deletion: {}", id);
                return new ResourceNotFoundException("Hostel not found: " + id);
            });
        
        hostel.setStatus(Status.DELETED);
        hostelRepository.save(hostel);
        
        log.info("Hostel deleted successfully: {}", id);
    }
    
    /**
     * Get all hostels with pagination.
     * @param pageable - Pagination information
     * @return Page of hostel response DTOs
     */
    @Transactional(readOnly = true)
    public Page<HostelDto.HostelResponseDto> getAllHostels(Pageable pageable, String search) {
        log.info("Fetching all hostels - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Hostel> hostels;
        if (search != null && !search.trim().isEmpty()) {
            log.info("Searching hostels by name: {}", search);
            hostels = hostelRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            hostels = hostelRepository.findAll(pageable);
        }
        log.info("Found {} hostels", hostels.getTotalElements());
        
        return hostels.map(this::convertToResponseDto);
    }

      /**
     * Get all hostels with pagination.
     * @param pageable - Pagination information
     * @return Page of hostel response DTOs
     */
    @Transactional(readOnly = true)
    public List<HostelDto.HostelResponseDto> getAllHostels() {
        log.info("Fetching all hostels");

        List<Hostel> hostels = hostelRepository.findAll();
        log.info("Found {} hostels", hostels.size());

        return hostels.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get hostel by ID.
     * @param id - Hostel ID
     * @return Optional hostel response DTO
     */
    @Transactional(readOnly = true)
    public Optional<HostelDto.HostelResponseDto> getHostelById(Long id) {
        log.info("Fetching hostel by ID: {}", id);
        
        Optional<Hostel> hostel = hostelRepository.findById(id);
        if (hostel.isPresent()) {
            log.info("Hostel found: {}", hostel.get().getName());
            return Optional.of(convertToResponseDto(hostel.get()));
        } else {
            log.info("Hostel not found with ID: {}", id);
            throw new ResourceNotFoundException("Hostel not found: " + id);
        }
    }
    
    
    /**
     * Get all active hostels with pagination.
     * @param pageable - Pagination information
     * @return Page of active hostel response DTOs
     */
    @Transactional(readOnly = true)
    public Page<HostelDto.HostelResponseDto> getAllActiveHostels(Pageable pageable) {
        log.info("Fetching all active hostels - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Hostel> hostels = hostelRepository.findAllActiveHostels(pageable);
        log.info("Found {} active hostels", hostels.getTotalElements());
        
        return hostels.map(this::convertToResponseDto);
    }

    /**
     * Get all active hostels with pagination.
     * @param pageable - Pagination information
     * @return Page of active hostel response DTOs
     */
    @Transactional(readOnly = true)
    public List<HostelDto.HostelResponseDto> getAllActiveHostels() {
        log.info("Fetching all active hostels");

        List<Hostel> hostels = hostelRepository.findAllActiveHostels();
        log.info("Found {} active hostels", hostels.size());

        return hostels.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Search hostels by name.
     * @param keyword - Search keyword
     * @return List of matching hostel response DTOs
     */
    @Transactional(readOnly = true)
    public List<HostelDto.HostelResponseDto> searchHostelsByName(String name) {
        log.info("Searching hostels with name: {}", name);

        List<Hostel> hostels = hostelRepository.searchByName(name);
        log.info("Found {} hostels matching name", hostels.size());

        return hostels.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    

    /**
     * Convert Hostel entity to HostelResponseDto.
     * @param hostel - Hostel entity
     * @return HostelResponseDto
     */
    private HostelDto.HostelResponseDto convertToResponseDto(Hostel hostel) {
        HostelDto.HostelResponseDto dto = HostelDto.HostelResponseDto.builder()
                .id(hostel.getId())
                .name(hostel.getName())
                .email(hostel.getEmail())
                .contactNumber(hostel.getContactNumber())
                .description(hostel.getDescription())
                .status(hostel.getStatus())
                .createdAt(hostel.getCreatedAt())
                .updatedAt(hostel.getUpdatedAt())
                .build();
        return dto;
    }
}