package com.hostelgrid.hostelservice.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "item_category",
    indexes = {
        @Index(name = "idx_branch_category_name", columnList = "branch_id, category_name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_branch_category", columnNames = {"branch_id", "category_name"})
    }
)
@Data
public class ItemCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    /**
     * Typical categories might include:
     * Furniture: Beds, tables, chairs, wardrobes
     * Electronics: Fans, lights, ACs, WiFi routers
     * Supplies: Bedsheets, pillows, cleaning supplies
     * Maintenance: Tools, spare parts, consumables
     */
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
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
