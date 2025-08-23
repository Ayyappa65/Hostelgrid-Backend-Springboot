package com.hostelgrid.hostelservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostelgrid.hostelservice.model.ItemCategory;
import com.hostelgrid.hostelservice.repository.ItemCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for ItemCategory operations.
 * Handles business logic for item category management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemCategoryService {
    
    private final ItemCategoryRepository categoryRepository;
    
    /**
     * Create a new item category.
     * @param categoryName - Name of the category
     * @param description - Category description
     * @return Created ItemCategory
     * @throws IllegalArgumentException if category already exists
     */
    public ItemCategory createCategory(String categoryName, String description) {
        log.info("Creating new category: {}", categoryName);
        
        // Check if category already exists
        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryName)) {
            log.warn("Category creation failed - already exists: {}", categoryName);
            throw new IllegalArgumentException("Category already exists: " + categoryName);
        }
        
        ItemCategory category = new ItemCategory();
        category.setCategoryName(categoryName.trim());
        category.setDescription(description);
        category.setIsActive(true);
        
        ItemCategory savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        
        return savedCategory;
    }
    
    /**
     * Get all active categories.
     * @return List of active categories
     */
    @Transactional(readOnly = true)
    public List<ItemCategory> getAllActiveCategories() {
        log.debug("Fetching all active categories");
        List<ItemCategory> categories = categoryRepository.findAllActiveCategories();
        log.debug("Found {} active categories", categories.size());
        return categories;
    }
    
    /**
     * Find category by ID.
     * @param id - Category ID
     * @return Optional ItemCategory
     */
    @Transactional(readOnly = true)
    public Optional<ItemCategory> findById(Long id) {
        log.debug("Finding category by ID: {}", id);
        return categoryRepository.findById(id);
    }
    
    /**
     * Find category by name (case-insensitive).
     * @param categoryName - Category name
     * @return Optional ItemCategory
     */
    @Transactional(readOnly = true)
    public Optional<ItemCategory> findByName(String categoryName) {
        log.debug("Finding category by name: {}", categoryName);
        return categoryRepository.findByCategoryNameIgnoreCase(categoryName);
    }
    
    /**
     * Update category details.
     * @param id - Category ID
     * @param categoryName - New category name
     * @param description - New description
     * @return Updated ItemCategory
     * @throws IllegalArgumentException if category not found or name already exists
     */
    public ItemCategory updateCategory(Long id, String categoryName, String description) {
        log.info("Updating category ID: {}", id);
        
        ItemCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Category not found for update: {}", id);
                return new IllegalArgumentException("Category not found: " + id);
            });
        
        // Check if new name conflicts with existing category
        if (!category.getCategoryName().equalsIgnoreCase(categoryName) && 
            categoryRepository.existsByCategoryNameIgnoreCase(categoryName)) {
            log.warn("Category update failed - name already exists: {}", categoryName);
            throw new IllegalArgumentException("Category name already exists: " + categoryName);
        }
        
        category.setCategoryName(categoryName.trim());
        category.setDescription(description);
        
        ItemCategory updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", updatedCategory.getId());
        
        return updatedCategory;
    }
    
    /**
     * Deactivate a category (soft delete).
     * @param id - Category ID
     * @throws IllegalArgumentException if category not found
     */
    public void deactivateCategory(Long id) {
        log.info("Deactivating category ID: {}", id);
        
        ItemCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Category not found for deactivation: {}", id);
                return new IllegalArgumentException("Category not found: " + id);
            });
        
        category.setIsActive(false);
        categoryRepository.save(category);
        
        log.info("Category deactivated successfully: {}", id);
    }
}