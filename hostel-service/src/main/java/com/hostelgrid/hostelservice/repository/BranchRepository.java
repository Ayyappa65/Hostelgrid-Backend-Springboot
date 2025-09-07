package com.hostelgrid.hostelservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelgrid.hostelservice.enums.Status;
import com.hostelgrid.hostelservice.model.Branch;
import com.hostelgrid.hostelservice.model.Hostel;

/**
 * Repository interface for Branch entity operations.
 * Provides CRUD operations and custom queries for branches.
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    /**
     * Find branches by hostel.
     * @param hostel - Hostel entity
     * @return List of branches for the hostel
     */
    List<Branch> findByHostel(Hostel hostel);
    
    /**
     * Find branches by hostel and status.
     * @param hostel - Hostel entity
     * @param status - Branch status
     * @return List of branches with the given status
     */
    List<Branch> findByHostelAndStatus(Hostel hostel, Status status);
    
    /**
     * Find branch by hostel and branch name (case-insensitive).
     * @param hostel - Hostel entity
     * @param branchName - Branch name
     * @return Optional Branch
     */
    @Query("SELECT b FROM Branch b WHERE b.hostel = :hostel AND LOWER(b.branchName) = LOWER(:branchName)")
    Optional<Branch> findByHostelAndBranchNameIgnoreCase(@Param("hostel") Hostel hostel, @Param("branchName") String branchName);
    
    /**
     * Find all active branches.
     * @return List of active branches
     */
    @Query("SELECT b FROM Branch b WHERE b.status = 'ACTIVE' ORDER BY b.branchName")
    List<Branch> findAllActiveBranches();
    
    /**
     * Find active branches by hostel.
     * @param hostel - Hostel entity
     * @return List of active branches for the hostel
     */
    @Query("SELECT b FROM Branch b WHERE b.hostel = :hostel AND b.status = 'ACTIVE' ORDER BY b.branchName")
    List<Branch> findActiveByHostel(@Param("hostel") Hostel hostel);
    
    /**
     * Search branches by name containing keyword (case-insensitive).
     * @param keyword - Search keyword
     * @return List of matching branches
     */
    @Query("SELECT b FROM Branch b WHERE LOWER(b.branchName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY b.branchName")
    List<Branch> searchByBranchName(@Param("keyword") String keyword);
    
    /**
     * Check if branch name exists for a hostel (case-insensitive).
     * @param hostel - Hostel entity
     * @param branchName - Branch name
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(b) > 0 FROM Branch b WHERE b.hostel = :hostel AND LOWER(b.branchName) = LOWER(:branchName)")
    boolean existsByHostelAndBranchNameIgnoreCase(@Param("hostel") Hostel hostel, @Param("branchName") String branchName);
}