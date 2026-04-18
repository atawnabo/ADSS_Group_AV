package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryController {

    private final CategoryController categoryController;
    private final DiscountController discountController;

    private final Map<Integer, ItemType> itemTypes;
    private final Map<ItemType, List<Item>> items;

    // השדה הזה נשאר זמנית כי אצלכם עוד לא הוכרע סופית איפה לשמור היסטוריית הנחות ספק
    private final Map<ItemType, List<SupplierDiscountHistory>> supplierDiscountHistory;

    private int itemTypeIdCounter;
    private int itemIdCounter;
    private int reportIdCounter;
    private int alertIdCounter;

    public InventoryController() {
        this.categoryController = new CategoryController();
        this.discountController = new DiscountController();
        this.itemTypes = new HashMap<>();
        this.items = new HashMap<>();
        this.supplierDiscountHistory = new HashMap<>();

        this.itemTypeIdCounter = 1;
        this.itemIdCounter = 1;
        this.reportIdCounter = 1;
        this.alertIdCounter = 1;
    }

    // =========================================================
    // CATEGORY OPERATIONS
    // =========================================================

    public String addCategory(String name) {
        return categoryController.addCategory(name);
    }

    public String addCategory(String name, int parentId) {
        return categoryController.addCategory(name, parentId);
    }

    public List<Category> getAllCategories() {
        return categoryController.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryController.getCategoryById(id);
    }

    public List<Category> getRootCategories() {
        return categoryController.getRootCategories();
    }

    // =========================================================
    // DISCOUNT OPERATIONS
    // =========================================================

    public String addItemDiscount(double percentage,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  List<Integer> itemIds) {
        List<ItemType> targetItems = new ArrayList<>();

        for (int id : itemIds) {
            ItemType item = itemTypes.get(id);
            if (item == null) {
                return "ERROR: item " + id + " not found";
            }
            targetItems.add(item);
        }

        return discountController.addItemDiscount(
                percentage, startDate, endDate, targetItems);
    }

    public String addCategoryDiscount(double percentage,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      List<Integer> categoryIds) {
        List<Category> targetCategories = new ArrayList<>();

        for (int id : categoryIds) {
            Category category = categoryController.getCategoryById(id);
            if (category == null) {
                return "ERROR: category " + id + " not found";
            }
            targetCategories.add(category);
        }

        return discountController.addCategoryDiscount(
                percentage, startDate, endDate, targetCategories);
    }

    public List<Discount> getActiveDiscountsForItem(int itemTypeId) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return new ArrayList<>();
        }
        return discountController.getActiveDiscountsForItem(itemType);
    }

    public List<Discount> getAllDiscounts() {
        return discountController.getAllDiscounts();
    }

    public double getFinalPrice(int itemTypeId) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return -1;
        }
        return discountController.getFinalPrice(itemType);
    }

    public String addSupplierDiscount(int itemTypeId,
                                      double percentage,
                                      LocalDate date,
                                      String supplierName) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return "ERROR: item not found";
        }

        return discountController.addSupplierDiscount(
                itemType, percentage, date, supplierName);
    }

    public List<SupplierDiscountHistory> getSupplierDiscountHistory(int itemTypeId) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return new ArrayList<>();
        }

        return discountController.getSupplierDiscountHistory(itemType);
    }

    // =========================================================
    // ITEM TYPE OPERATIONS
    // =========================================================

    public int addItemType(String name,
                           Location storeLocation,
                           int minQuantity,
                           int costPrice,
                           int sellingPrice,
                           int categoryId,
                           String manufacturer) {
        Category category = categoryController.getCategoryById(categoryId);
        if (category == null) {
            return -1;
        }

        ItemType itemType = new ItemType(
                itemTypeIdCounter,
                name,
                storeLocation,
                0, // shelfQuantity
                0, // warehouseQuantity
                minQuantity,
                costPrice,
                sellingPrice,
                category,
                manufacturer
        );

        itemTypes.put(itemTypeIdCounter, itemType);
        items.put(itemType, new ArrayList<>());
        supplierDiscountHistory.put(itemType, new ArrayList<>());

        return itemTypeIdCounter++;
    }

    public ItemType getItemTypeById(int itemTypeId) {
        return itemTypes.get(itemTypeId);
    }

    public List<ItemType> getAllItemTypes() {
        return new ArrayList<>(itemTypes.values());
    }

    public boolean updateMinQuantity(int itemTypeId, int minQuantity) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return false;
        }

        itemType.setMinQuantity(minQuantity);
        return true;
    }

    // =========================================================
    // ITEM OPERATIONS
    // =========================================================

   
    public int addItem(int itemTypeId,
                       int sellDiscount,
                       int buyDiscount,
                       LocalDate expirationDate,
                       boolean damaged,
                       boolean inWarehouse) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return -1;
        }

        Item item = new Item(
                itemType,
                itemIdCounter,
                sellDiscount,
                buyDiscount,
                expirationDate,
                damaged,
                inWarehouse
        );

        items.get(itemType).add(item);

        if (inWarehouse) {
            itemType.addToWarehouse(1);
        } else {
            itemType.setShelfQuantity(itemType.getShelfQuantity() + 1);
        }

        return itemIdCounter++;
    }

    public Item getItemById(int itemId) {
        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if (item.getId() == itemId) {
                    return item;
                }
            }
        }
        return null;
    }

    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();

        for (List<Item> itemList : items.values()) {
            allItems.addAll(itemList);
        }

        return allItems;
    }

    public List<Item> getItemsByType(int itemTypeId) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(items.get(itemType));
    }

    public boolean moveItemToShelf(int itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        if (!item.isInWarehouse()) {
            return false;
        }

        ItemType itemType = item.getItemType();
        itemType.addToShelf(1);
        item.setInWarehouse(false);

        return true;
    }

    public boolean moveItemsToShelf(int itemTypeId, int amount) {
        if (amount <= 0) {
            return false;
        }

        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return false;
        }

        List<Item> itemList = items.get(itemType);
        int moved = 0;

        for (Item item : itemList) {
            if (item.isInWarehouse()) {
                itemType.addToShelf(1);
                item.setInWarehouse(false);
                moved++;

                if (moved == amount) {
                    return true;
                }
            }
        }

        return moved == amount;
    }

    public boolean moveItemToWarehouse(int itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        if (item.isInWarehouse()) {
            return false;
        }

        ItemType itemType = item.getItemType();

        if (itemType.getShelfQuantity() <= 0) {
            return false;
        }

        itemType.removeFromShelf(1);
        itemType.addToWarehouse(1);
        item.setInWarehouse(true);

        return true;
    }

    public boolean markItemAsDamaged(int itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        item.setDamaged(true);
        return true;
    }

    public boolean unmarkItemAsDamaged(int itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        item.setDamaged(false);
        return true;
    }

    public boolean updateItemExpirationDate(int itemId, LocalDate newDate) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        item.setExpirationDate(newDate);
        return true;
    }

  
    public boolean removeItem(int itemId) {
        for (Map.Entry<ItemType, List<Item>> entry : items.entrySet()) {
            ItemType itemType = entry.getKey();
            List<Item> itemList = entry.getValue();

            for (Item item : itemList) {
                if (item.getId() == itemId) {
                    if (item.isInWarehouse()) {
                        itemType.removeFromWarehouse(1);
                    } else {
                        itemType.removeFromShelf(1);
                    }

                    itemList.remove(item);
                    return true;
                }
            }
        }

        return false;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private boolean belongsToCategory(ItemType itemType, Category category) {
        if (itemType == null || category == null || itemType.getCategory() == null) {
            return false;
        }

        return itemType.getCategory().getCategoryPath().contains(category);
    }

    private boolean isDefective(Item item) {
        return item.isDamaged() || item.isExpired();
    }

    // =========================================================
    // REPORT DATA HELPERS
    // =========================================================


    public List<ItemType> getItemTypesByCategory(int categoryId) {
        Category category = categoryController.getCategoryById(categoryId);
        List<ItemType> result = new ArrayList<>();

        if (category == null) {
            return result;
        }

        for (ItemType itemType : itemTypes.values()) {
            if (belongsToCategory(itemType, category)) {
                result.add(itemType);
            }
        }

        return result;
    }

    /**
     * מחזיר מיפוי של קטגוריה -> ItemTypes ששייכים אליה.
     * זה שימושי גם אם CategoryInventoryReport עצמו עדיין פשוט.
     */
    public Map<Category, List<ItemType>> getInventoryByCategories(List<Integer> categoryIds) {
        Map<Category, List<ItemType>> result = new HashMap<>();

        for (int categoryId : categoryIds) {
            Category category = categoryController.getCategoryById(categoryId);
            if (category != null) {
                result.put(category, getItemTypesByCategory(categoryId));
            }
        }

        return result;
    }

    // =========================================================
    // REPORT OPERATIONS
    // =========================================================

    public CategoryInventoryReport createCategoryInventoryReport(List<Integer> categoryIds) {
        List<Category> selectedCategories = new ArrayList<>();

        for (int categoryId : categoryIds) {
            Category category = categoryController.getCategoryById(categoryId);
            if (category != null) {
                selectedCategories.add(category);
            }
        }

        return new CategoryInventoryReport(
                reportIdCounter++,
                LocalDate.now(),
                selectedCategories
        );
    }

    public DefectiveItemReport createDefectiveItemReport() {
        Map<ItemType, List<Item>> defectiveItems = new HashMap<>();

        for (Map.Entry<ItemType, List<Item>> entry : items.entrySet()) {
            ItemType itemType = entry.getKey();
            List<Item> itemList = entry.getValue();

            List<Item> badItems = new ArrayList<>();
            for (Item item : itemList) {
                if (isDefective(item)) {
                    badItems.add(item);
                }
            }

            if (!badItems.isEmpty()) {
                defectiveItems.put(itemType, badItems);
            }
        }

        return new DefectiveItemReport(
                reportIdCounter++,
                LocalDate.now(),
                defectiveItems
        );
    }

    public PurchasingReport createPurchasingReport() {
        Map<ItemType, Integer> itemsToOrder = new HashMap<>();

        for (ItemType itemType : itemTypes.values()) {
            if (itemType.needsRestock()) {
                int missingAmount = itemType.getMinQuantity() - itemType.getTotalQuantity();

                // בגלל שה-alert מופעל גם כאשר total == min,
                // אני מוודא שתמיד נזמין לפחות 1 במקרה כזה
                int quantityToOrder = Math.max(1, missingAmount + 1);

                itemsToOrder.put(itemType, quantityToOrder);
            }
        }

        return new PurchasingReport(
                reportIdCounter++,
                LocalDate.now(),
                itemsToOrder
        );
    }

    // =========================================================
    // ALERT OPERATIONS
    // =========================================================

    public List<Alert> getAllAlerts() {
        List<Alert> alerts = new ArrayList<>();

        for (ItemType itemType : itemTypes.values()) {
            if (itemType.needsRestock()) {
                String description =
                        "Low stock alert for " + itemType.getName() +
                        ". Current quantity: " + itemType.getTotalQuantity() +
                        ", minimum quantity: " + itemType.getMinQuantity();

                alerts.add(new Alert(alertIdCounter++, description, itemType));
            }
        }

        return alerts;
    }

    public Alert getAlertForItemType(int itemTypeId) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null || !itemType.needsRestock()) {
            return null;
        }

        String description =
                "Low stock alert for " + itemType.getName() +
                ". Current quantity: " + itemType.getTotalQuantity() +
                ", minimum quantity: " + itemType.getMinQuantity();

        return new Alert(alertIdCounter++, description, itemType);
    }
}