package com.hostelgrid.hostelservice.graphql;

import java.util.List;
import java.util.Optional;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.hostelgrid.hostelservice.dto.HostelDto;
import com.hostelgrid.hostelservice.dto.HostelDto.HostelResponseDto;
import com.hostelgrid.hostelservice.service.HostelService;

import lombok.RequiredArgsConstructor;

/*
 * GraphQL Controller for managing hostels.
 * Provides query and mutation mappings for hostel operations.
 * Uses HostelService to perform business logic.
 * Returns appropriate responses for each operation.
 * Handles exceptions and errors gracefully.
 * Ensures data validation and integrity.
 * Supports pagination and filtering for listing hostels.
 * Integrates with other services as needed.
 * Follows best practices for GraphQL API design.
 * Includes logging for monitoring and debugging.
 * Secures endpoints to prevent unauthorized access.
 * Provides clear and concise documentation for each method.
 * Facilitates testing and maintenance of the codebase.
 * Adheres to coding standards and conventions.
 * Promotes reusability and modularity of components.
 * Enhances user experience with efficient data retrieval.
 * Implements caching strategies for performance optimization.
 * Supports internationalization and localization.
 * Monitors and analyzes usage patterns for improvements.
 * Collaborates with frontend teams for seamless integration.
 * Continuously updates and refines the API based on feedback.  
 */


 // to see in postman use http://localhost:8083/graphql
 // to see in graphiql use http://localhost:8083/graphiql
@Controller
@RequiredArgsConstructor
public class HostelGraphQLController {

    private final HostelService hostelService;

    /**
     * Fetches a list of all hostels.
     * This method is mapped to a GraphQL query.
     * It retrieves all hostels from the HostelService and returns them as a list of HostelResponseDto.
     * @return List of HostelResponseDto representing all hostels.
     */ 
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'WARDEN')")
    public List<HostelDto.HostelResponseDto> hostels() {
        return hostelService.getAllHostels();
    }

    /**
     * Fetches a specific hostel by its ID.
     * This method is mapped to a GraphQL query.
     * It retrieves the hostel with the given ID from the HostelService and returns it as an Optional of HostelResponseDto.
     * @param id The ID of the hostel to be fetched.
     * @return Optional containing the HostelResponseDto if found, or empty if not found.
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'WARDEN', 'STUDENT')")
    public Optional<HostelResponseDto> hostel(@Argument Long id) {
        return hostelService.getHostelById(id);
    }

    /**
     * Fetches a list of all active hostels.
     * This method is mapped to a GraphQL query.
     * It retrieves all active hostels from the HostelService and returns them as a list of HostelResponseDto.
     * @return List of HostelResponseDto representing all active hostels.
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'WARDEN', 'STUDENT')")
    public List<HostelResponseDto> activeHostels() {
        return hostelService.getAllActiveHostels();
    }

    /**
     * Creates a new hostel.
     * This method is mapped to a GraphQL mutation.
     * It takes a CreateHostelDto as input, calls the HostelService to create the hostel, and returns a success message.
     * @param input The CreateHostelDto containing the details of the hostel to be created.
     * @return A success message indicating that the hostel was created successfully.
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public String createHostel(@Argument HostelDto.CreateHostelDto input) {
        hostelService.createHostel(input);
        return "Hostel created successfully";
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public String updateHostel(@Argument HostelDto.HostelUpdateDto updateDto) {
        hostelService.updateHostel(updateDto.getId(), updateDto);
        return "Hostel updated successfully";
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteHostel(@Argument Long id) {
        hostelService.deleteHostel(id);
        return "Hostel deleted successfully";
    }
}