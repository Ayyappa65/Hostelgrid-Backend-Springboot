package com.hostelgrid.hostelservice.model;

import java.time.LocalDateTime;
import java.util.List;

import com.hostelgrid.hostelservice.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "branch", indexes = {
    @Index(name = "idx_branch_hostel_id", columnList = "hostel_id"),
    @Index(name = "idx_branch_status", columnList = "status"),
    @Index(name = "idx_branch_email", columnList = "email"),
    @Index(name = "idx_branch_contact_number", columnList = "contact_number"),
    @Index(name = "idx_branch_hostel_status", columnList = "hostel_id, status")
})
@Data
public class Branch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Relationship with Hostel 
     * ManyToOne relationship with Hostel entity
     * Each branch belongs to one hostel
    */
    @ManyToOne
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;
    
    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private String address;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    

    /* Relationship with Building
     * OneToMany relationship with Building entity
     * A branch can have multiple buildings
    */
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Building> buildings;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}