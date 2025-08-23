package com.hostelgrid.hostelservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "item", indexes = {
       @Index(name = "idx_category_item_name", columnList = "item_name, category_id")
       })
@Data
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory category;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity_ordered_till_now")
    private Integer quantityOrderTillNow;  // Total ordered historically

    @Column(name = "available_quantity")
    private Integer availableQuantity;    // Currently available

    @Column(name = "quantity_used_till_now")
    private Integer quantityUsedTillNow;  // Total used historically

    @Column(name = "quantity_in_use")
    private Integer quantityInUse;        // Currently in use

    @Column(name = "quantity_to_be_ordered")
    private Integer quantityToBeOrdered;   // To be ordered in the future

    @Column(name = "price_invested")
    private Long priceInvested;    // Total price invested

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
