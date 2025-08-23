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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hostel", indexes = {
    @Index(name = "idx_hostel_status", columnList = "status"),
    @Index(name = "idx_hostel_name", columnList = "name"),
    @Index(name = "idx_hostel_email", columnList = "email"),
    @Index(name = "idx_hostel_contact_number", columnList = "contact_number")
})
@Data
public class Hostel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // @Column(name = "tenant_id", nullable = false)
    // private Long tenantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", length = 100, nullable= false)
    private String email;

    @Column(name = "contact_number", length = 20, nullable= false)
    private String contactNumber;

    @Column(length = 500)
    private String description;

    /*
     * Status of the hostel
     * ACTIVE, INACTIVE, DELETED
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    /* Relationship with Branch
     * OneToMany relationship with Branch entity
     * A hostel can have multiple branches
    */
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    private List<Branch> branches;
    
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