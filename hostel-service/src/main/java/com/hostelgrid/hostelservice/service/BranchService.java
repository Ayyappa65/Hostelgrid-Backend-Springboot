package com.hostelgrid.hostelservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostelgrid.common.exception.ResourceNotFoundException;
import com.hostelgrid.hostelservice.dto.BranchDto;
import com.hostelgrid.hostelservice.enums.Status;
import com.hostelgrid.hostelservice.model.Branch;
import com.hostelgrid.hostelservice.model.Hostel;
import com.hostelgrid.hostelservice.repository.BranchRepository;
import com.hostelgrid.hostelservice.repository.HostelRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Branch operations.
 * Handles business logic for branch management with DTO conversions.
 */
@Service
@Slf4j
@Transactional
public class BranchService {
    
    private final BranchRepository branchRepository;
    private final HostelRepository hostelRepository;

    public BranchService(BranchRepository branchRepository, HostelRepository hostelRepository) {
        
        log.info("Initializing BranchService");
        this.branchRepository = branchRepository;
        this.hostelRepository = hostelRepository;
    }

    /**
     * Create a new branch.
     * @param createDto - Branch creation data
     * @throws IllegalArgumentException if hostel not found or branch name already exists
     */
    public void createBranch(BranchDto.CreateBranchDto createDto) {
        log.info("Creating new branch: {} for hostel ID: {}", createDto.getBranchName(), createDto.getHostelId());
        
        // Validate hostel exists
        Hostel hostel = hostelRepository.findById(createDto.getHostelId())
            .orElseThrow(() -> {
                log.error("Hostel not found for branch creation: {}", createDto.getHostelId());
                return new IllegalArgumentException("Hostel not found: " + createDto.getHostelId());
            });
        
        // Check if branch name already exists for this hostel
        if (branchRepository.existsByHostelAndBranchNameIgnoreCase(hostel, createDto.getBranchName())) {
            log.warn("Branch creation failed - name already exists: {} for hostel: {}", 
                    createDto.getBranchName(), hostel.getName());
            throw new IllegalArgumentException("Branch name already exists for this hostel: " + createDto.getBranchName());
        }
        
        // Create branch entity
        Branch branch = new Branch();
        branch.setHostel(hostel);
        branch.setBranchName(createDto.getBranchName().trim());
        branch.setEmail(createDto.getEmail().trim());
        branch.setAddress(createDto.getAddress());
        branch.setContactNumber(createDto.getContactNumber());
        branch.setDescription(createDto.getDescription());
        
        Branch savedBranch = branchRepository.save(branch);
        log.info("Branch created successfully with ID: {}", savedBranch.getId());
    }
    
