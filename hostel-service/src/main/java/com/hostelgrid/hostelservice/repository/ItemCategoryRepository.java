package com.hostelgrid.hostelservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelgrid.hostelservice.model.ItemCategory;

/**
 * Repository interface for ItemCategory entity operations.
 * Provides CRUD operations and custom queries for item categories.
 */
@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    
    /**
     * Find category by name (case-insensitive).
     * @param categoryName - Category name to search
     * @return Optional ItemCategory
     */
    @Query("SELECT ic FROM ItemCategory ic WHERE LOWER(ic.categoryName) = LOWER(:categoryName)")
    Optional<ItemCategory> findByCategoryNameIgnoreCase(@Param("categoryName") String categoryName);
    
    /**
     * Find all active categories.
     * @return List of active categories
     */
    @Query("SELECT ic FROM ItemCategory ic WHERE ic.isActive = true ORDER BY ic.categoryName")
    List<ItemCategory> findAllActiveCategories();
    
    /**
     * Check if category name exists (case-insensitive).
     * @param categoryName - Category name to check
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(ic) > 0 FROM ItemCategory ic WHERE LOWER(ic.categoryName) = LOWER(:categoryName)")
    boolean existsByCategoryNameIgnoreCase(@Param("categoryName") String categoryName);
}