package adss.inventory.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adss.inventory.mock.SupplierMock;
import adss.inventory.repository.DatabaseInitializer;
import adss.inventory.repository.dao.AlertDAO;
import adss.inventory.repository.dao.CategoryDAO;
import adss.inventory.repository.dao.DiscountDAO;
import adss.inventory.repository.dao.ItemDAO;
import adss.inventory.repository.dao.ItemTypeDAO;
import adss.inventory.repository.dto.AlertDTO;
import adss.inventory.repository.dto.CategoryDTO;
import adss.inventory.repository.dto.DiscountDTO;
import adss.inventory.repository.dto.ItemDTO;
import adss.inventory.repository.dto.ItemTypeDTO;

public class InventoryController {

    private final CategoryController categoryController;
    private final DiscountController discountController;

    private final CategoryDAO categoryDAO;
    private final ItemTypeDAO itemTypeDAO;
    private final ItemDAO itemDAO;
    private final DiscountDAO discountDAO;
    private final AlertDAO alertDAO;

    private final Map<Integer, ItemType> itemTypes;
    private final Map<ItemType, List<Item>> items;

    private int itemTypeIdCounter;
    private int itemIdCounter;
    private int reportIdCounter;
    private int alertIdCounter;
    private Warehouse warehouse;

    public InventoryController() {
        DatabaseInitializer.initialize();
        this.categoryController = new CategoryController();
        this.discountController = new DiscountController();

        this.categoryDAO = new CategoryDAO();
        this.itemTypeDAO = new ItemTypeDAO();
        this.itemDAO = new ItemDAO();
        this.discountDAO = new DiscountDAO();
        this.alertDAO = new AlertDAO();

        this.itemTypes = new HashMap<>();
        this.items = new HashMap<>();
        this.warehouse = new Warehouse(1, "Main Warehouse", 10000);

        this.itemTypeIdCounter = 1;
        this.itemIdCounter = 1;
        this.reportIdCounter = 1;
        this.alertIdCounter = 1;
    }

    // =========================================================
    // LOAD DATA FROM DATABASE
    // =========================================================
    public void loadDataFromDatabase() {
        loadCategoriesFromDatabase();
        loadItemTypesFromDatabase();
        loadItemsFromDatabase();
        loadDiscountsFromDatabase();
        updateCountersAfterLoad();
    }

    private void loadCategoriesFromDatabase() {
        List<CategoryDTO> dtos = categoryDAO.findAll();
        Map<Integer, Category> loadedCategories = new HashMap<>();

        boolean progress = true;

        while (loadedCategories.size() < dtos.size() && progress) {
            progress = false;

            for (CategoryDTO dto : dtos) {
                if (loadedCategories.containsKey(dto.getId())) {
                    continue;
                }

                Integer parentId = dto.getParentId();

                if (parentId == null) {
                    Category category = new Category(dto.getId(), dto.getName());
                    loadedCategories.put(dto.getId(), category);
                    progress = true;
                } else {
                    Category parent = loadedCategories.get(parentId);
                    if (parent != null) {
                        Category category = new Category(dto.getId(), dto.getName(), parent);
                        parent.addChild(category);
                        loadedCategories.put(dto.getId(), category);
                        progress = true;
                    }
                }
            }
        }

        categoryController.loadCategories(new ArrayList<>(loadedCategories.values()));
    }

    private void loadItemTypesFromDatabase() {
        itemTypes.clear();
        items.clear();

        for (ItemTypeDTO dto : itemTypeDAO.findAll()) {
            Category category = categoryController.getCategoryById(dto.getCategoryId());
            if (category == null) {
                continue;
            }

            Location location = new Location(
                    dto.getShelfNum() == null ? 0 : dto.getShelfNum(),
                    dto.getAisleNum() == null ? 0 : dto.getAisleNum()
            );

            ItemType itemType = new ItemType(
                    dto.getId(),
                    dto.getName(),
                    location,
                    dto.getShelfQuantity(),
                    dto.getWarehouseQuantity(),
                    dto.getMinQuantity(),
                    dto.getCostPrice(),
                    dto.getSellingPrice(),
                    category,
                    dto.getManufacturer()
            );

            itemTypes.put(itemType.getId(), itemType);
            items.put(itemType, new ArrayList<>());
        }
    }

