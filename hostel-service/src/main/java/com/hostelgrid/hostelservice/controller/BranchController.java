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
import com.hostelgrid.hostelservice.dto.BranchDto;
import com.hostelgrid.hostelservice.service.BranchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Branch management operations.
 * Provides CRUD endpoints for managing hostel branches.
 */
@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Slf4j
public class BranchController {
    
    private final BranchService branchService;
    
    /**
     * Create a new branch.
     * Only ADMIN and OWNER roles can create branches.
     * 
     * @param createDto - Branch creation data
     * @return Success message
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<MessageResponse> createBranch(@Valid @RequestBody BranchDto.CreateBranchDto createDto) {
        log.info("REST request to create branch: {} for hostel ID: {}", createDto.getBranchName(), createDto.getHostelId());
        
        branchService.createBranch(createDto);
        
        log.info("Branch created successfully: {}", createDto.getBranchName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Branch created successfully!"));
    }
    
    /**
     * Get branch by ID.
     * All authenticated users can view branch details.
     * 
     * @param id - Branch ID
     * @return Branch details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER', 'WARDEN', 'USER', 'STUDENT')")
    public ResponseEntity<BranchDto.BranchResponseDto> getBranchById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            log.warn("Invalid branch ID provided: {}", id);
            return ResponseEntity.badRequest().build();
        }
        
        log.info("REST request to get branch by ID: {}", id);
        
        Optional<BranchDto.BranchResponseDto> branch = branchService.getBranchById(id);
        
        if (branch.isPresent()) {
            log.info("Branch found: {}", branch.get().getBranchName());
            return ResponseEntity.ok(branch.get());
        } else {
            log.warn("Branch not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all branches.
     * Only ADMIN and OWNER roles can view all branches.
     * 
     * @return List of all branches
     */
    // @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER')")
    // public ResponseEntity<List<BranchDto.BranchResponseDto>> getAllBranches() {
    //     log.info("REST request to get all branches");
        
    //     List<BranchDto.BranchResponseDto> branches = branchService.getAllBranches();
        
    //     log.info("Retrieved {} branches", branches.size());
    //     return ResponseEntity.ok(branches);
    // }
    

    /**
     * Get branches by hostel ID.
     * All authenticated users can view branches for a specific hostel.
     * 
     * @param hostelId - Hostel ID
     * @return List of branches for the hostel
     */
    @GetMapping("/hostel/{hostelId}")
    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER') or hasAnyRole('WARDEN') or hasAnyRole('USER') or hasAnyRole('STUDENT')")
    public ResponseEntity<List<BranchDto.BranchResponseDto>> getBranchesByHostel(@PathVariable Long hostelId) {
        if (hostelId == null || hostelId <= 0) {
            log.warn("Invalid hostel ID provided: {}", hostelId);
            return ResponseEntity.badRequest().build();
        }
        
        log.info("REST request to get branches for hostel ID: {}", hostelId);
        
        List<BranchDto.BranchResponseDto> branches = branchService.getBranchesByHostel(hostelId);
        
        log.info("Retrieved {} branches for hostel ID: {}", branches.size(), hostelId);
        return ResponseEntity.ok(branches);
    }
    
    /**
     * Get active branches by hostel ID.
     * Public endpoint for active branches of a hostel.
     * 
     * @param hostelId - Hostel ID
     * @return List of active branches for the hostel
     */
    @GetMapping("/hostel/{hostelId}/active")
    public ResponseEntity<List<BranchDto.BranchResponseDto>> getActiveBranchesByHostel(@PathVariable Long hostelId) {
        if (hostelId == null || hostelId <= 0) {
            log.warn("Invalid hostel ID provided: {}", hostelId);
            return ResponseEntity.badRequest().build();
        }
        
        log.info("REST request to get active branches for hostel ID: {}", hostelId);
        
        List<BranchDto.BranchResponseDto> branches = branchService.getActiveBranchesByHostel(hostelId);
        
        log.info("Retrieved {} active branches for hostel ID: {}", branches.size(), hostelId);
        return ResponseEntity.ok(branches);
    }
    
    /**
     * Update branch information.
     * Only ADMIN and OWNER roles can update branches.
     * 
     * @param id - Branch ID
     * @param updateDto - Update data
     * @return Success message
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER')")
    public ResponseEntity<MessageResponse> updateBranch(@Valid @RequestBody BranchDto.UpdateBranchDto updateDto) {

        log.info("REST request to update branch ID: {}", updateDto.getId());

        branchService.updateBranch(updateDto.getId(), updateDto);

        log.info("Branch updated successfully: {}", updateDto.getId());
        return ResponseEntity.ok(new MessageResponse("Branch updated successfully!"));
    }
    
    /**
     * Delete branch (soft delete).
     * Only ADMIN role can delete branches.
     * 
     * @param id - Branch ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER')")
    public ResponseEntity<MessageResponse> deleteBranch(@PathVariable Long id) {
        log.info("REST request to delete branch ID: {}", id);
        
        branchService.deleteBranch(id);
        
        log.info("Branch deleted successfully: {}", id);
        return ResponseEntity.ok(new MessageResponse("Branch deleted successfully!"));
    }

    /*
     * Update branch status (ACTIVE/INACTIVE).
     * Only ADMIN and OWNER roles can update branch status.
     */
    @PutMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER')")
    public ResponseEntity<MessageResponse> updateBranchStatus(@Valid @RequestBody BranchDto.BranchStatusUpdate statusUpdate) {
        log.info("REST request to update branch status ID: {}", statusUpdate.getId());

        branchService.updateBranchStatus(statusUpdate.getId(), statusUpdate.getStatus());

        log.info("Branch status updated successfully: {}", statusUpdate.getId());
        return ResponseEntity.ok(new MessageResponse("Branch status updated successfully!"));
    }

    /**
     * Search branches by name.
     * All authenticated users can search branches.
     * 
     * @param keyword - Search keyword
     * @return List of matching branches
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN') or hasAnyRole('OWNER') or hasAnyRole('WARDEN') or hasAnyRole('USER') or hasAnyRole('STUDENT')")
    public ResponseEntity<List<BranchDto.BranchResponseDto>> searchBranches(@RequestParam String keyword) {
        log.info("REST request to search branches with keyword: {}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("Empty search keyword provided");
            return ResponseEntity.badRequest().build();
        }
        
        List<BranchDto.BranchResponseDto> branches = branchService.searchBranchesByName(keyword.trim());
        
        log.info("Found {} branches matching keyword: {}", branches.size(), keyword);
        return ResponseEntity.ok(branches);
    }
}