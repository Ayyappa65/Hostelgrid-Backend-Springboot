package com.hostelgrid.hostelservice.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTOs for ItemCategory operations.
 */
public class ItemCategoryDto {

    /**
     * DTO for creating a new item category.
     */
    @Data
    public static class CreateItemCategoryDto {

        @NotNull(message = "Branch ID is required")
        private Long branchId;

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        private String categoryName;
        
        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    /**
     * DTO for updating item category.
     */
    @Data
    public static class UpdateItemCategoryDto {

        @NotNull(message = "Category ID is required")
        private Long id;

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        private String categoryName;
        
        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    /**
     * DTO for item category response.
     */
    @Data
    public static class ItemCategoryResponseDto {
        
        private Long id;
        private String categoryName;
        private String description;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}