package com.hostelgrid.hostelservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelgrid.hostelservice.enums.Status;
import com.hostelgrid.hostelservice.model.Hostel;

/**
 * Repository interface for Hostel entity operations.
 * Provides CRUD operations and custom queries for hostels.
 */
@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
    
    /**
     * Find hostels by status.
     * @param status - Hostel status
     * @return List of hostels with the given status
     */
    List<Hostel> findByStatus(Status status);
    
    /**
     * Find hostel by name (case-insensitive).
     * @param name - Hostel name
     * @return Optional Hostel
     */
    @Query("SELECT h FROM Hostel h WHERE LOWER(h.name) = LOWER(:name)")
    Optional<Hostel> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Find hostels by name containing keyword (case-insensitive).
     * @param keyword - Search keyword
     * @return List of matching hostels
     */
    @Query("SELECT h FROM Hostel h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY h.name")
    List<Hostel> searchByName(@Param("keyword") String keyword);
    
    /**
     * Find hostel by email.
     * @param email - Hostel email
     * @return Optional Hostel
     */
    Optional<Hostel> findByEmail(String email);
    
    /**
     * Find hostel by contact number.
     * @param contactNumber - Contact number
     * @return Optional Hostel
     */
    Optional<Hostel> findByContactNumber(String contactNumber);
    
    /**
     * Check if hostel name exists (case-insensitive).
     * @param name - Hostel name
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(h) > 0 FROM Hostel h WHERE LOWER(h.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    /**
     * Check if hostel email and contact number exists.
     * @param email - Hostel email
     * @param contactNumber - Contact number
     * @return true if exists, false otherwise
     */
    boolean existsByEmailAndContactNumber(String email, String contactNumber);

    /**
     * Find all active hostels ordered by name.
     * @return List of active hostels
     */
    @Query("SELECT h FROM Hostel h WHERE h.status = 'ACTIVE' ORDER BY h.name")
    List<Hostel> findAllActiveHostels();
    
    /**
     * Find all active hostels with pagination.
     * @param pageable - Pagination information
     * @return Page of active hostels
     */
    @Query("SELECT h FROM Hostel h WHERE h.status = 'ACTIVE' ORDER BY h.name")
    Page<Hostel> findAllActiveHostels(Pageable pageable);
    
    /**
     * Find hostels by name containing keyword with pagination (case-insensitive).
     * @param search - Search keyword
     * @param pageable - Pagination information
     * @return Page of matching hostels
     */
    Page<Hostel> findByNameContainingIgnoreCase(String search, Pageable pageable);
}