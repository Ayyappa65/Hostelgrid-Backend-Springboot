package com.hostelgrid.hostelservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hostelgrid.hostelservice.model.Item;
import com.hostelgrid.hostelservice.model.ItemCategory;

/**
 * Repository interface for Item entity operations.
 * Provides CRUD operations and custom queries for inventory items.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Find item by category and name.
     * @param category - ItemCategory entity
     * @param itemName - Item name
     * @return Optional Item
     */
    Optional<Item> findByCategoryAndItemName(ItemCategory category, String itemName);
    
    /**
     * Find all items by category.
     * @param category - ItemCategory entity
     * @return List of items in the category
     */
    List<Item> findByCategory(ItemCategory category);
    
    /**
     * Find items with low stock (available quantity <= threshold).
     * @param threshold - Minimum stock threshold
     * @return List of low stock items
     */
    @Query("SELECT i FROM Item i WHERE i.availableQuantity <= :threshold ORDER BY i.availableQuantity ASC")
    List<Item> findLowStockItems(@Param("threshold") Integer threshold);
    
    /**
     * Find items that need to be ordered.
     * @return List of items with quantityToBeOrdered > 0
     */
    @Query("SELECT i FROM Item i WHERE i.quantityToBeOrdered > 0 ORDER BY i.quantityToBeOrdered DESC")
    List<Item> findItemsToBeOrdered();
    
    /**
     * Get total investment by category.
     * @param category - ItemCategory entity
     * @return Total price invested in the category
     */
    @Query("SELECT COALESCE(SUM(i.priceInvested), 0) FROM Item i WHERE i.category = :category")
    Long getTotalInvestmentByCategory(@Param("category") ItemCategory category);
    
    /**
     * Search items by name containing keyword (case-insensitive).
     * @param keyword - Search keyword
     * @return List of matching items
     */
    @Query("SELECT i FROM Item i WHERE LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY i.itemName")
    List<Item> searchByItemName(@Param("keyword") String keyword);
}