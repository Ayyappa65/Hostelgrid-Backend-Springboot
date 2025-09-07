package com.hostelgrid.hostelservice.dto;

import java.time.LocalDateTime;

import com.hostelgrid.hostelservice.enums.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTOs for Branch operations.
 */
public class BranchDto {

    /**
     * DTO for creating a new branch.
     */
    @Data
    public static class CreateBranchDto {
        
        @NotNull(message = "Hostel ID is required")
        private Long hostelId;
        
        @NotBlank(message = "Branch name is required")
        @Size(max = 100, message = "Branch name must not exceed 100 characters")
        private String branchName;
        
        @Size(max = 500, message = "Address must not exceed 500 characters")
        private String address;

        @Size(max = 100, message = "Email must not exceed 100 characters")
        @Email(message = "Email should be valid")
        private String email;

        @Size(max = 20, message = "Contact number must not exceed 20 characters")
        private String contactNumber;
        
        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    /**
     * DTO for updating branch information.
     */
    @Data
    public static class UpdateBranchDto {

        @NotNull(message = "Branch ID is required")
        private Long id;

        @Size(max = 100, message = "Branch name must not exceed 100 characters")
        private String branchName;
        
        @Size(max = 500, message = "Address must not exceed 500 characters")
        private String address;

        @Size(max = 100, message = "Email must not exceed 100 characters")
        @Email(message = "Email should be valid")
        private String email;
        
        @Size(max = 20, message = "Contact number must not exceed 20 characters")
        private String contactNumber;
        
        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    @Data
    public static class BranchStatusUpdate{
        private Long id;
        private Status status;
    }

    /**
     * DTO for branch response.
     */
    @Data
    @Builder
    public static class BranchResponseDto {
        
        private Long id;
        private String branchName;
        private String email;
        private String address;
        private String contactNumber;
        private String description;
        private Status status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}