    /**
     * Update branch information.
     * @param id - Branch ID
     * @param updateDto - Update data
     * @throws IllegalArgumentException if branch not found or validation fails
     */
    public void updateBranch(Long id, BranchDto.UpdateBranchDto updateDto) {
        log.info("Updating branch ID: {}", id);
        
        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Branch not found for update: {}", id);
                return new ResourceNotFoundException("Branch not found: " + id);
            });
        
        // Update branch name if provided
        if (updateDto.getBranchName() != null && !updateDto.getBranchName().trim().isEmpty()) {
            String newName = updateDto.getBranchName().trim();
            // Check if new name conflicts with existing branch for same hostel
            if (!branch.getBranchName().equalsIgnoreCase(newName) && 
                branchRepository.existsByHostelAndBranchNameIgnoreCase(branch.getHostel(), newName)) {
                log.warn("Branch update failed - name already exists: {} for hostel: {}", 
                        newName, branch.getHostel().getName());
                throw new IllegalArgumentException("Branch name already exists for this hostel: " + newName);
            }
            branch.setBranchName(newName);
        }
        
        // Update other fields if provided
        if (updateDto.getAddress() != null) {
            branch.setAddress(updateDto.getAddress());
        }
        
        if (updateDto.getContactNumber() != null) {
            branch.setContactNumber(updateDto.getContactNumber().trim().isEmpty() ? 
                null : updateDto.getContactNumber().trim());
        }
        
        if (updateDto.getDescription() != null) {
            branch.setDescription(updateDto.getDescription());
        }
        
        branchRepository.save(branch);
        log.info("Branch updated successfully: {}", id);
    }
    
    /*
     * update branch status.
     * Only ADMIN and OWNER roles can update branch status.
     */
    public void updateBranchStatus(Long id, Status status) {
        log.info("Updating branch status ID: {}", id);

        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Branch not found for status update: {}", id);
                return new ResourceNotFoundException("Branch not found: " + id);
            });

        branch.setStatus(status);
        branchRepository.save(branch);

        log.info("Branch status updated successfully: {}", id);
    }


    /**
     * Delete branch (soft delete by setting status to DELETED).
     * @param id - Branch ID
     * @throws IllegalArgumentException if branch not found
     */
    public void deleteBranch(Long id) {
        log.info("Deleting branch ID: {}", id);
        
        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Branch not found for deletion: {}", id);
                return new ResourceNotFoundException("Branch not found: " + id);
            });
        
        branch.setStatus(Status.DELETED);
        branchRepository.save(branch);
        
        log.info("Branch deleted successfully: {}", id);
    }
    
    /**
     * Get branch by ID as DTO.
     * @param id - Branch ID
     * @return Optional branch response DTO
     */
    @Transactional(readOnly = true)
    public Optional<BranchDto.BranchResponseDto> getBranchById(Long id) {
        log.info("Fetching branch by ID: {}", id);
        
        Optional<Branch> branch = branchRepository.findById(id);
        if (branch.isPresent()) {
            log.info("Branch found: {}", branch.get().getBranchName());
            return Optional.of(convertToResponseDto(branch.get()));
        } else {
            log.info("Branch not found with ID: {}", id);
            return Optional.empty();
        }
    }
    
    /**
     * Get all branches as DTOs.
     * @return List of branch response DTOs
     */
    @Transactional(readOnly = true)
    public List<BranchDto.BranchResponseDto> getAllBranches() {
        log.info("Fetching all branches");
        
        List<Branch> branches = branchRepository.findAll();
        log.info("Found {} branches", branches.size());
        
        return branches.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get branches by hostel ID as DTOs.
     * @param hostelId - Hostel ID
     * @return List of branch response DTOs
     */
    @Transactional(readOnly = true)
    public List<BranchDto.BranchResponseDto> getBranchesByHostel(Long hostelId) {
        log.info("Fetching branches for hostel ID: {}", hostelId);
        
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hostel not found: " + hostelId));

        List<Branch> branches = branchRepository.findByHostel(hostel);
        log.info("Found {} branches for hostel: {}", branches.size(), hostel.getName());
        
        return branches.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active branches by hostel ID as DTOs.
     * @param hostelId - Hostel ID
     * @return List of active branch response DTOs
     */
    @Transactional(readOnly = true)
    public List<BranchDto.BranchResponseDto> getActiveBranchesByHostel(Long hostelId) {
        log.info("Fetching active branches for hostel ID: {}", hostelId);
        
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new IllegalArgumentException("Hostel not found: " + hostelId));
        
        List<Branch> branches = branchRepository.findActiveByHostel(hostel);
        log.info("Found {} active branches for hostel: {}", branches.size(), hostel.getName());
        
        return branches.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search branches by name as DTOs.
     * @param keyword - Search keyword
     * @return List of matching branch response DTOs
     */
    @Transactional(readOnly = true)
    public List<BranchDto.BranchResponseDto> searchBranchesByName(String keyword) {
        log.info("Searching branches with keyword: {}", keyword);
        
        List<Branch> branches = branchRepository.searchByBranchName(keyword);
        log.info("Found {} branches matching keyword", branches.size());
        
        return branches.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Branch entity to response DTO.
     * @param branch - Branch entity
     * @return BranchResponseDto
     */
    private BranchDto.BranchResponseDto convertToResponseDto(Branch branch) {
        BranchDto.BranchResponseDto dto = BranchDto.BranchResponseDto.builder()
                .id(branch.getId())
                .branchName(branch.getBranchName())
                .email(branch.getEmail())
                .address(branch.getAddress())
                .contactNumber(branch.getContactNumber())
                .description(branch.getDescription())
                .status(branch.getStatus())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
        return dto;
    }
}