    private void loadItemsFromDatabase() {
        this.warehouse = new Warehouse(1, "Main Warehouse", 10000);

        for (ItemDTO dto : itemDAO.findAll()) {
            ItemType itemType = itemTypes.get(dto.getItemTypeId());
            if (itemType == null) {
                continue;
            }

            LocalDate expirationDate = dto.getExpirationDate() == null
                    ? null
                    : LocalDate.parse(dto.getExpirationDate());

            Item item = new Item(
                    itemType,
                    dto.getId(),
                    dto.getSellDiscount(),
                    dto.getBuyDiscount(),
                    expirationDate,
                    dto.isDamaged(),
                    dto.isInWarehouse()
            );

            items.get(itemType).add(item);

            if (item.isInWarehouse()) {
                warehouse.addItem(item);
            }
        }
    }

    private void loadDiscountsFromDatabase() {
        List<Discount> loadedDiscounts = new ArrayList<>();

        for (DiscountDTO dto : discountDAO.findAll()) {
            LocalDate startDate = LocalDate.parse(dto.getStartDate());
            LocalDate endDate = LocalDate.parse(dto.getEndDate());

            if ("ITEM".equals(dto.getDiscountType())) {
                List<ItemType> targetItems = new ArrayList<>();

                for (int itemTypeId : discountDAO.findItemTargets(dto.getId())) {
                    ItemType itemType = itemTypes.get(itemTypeId);
                    if (itemType != null) {
                        targetItems.add(itemType);
                    }
                }

                loadedDiscounts.add(new ItemsDiscount(
                        dto.getId(),
                        dto.getPercentage(),
                        startDate,
                        endDate,
                        targetItems
                ));

            } else if ("CATEGORY".equals(dto.getDiscountType())) {
                List<Category> targetCategories = new ArrayList<>();

                for (int categoryId : discountDAO.findCategoryTargets(dto.getId())) {
                    Category category = categoryController.getCategoryById(categoryId);
                    if (category != null) {
                        targetCategories.add(category);
                    }
                }

                loadedDiscounts.add(new CategoryDiscount(
                        dto.getId(),
                        dto.getPercentage(),
                        startDate,
                        endDate,
                        targetCategories
                ));
            }
        }

        discountController.loadDiscounts(loadedDiscounts);
    }

    private void updateCountersAfterLoad() {
        int maxCategoryId = 0;
        for (Category category : categoryController.getAllCategories()) {
            maxCategoryId = Math.max(maxCategoryId, category.getId());
        }

        int maxItemTypeId = 0;
        for (ItemType itemType : itemTypes.values()) {
            maxItemTypeId = Math.max(maxItemTypeId, itemType.getId());
        }

        int maxItemId = 0;
        for (Item item : getAllItems()) {
            maxItemId = Math.max(maxItemId, item.getId());
        }

        int maxDiscountId = 0;
        for (Discount discount : discountController.getAllDiscounts()) {
            maxDiscountId = Math.max(maxDiscountId, discount.getId());
        }

        int maxAlertId = 0;
        for (AlertDTO alert : alertDAO.findAll()) {
            maxAlertId = Math.max(maxAlertId, alert.getId());
        }

        this.itemTypeIdCounter = maxItemTypeId + 1;
        this.itemIdCounter = maxItemId + 1;
        this.reportIdCounter = 1;
        this.alertIdCounter = maxAlertId + 1;
    }

    // =========================================================
    // CATEGORY OPERATIONS
    // =========================================================
    public String addCategory(String name) {
        String result = categoryController.addCategory(name);
        if ("OK".equals(result)) {
            saveAllCategoriesToDatabase();
        }
        return result;
    }

