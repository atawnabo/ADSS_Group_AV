package adss.inventory.service;

import java.time.LocalDate;
import java.util.List;

import adss.inventory.domain.Alert;
import adss.inventory.domain.InventoryController;
import adss.inventory.domain.Item;
import adss.inventory.domain.ItemType;

public class ItemController {

    private final InventoryController inventoryController;

    public ItemController(InventoryController inventoryController) {
        if (inventoryController == null) {
            throw new IllegalArgumentException("InventoryController cannot be null");
        }
        this.inventoryController = inventoryController;
    }

  public int addItemType(String name,
                       int shelfNum,
                       int aisleNum,
                       int minQuantity,
                       int costPrice,
                       int sellingPrice,
                       int categoryId,
                       String manufacturer) {
    if (name == null || name.trim().isEmpty()) return -1;
    if (costPrice < 0 || sellingPrice < 0)     return -1;
    if (minQuantity < 0)                        return -1;

    return inventoryController.addItemType(
            name, shelfNum, aisleNum, minQuantity,
            costPrice, sellingPrice, categoryId, manufacturer
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

    public Alert removeItem(int itemId) {
        return inventoryController.removeItem(itemId);
    }

    public List<ItemType> getItemTypesByCategory(int categoryId) {
        return inventoryController.getItemTypesByCategory(categoryId);
    }

    public int addItems(int itemTypeId, int amount,
            LocalDate expirationDate, boolean inWarehouse) {
        if (amount <= 0) {
            return -1;
        }
        if (itemTypeId <= 0) {
            return -1;
        }
        return inventoryController.addItems(
                itemTypeId, amount, expirationDate, inWarehouse
        );
    }

    public List<Alert> removeAllDefectiveItems() {
    return inventoryController.removeAllDefectiveItems();
}
}
