package com.hostelgrid.hostelservice.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.hostelgrid.common.response.MessageResponse;
import com.hostelgrid.hostelservice.dto.ItemCategoryDto;
import com.hostelgrid.hostelservice.service.ItemCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Item Category management operations.
 * Provides CRUD endpoints for managing inventory categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class ItemCategoryController {
    
    private final ItemCategoryService categoryService;
    
    /**
     * Create a new item category.
     * Only ADMIN and WARDEN roles can create categories.
     * 
     * @param createDto - Category creation data
     * @return Success message
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<MessageResponse> createCategory(@Valid @RequestBody ItemCategoryDto.CreateItemCategoryDto createDto) {
        log.info("REST request to create category: {}", createDto.getCategoryName());
        
        categoryService.createCategory(createDto.getCategoryName(), createDto.getDescription());
        
        log.info("Category created successfully: {}", createDto.getCategoryName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Category created successfully!"));
    }
    
    /**
     * Get all active categories.
     * All authenticated users can view categories.
     * 
     * @return List of active categories
     */
    @GetMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'MAINTENANCE')")
    public ResponseEntity<List<ItemCategoryDto.ItemCategoryResponseDto>> getAllCategoriesByBranchId(@Valid @PathVariable Long branchId) {
        log.info("REST request to get all active categories for branch ID: {}", branchId);

        List<ItemCategoryDto.ItemCategoryResponseDto> categories = categoryService.getAllCategoriesDtoByBranchId(branchId);

        log.info("Retrieved {} active categories", categories.size());
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Update category information.
     * Only ADMIN and WARDEN roles can update categories.
     * 
     * @param id - Category ID
     * @param updateDto - Update data
     * @return Success message
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<MessageResponse> updateCategory(@Valid @RequestBody ItemCategoryDto.UpdateItemCategoryDto updateDto) {

        log.info("REST request to update category ID: {}", updateDto.getId());

        categoryService.updateCategory(updateDto.getId(), updateDto.getCategoryName(), updateDto.getDescription());

        return ResponseEntity.ok(new MessageResponse("Category updated successfully!"));
    }
    
    /**
     * Deactivate category (soft delete).
     * Only ADMIN role can deactivate categories.
     * 
     * @param id - Category ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<MessageResponse> deactivateCategory(@PathVariable Long id) {
        log.info("REST request to deactivate category ID: {}", id);
        
        categoryService.deactivateCategory(id);
        
        log.info("Category deactivated successfully: {}", id);
        return ResponseEntity.ok(new MessageResponse("Category deactivated successfully!"));
    }
}