package com.hostelgrid.hostelservice.dto;

import java.time.LocalDateTime;

import com.hostelgrid.hostelservice.enums.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class HostelDto {

    @Data
    public static class CreateHostelDto {

        @NotBlank(message = "Hostel name is required")
        @Size(max = 100, message = "Hostel name must not exceed 100 characters")
        private String name;
    
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        private String email;
    
        @Size(max = 20, message = "Contact number must not exceed 20 characters")
        private String contactNumber;
    
        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    @Data
    @Builder
    public static class HostelResponseDto {
        private Long id;
        private String name;
        private String email;
        private String contactNumber;
        private String description;
        private Status status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class HostelUpdateDto {
        
        @NotBlank(message = "Hostel ID is required")
        private Long id;
        
        @NotBlank(message = "Hostel name is required")
        @Size(max = 100, message = "Hostel name must not exceed 100 characters")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        private String email;

        @Size(max = 20, message = "Contact number must not exceed 20 characters")
        private String contactNumber;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    @Data
    public static class HostelStatusUpdate{
        private Long id;
        private Status status;
    }
}
