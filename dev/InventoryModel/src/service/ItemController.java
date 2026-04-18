package service;

import domain.Item;
import domain.ItemType;
import domain.InventoryController;
import domain.Location;

import java.time.LocalDate;
import java.util.List;

public class ItemController {
    private final InventoryController inventoryController;

    public ItemController(InventoryController inventoryController) {
        if (inventoryController == null) {
            throw new IllegalArgumentException("InventoryController cannot be null");
        }
        this.inventoryController = inventoryController;
    }

    public int addItemType(String name,
                           Location storeLocation,
                           int minQuantity,
                           int costPrice,
                           int sellingPrice,
                           int categoryId,
                           String manufacturer) {
        return inventoryController.addItemType(
                name,
                storeLocation,
                minQuantity,
                costPrice,
                sellingPrice,
                categoryId,
                manufacturer
        );
    }

    public ItemType getItemTypeById(int itemTypeId) {
        return inventoryController.getItemTypeById(itemTypeId);
    }

    public List<ItemType> getAllItemTypes() {
        return inventoryController.getAllItemTypes();
    }

    public boolean updateMinQuantity(int itemTypeId, int minQuantity) {
        return inventoryController.updateMinQuantity(itemTypeId, minQuantity);
    }

    public int addItem(int itemTypeId,
                       int sellDiscount,
                       int buyDiscount,
                       LocalDate expirationDate,
                       boolean damaged,
                       boolean inWarehouse) {
        return inventoryController.addItem(
                itemTypeId,
                sellDiscount,
                buyDiscount,
                expirationDate,
                damaged,
                inWarehouse
        );
    }

    public Item getItemById(int itemId) {
        return inventoryController.getItemById(itemId);
    }

    public List<Item> getAllItems() {
        return inventoryController.getAllItems();
    }

    public List<Item> getItemsByType(int itemTypeId) {
        return inventoryController.getItemsByType(itemTypeId);
    }

    public boolean moveItemToShelf(int itemId) {
        return inventoryController.moveItemToShelf(itemId);
    }

    public boolean moveItemsToShelf(int itemTypeId, int amount) {
        return inventoryController.moveItemsToShelf(itemTypeId, amount);
    }

    public boolean moveItemToWarehouse(int itemId) {
        return inventoryController.moveItemToWarehouse(itemId);
    }

    public boolean markItemAsDamaged(int itemId) {
        return inventoryController.markItemAsDamaged(itemId);
    }

    public boolean unmarkItemAsDamaged(int itemId) {
        return inventoryController.unmarkItemAsDamaged(itemId);
    }

    public boolean updateItemExpirationDate(int itemId, LocalDate newDate) {
        return inventoryController.updateItemExpirationDate(itemId, newDate);
    }

    public boolean removeItem(int itemId) {
        return inventoryController.removeItem(itemId);
    }

    public List<ItemType> getItemTypesByCategory(int categoryId) {
        return inventoryController.getItemTypesByCategory(categoryId);
    }
}