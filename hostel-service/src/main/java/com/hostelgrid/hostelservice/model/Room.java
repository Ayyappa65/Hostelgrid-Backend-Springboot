package com.hostelgrid.hostelservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hostelgrid.hostelservice.enums.RoomType;
import com.hostelgrid.hostelservice.enums.Status;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "room", indexes = {
    @Index(name = "idx_room_floor_id", columnList = "floor_id"),
    @Index(name = "idx_room_number", columnList = "room_number"),
    @Index(name = "idx_room_status", columnList = "status"),
    @Index(name = "idx_room_type", columnList = "type"),
    @Index(name = "idx_room_floor_status", columnList = "floor_id, status")
})
@Data
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Relationship with Floor
     * ManyToOne relationship with Floor entity
     * Each room belongs to one floor
    */
    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
    
    @Column(name="room_number", nullable = false, length = 20)
    private String roomNumber;

    /*
     * Relationship with RoomType
     * ManyToOne relationship with RoomType entity
     * Each room has one type like Ac or Non-Ac
    */
    @Enumerated(EnumType.STRING)
    @Column(name= "type", nullable = false)
    private RoomType type;

    /*
     * Relationship with Status
     * ManyToOne relationship with Status entity
     * Each room has one status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;


    @Column(name= "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name= "share", nullable = false)
    private Integer totalShare;

    @Column(name= "occupied_count", nullable = false)
    private Integer occupiedCount;

    @Column(name= "available_count", nullable = false)
    private Integer availableCount;

    @Column(name= "description", length = 500)
    private String description; // Room description like room has AC, attached bathroom, fridge etc.
 
    @Column(name= "bed_type", length = 50)
    private String bedType;

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