    public String addCategory(String name, int parentId) {
        String result = categoryController.addCategory(name, parentId);
        if ("OK".equals(result)) {
            saveAllCategoriesToDatabase();
        }
        return result;
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

        String result = discountController.addItemDiscount(
                percentage, startDate, endDate, targetItems);

        if ("OK".equals(result)) {
            saveAllDiscountsToDatabase();
        }

        return result;
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

        String result = discountController.addCategoryDiscount(
                percentage, startDate, endDate, targetCategories);

        if ("OK".equals(result)) {
            saveAllDiscountsToDatabase();
        }

        return result;
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

    // =========================================================
    // ITEM TYPE OPERATIONS
    // =========================================================
    public int addItemType(String name,
            int shelfNum,
            int aisleNum,
            int minQuantity,
            int costPrice,
            int sellingPrice,
            int categoryId,
            String manufacturer) {

        Category category = categoryController.getCategoryById(categoryId);
        if (category == null) {
            return -1;
        }

        try {
            Location location = new Location(shelfNum, aisleNum);

            ItemType itemType = new ItemType(
                    itemTypeIdCounter,
                    name,
                    location,
                    0,
                    0,
                    minQuantity,
                    costPrice,
                    sellingPrice,
                    category,
                    manufacturer
            );

            itemTypes.put(itemTypeIdCounter, itemType);
            items.put(itemType, new ArrayList<>());

            saveItemTypeToDatabase(itemType);

            return itemTypeIdCounter++;

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
            return -1;
        }
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

        try {
            itemType.setMinQuantity(minQuantity);
            saveItemTypeToDatabase(itemType);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
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
            warehouse.addItem(item);
        } else {
            itemType.setShelfQuantity(itemType.getShelfQuantity() + 1);
        }

        saveItemTypeToDatabase(itemType);
        saveItemToDatabase(item);

        return itemIdCounter++;
    }

    public int addItems(int itemTypeId, int amount,
            LocalDate expirationDate, boolean inWarehouse) {

        if (amount <= 0) {
            return -1;
        }

        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) {
            return -1;
        }

        for (int i = 0; i < amount; i++) {
            Item item = new Item(
                    itemType,
                    itemIdCounter++,
                    0,
                    0,
                    expirationDate,
                    false,
                    inWarehouse
            );

            items.get(itemType).add(item);

            if (inWarehouse) {
                itemType.addToWarehouse(1);
                warehouse.addItem(item);
            } else {
                itemType.setShelfQuantity(itemType.getShelfQuantity() + 1);
            }

            saveItemToDatabase(item);
        }

        saveItemTypeToDatabase(itemType);

        return amount;
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
        if (item == null || !item.isInWarehouse()) {
            return false;
        }

        ItemType itemType = item.getItemType();
        itemType.addToShelf(1);
        item.setInWarehouse(false);
        warehouse.removeItem(item);

        saveItemTypeToDatabase(itemType);
        saveItemToDatabase(item);

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
                warehouse.removeItem(item);
                saveItemToDatabase(item);
                moved++;

                if (moved == amount) {
                    saveItemTypeToDatabase(itemType);
                    return true;
                }
            }
        }

        saveItemTypeToDatabase(itemType);
        return moved == amount;
    }

    public boolean moveItemToWarehouse(int itemId) {
        Item item = getItemById(itemId);
        if (item == null || item.isInWarehouse()) {
            return false;
        }

        ItemType itemType = item.getItemType();

        if (itemType.getShelfQuantity() <= 0) {
            return false;
        }

        itemType.removeFromShelf(1);
        itemType.addToWarehouse(1);
        item.setInWarehouse(true);
        warehouse.addItem(item);

        saveItemTypeToDatabase(itemType);
        saveItemToDatabase(item);

        return true;
    }

    public boolean markItemAsDamaged(int itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        item.setDamaged(true);
        saveItemToDatabase(item);
        return true;
    }

    public boolean unmarkItemAsDamaged(int itemId) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        item.setDamaged(false);
        saveItemToDatabase(item);
        return true;
    }

    public boolean updateItemExpirationDate(int itemId, LocalDate newDate) {
        Item item = getItemById(itemId);
        if (item == null) {
            return false;
        }

        item.setExpirationDate(newDate);
        saveItemToDatabase(item);
        return true;
    }

    public Alert removeItem(int itemId) {
        for (Map.Entry<ItemType, List<Item>> entry : items.entrySet()) {
            ItemType itemType = entry.getKey();
            List<Item> itemList = entry.getValue();

            for (Item item : new ArrayList<>(itemList)) {
                if (item.getId() == itemId) {
                    if (item.isInWarehouse()) {
                        itemType.removeFromWarehouse(1);
                        warehouse.removeItem(item);
                    } else {
                        itemType.removeFromShelf(1);
                    }

                    itemList.remove(item);
                    itemDAO.delete(itemId);
                    saveItemTypeToDatabase(itemType);

                    Alert alert = checkAndGenerateAlert(itemType);
                    if (alert != null) {
                        saveAlertToDatabase(alert);
                    }

                    return alert;
                }
            }
        }

        return null;
    }

    private Alert checkAndGenerateAlert(ItemType itemType) {
        if (itemType.needsRestock()) {
            int quantity = itemType.getRequiredRestockQuantity();
            int orderId = SupplierMock.createOrder(itemType, quantity);

            itemType.addIncoming(quantity);
            saveItemTypeToDatabase(itemType);

            String description = "Low stock alert for " + itemType.getName()
                    + ". Current quantity: " + itemType.getTotalQuantity()
                    + ", minimum quantity: " + itemType.getMinQuantity()
                    + ". Ordered " + quantity + " units (order #" + orderId + ").";

            return new Alert(alertIdCounter++, description, itemType);
        }

        return null;
    }

    public List<Alert> removeAllDefectiveItems() {
        List<Alert> alerts = new ArrayList<>();

        for (Map.Entry<ItemType, List<Item>> entry : items.entrySet()) {
            ItemType itemType = entry.getKey();
            List<Item> itemList = entry.getValue();
            List<Item> toRemove = new ArrayList<>();

            int quantityBefore = itemType.getTotalQuantity();

            for (Item item : itemList) {
                if (isDefective(item)) {
                    toRemove.add(item);

                    if (item.isInWarehouse()) {
                        itemType.removeFromWarehouse(1);
                        warehouse.removeItem(item);
                    } else {
                        itemType.removeFromShelf(1);
                    }
                }
            }

            for (Item item : toRemove) {
                itemDAO.delete(item.getId());
            }

            itemList.removeAll(toRemove);
            saveItemTypeToDatabase(itemType);

            if (!toRemove.isEmpty()) {
                int quantityAfter = itemType.getTotalQuantity();
                boolean wasOkBefore = quantityBefore >= itemType.getMinQuantity();
                boolean isLowNow = quantityAfter < itemType.getMinQuantity();

                if (wasOkBefore && isLowNow) {
                    Alert alert = checkAndGenerateAlert(itemType);
                    if (alert != null) {
                        saveAlertToDatabase(alert);
                        alerts.add(alert);
                    }
                }
            }
        }

        return alerts;
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
        Map<Category, List<ItemType>> itemsByCategory = new HashMap<>();

        for (int categoryId : categoryIds) {
            Category category = categoryController.getCategoryById(categoryId);
            if (category != null) {
                selectedCategories.add(category);
                itemsByCategory.put(category, getItemTypesByCategory(categoryId));
            }
        }

        return new CategoryInventoryReport(
                reportIdCounter++,
                LocalDate.now(),
                selectedCategories,
                itemsByCategory
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

        for (AlertDTO dto : alertDAO.findAll()) {
            ItemType itemType = itemTypes.get(dto.getItemTypeId());
            if (itemType != null) {
                alerts.add(new Alert(dto.getId(), dto.getDescription(), itemType));
            }
        }

        return alerts;
    }

    public Alert getAlertForItemType(int itemTypeId) {
        List<AlertDTO> alertDTOs = alertDAO.findByItemTypeId(itemTypeId);

        if (!alertDTOs.isEmpty()) {
            AlertDTO dto = alertDTOs.get(alertDTOs.size() - 1);
            ItemType itemType = itemTypes.get(dto.getItemTypeId());

            if (itemType != null) {
                return new Alert(dto.getId(), dto.getDescription(), itemType);
            }
        }

        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null || !itemType.needsRestock()) {
            return null;
        }

        String description = "Low stock alert for " + itemType.getName()
                + ". Current quantity: " + itemType.getTotalQuantity()
                + ", minimum quantity: " + itemType.getMinQuantity();

        Alert alert = new Alert(alertIdCounter++, description, itemType);
        saveAlertToDatabase(alert);

        return alert;
    }

    // =========================================================
    // DATABASE SAVE HELPERS
    // =========================================================
    private void saveAllCategoriesToDatabase() {
        for (Category category : categoryController.getAllCategories()) {
            Integer parentId = category.getParent() == null
                    ? null
                    : category.getParent().getId();

            categoryDAO.save(new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    parentId
            ));
        }
    }

    private void saveItemTypeToDatabase(ItemType itemType) {
        itemTypeDAO.save(new ItemTypeDTO(
                itemType.getId(),
                itemType.getName(),
                itemType.getStoreLocation().getShelfNum(),
                itemType.getStoreLocation().getAisleNum(),
                itemType.getShelfQuantity(),
                itemType.getWarehouseQuantity(),
                itemType.getMinQuantity(),
                itemType.getCostPrice(),
                itemType.getSellingPrice(),
                itemType.getCategory().getId(),
                itemType.getManufacturer()
        ));
    }

    private void saveItemToDatabase(Item item) {
        itemDAO.save(new ItemDTO(
                item.getId(),
                item.getItemType().getId(),
                item.getSellDiscount(),
                item.getBuyDiscount(),
                item.getItemPrice(),
                item.getItemSellPrice(),
                item.getExpirationDate() == null ? null : item.getExpirationDate().toString(),
                item.isDamaged(),
                item.isInWarehouse()
        ));
    }

    private void saveAlertToDatabase(Alert alert) {
        alertDAO.save(new AlertDTO(
                alert.getId(),
                alert.getDescription(),
                alert.getItemType().getId()
        ));
    }

    private void saveAllDiscountsToDatabase() {
        for (Discount discount : discountController.getAllDiscounts()) {
            if (discount instanceof ItemsDiscount) {
                ItemsDiscount itemsDiscount = (ItemsDiscount) discount;

                discountDAO.save(new DiscountDTO(
                        discount.getId(),
                        "ITEM",
                        discount.getPercentage(),
                        discount.getStartDate().toString(),
                        discount.getEndDate().toString()
                ));

                for (ItemType itemType : itemsDiscount.getTargetItems()) {
                    discountDAO.saveItemTarget(discount.getId(), itemType.getId());
                }

            } else if (discount instanceof CategoryDiscount) {
                CategoryDiscount categoryDiscount = (CategoryDiscount) discount;

                discountDAO.save(new DiscountDTO(
                        discount.getId(),
                        "CATEGORY",
                        discount.getPercentage(),
                        discount.getStartDate().toString(),
                        discount.getEndDate().toString()
                ));

                for (Category category : categoryDiscount.getTargetCategories()) {
                    discountDAO.saveCategoryTarget(discount.getId(), category.getId());
                }
            }
        }
    }

    public void initializeSimpleData() {
    // currently no simple data initialization here
    // keep this empty unless you want to move your SimpleData logic here
}

public void clearSystemData() {
    clearDatabase();

    categoryController.loadCategories(new ArrayList<>());
    discountController.loadDiscounts(new ArrayList<>());

    itemTypes.clear();
    items.clear();

    warehouse = new Warehouse(1, "Main Warehouse", 10000);

    itemTypeIdCounter = 1;
    itemIdCounter = 1;
    reportIdCounter = 1;
    alertIdCounter = 1;
}

private void clearDatabase() {
    try (java.sql.Connection connection =
                 adss.inventory.repository.DatabaseManager.getConnection();
         java.sql.Statement statement = connection.createStatement()) {

        statement.executeUpdate("DELETE FROM item_discount_targets");
        statement.executeUpdate("DELETE FROM category_discount_targets");
        statement.executeUpdate("DELETE FROM alerts");
        statement.executeUpdate("DELETE FROM items");
        statement.executeUpdate("DELETE FROM discounts");
        statement.executeUpdate("DELETE FROM item_types");
        statement.executeUpdate("DELETE FROM categories");

    } catch (java.sql.SQLException e) {
        throw new RuntimeException("Failed to clear database", e);
    }
}
}