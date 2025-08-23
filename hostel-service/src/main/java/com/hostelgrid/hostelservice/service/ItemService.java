package com.hostelgrid.hostelservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostelgrid.hostelservice.model.Item;
import com.hostelgrid.hostelservice.model.ItemCategory;
import com.hostelgrid.hostelservice.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Item operations.
 * Handles business logic for inventory item management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final ItemCategoryService categoryService;
    
    /**
     * Create a new inventory item.
     * @param categoryId - Category ID
     * @param itemName - Item name
     * @param initialQuantity - Initial available quantity
     * @param priceInvested - Initial price invested
     * @return Created Item
     * @throws IllegalArgumentException if category not found or item already exists
     */
    public Item createItem(Long categoryId, String itemName, Integer initialQuantity, Long priceInvested) {
        log.info("Creating new item: {} in category ID: {}", itemName, categoryId);
        
        // Validate category exists
        ItemCategory category = categoryService.findById(categoryId)
            .orElseThrow(() -> {
                log.error("Category not found for item creation: {}", categoryId);
                return new IllegalArgumentException("Category not found: " + categoryId);
            });
        
        // Check if item already exists in this category
        if (itemRepository.findByCategoryAndItemName(category, itemName).isPresent()) {
            log.warn("Item creation failed - already exists: {} in category: {}", itemName, category.getCategoryName());
            throw new IllegalArgumentException("Item already exists in this category: " + itemName);
        }
        
        Item item = new Item();
        item.setCategory(category);
        item.setItemName(itemName.trim());
        item.setQuantityOrderTillNow(initialQuantity);
        item.setAvailableQuantity(initialQuantity);
        item.setQuantityUsedTillNow(0);
        item.setQuantityInUse(0);
        item.setQuantityToBeOrdered(0);
        item.setPriceInvested(priceInvested);
        
        Item savedItem = itemRepository.save(item);
        log.info("Item created successfully with ID: {}", savedItem.getId());
        
        return savedItem;
    }
    
    /**
     * Update item stock when new quantity is ordered.
     * @param itemId - Item ID
     * @param orderedQuantity - Quantity ordered
     * @param additionalCost - Additional cost for this order
     * @return Updated Item
     * @throws IllegalArgumentException if item not found
     */
    public Item addStock(Long itemId, Integer orderedQuantity, Long additionalCost) {
        log.info("Adding stock for item ID: {}, quantity: {}", itemId, orderedQuantity);
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.error("Item not found for stock addition: {}", itemId);
                return new IllegalArgumentException("Item not found: " + itemId);
            });
        
        // Update quantities
        item.setQuantityOrderTillNow(item.getQuantityOrderTillNow() + orderedQuantity);
        item.setAvailableQuantity(item.getAvailableQuantity() + orderedQuantity);
        item.setPriceInvested(item.getPriceInvested() + additionalCost);
        
        // Reset reorder quantity if stock was added
        if (item.getQuantityToBeOrdered() > 0) {
            item.setQuantityToBeOrdered(Math.max(0, item.getQuantityToBeOrdered() - orderedQuantity));
        }
        
        Item updatedItem = itemRepository.save(item);
        log.info("Stock added successfully for item: {}", itemId);
        
        return updatedItem;
    }
    
    /**
     * Use/consume item from inventory.
     * @param itemId - Item ID
     * @param usedQuantity - Quantity to use
     * @return Updated Item
     * @throws IllegalArgumentException if item not found or insufficient stock
     */
    public Item useItem(Long itemId, Integer usedQuantity) {
        log.info("Using item ID: {}, quantity: {}", itemId, usedQuantity);
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.error("Item not found for usage: {}", itemId);
                return new IllegalArgumentException("Item not found: " + itemId);
            });
        
        // Check if sufficient stock available
        if (item.getAvailableQuantity() < usedQuantity) {
            log.warn("Insufficient stock for item: {}, available: {}, requested: {}", 
                    itemId, item.getAvailableQuantity(), usedQuantity);
            throw new IllegalArgumentException("Insufficient stock available");
        }
        
        // Update quantities
        item.setAvailableQuantity(item.getAvailableQuantity() - usedQuantity);
        item.setQuantityInUse(item.getQuantityInUse() + usedQuantity);
        
        Item updatedItem = itemRepository.save(item);
        log.info("Item used successfully: {}", itemId);
        
        return updatedItem;
    }
    
    /**
     * Return item to inventory (from in-use to available).
     * @param itemId - Item ID
     * @param returnedQuantity - Quantity returned
     * @return Updated Item
     * @throws IllegalArgumentException if item not found or invalid return quantity
     */
    public Item returnItem(Long itemId, Integer returnedQuantity) {
        log.info("Returning item ID: {}, quantity: {}", itemId, returnedQuantity);
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.error("Item not found for return: {}", itemId);
                return new IllegalArgumentException("Item not found: " + itemId);
            });
        
        // Check if sufficient quantity is in use
        if (item.getQuantityInUse() < returnedQuantity) {
            log.warn("Invalid return quantity for item: {}, in-use: {}, return requested: {}", 
                    itemId, item.getQuantityInUse(), returnedQuantity);
            throw new IllegalArgumentException("Cannot return more than what's in use");
        }
        
        // Update quantities
        item.setQuantityInUse(item.getQuantityInUse() - returnedQuantity);
        item.setAvailableQuantity(item.getAvailableQuantity() + returnedQuantity);
        
        Item updatedItem = itemRepository.save(item);
        log.info("Item returned successfully: {}", itemId);
        
        return updatedItem;
    }
    
    /**
     * Mark item as consumed/used up permanently.
     * @param itemId - Item ID
     * @param consumedQuantity - Quantity consumed
     * @return Updated Item
     */
    public Item consumeItem(Long itemId, Integer consumedQuantity) {
        log.info("Consuming item ID: {}, quantity: {}", itemId, consumedQuantity);
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.error("Item not found for consumption: {}", itemId);
                return new IllegalArgumentException("Item not found: " + itemId);
            });
        
        // Check if sufficient quantity is in use
        if (item.getQuantityInUse() < consumedQuantity) {
            log.warn("Invalid consumption quantity for item: {}, in-use: {}, consume requested: {}", 
                    itemId, item.getQuantityInUse(), consumedQuantity);
            throw new IllegalArgumentException("Cannot consume more than what's in use");
        }
        
        // Update quantities
        item.setQuantityInUse(item.getQuantityInUse() - consumedQuantity);
        item.setQuantityUsedTillNow(item.getQuantityUsedTillNow() + consumedQuantity);
        
        Item updatedItem = itemRepository.save(item);
        log.info("Item consumed successfully: {}", itemId);
        
        return updatedItem;
    }
    
    /**
     * Set reorder quantity for an item.
     * @param itemId - Item ID
     * @param reorderQuantity - Quantity to be ordered
     * @return Updated Item
     */
    public Item setReorderQuantity(Long itemId, Integer reorderQuantity) {
        log.info("Setting reorder quantity for item ID: {}, quantity: {}", itemId, reorderQuantity);
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.error("Item not found for reorder setting: {}", itemId);
                return new IllegalArgumentException("Item not found: " + itemId);
            });
        
        item.setQuantityToBeOrdered(reorderQuantity);
        
        Item updatedItem = itemRepository.save(item);
        log.info("Reorder quantity set successfully for item: {}", itemId);
        
        return updatedItem;
    }
    
    /**
     * Get all items by category.
     * @param categoryId - Category ID
     * @return List of items
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsByCategory(Long categoryId) {
        log.debug("Fetching items for category ID: {}", categoryId);
        
        ItemCategory category = categoryService.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        List<Item> items = itemRepository.findByCategory(category);
        log.debug("Found {} items in category: {}", items.size(), category.getCategoryName());
        
        return items;
    }
    
    /**
     * Get items with low stock.
     * @param threshold - Stock threshold
     * @return List of low stock items
     */
    @Transactional(readOnly = true)
    public List<Item> getLowStockItems(Integer threshold) {
        log.debug("Fetching low stock items with threshold: {}", threshold);
        List<Item> items = itemRepository.findLowStockItems(threshold);
        log.debug("Found {} low stock items", items.size());
        return items;
    }
    
    /**
     * Get items that need to be ordered.
     * @return List of items to be ordered
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsToBeOrdered() {
        log.debug("Fetching items to be ordered");
        List<Item> items = itemRepository.findItemsToBeOrdered();
        log.debug("Found {} items to be ordered", items.size());
        return items;
    }
    
    /**
     * Search items by name.
     * @param keyword - Search keyword
     * @return List of matching items
     */
    @Transactional(readOnly = true)
    public List<Item> searchItems(String keyword) {
        log.debug("Searching items with keyword: {}", keyword);
        List<Item> items = itemRepository.searchByItemName(keyword);
        log.debug("Found {} items matching keyword", items.size());
        return items;
    }
    
    /**
     * Find item by ID.
     * @param id - Item ID
     * @return Optional Item
     */
    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        log.debug("Finding item by ID: {}", id);
        return itemRepository.findById(id);
    }
}