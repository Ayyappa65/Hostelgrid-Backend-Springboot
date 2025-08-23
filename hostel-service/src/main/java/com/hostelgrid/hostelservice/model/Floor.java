package com.hostelgrid.hostelservice.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "floor", indexes = {
    @Index(name = "idx_floor_building_id", columnList = "building_id"),
    @Index(name = "idx_floor_building_number", columnList = "building_id, number")
})
@Data
public class Floor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
    
    @Column(nullable = false)
    private Integer number;

    /*
     * Relationship with Room
     * OneToMany relationship with Room entity
     * A floor can have multiple rooms
    */
    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Room> rooms;
    
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