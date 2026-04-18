
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
    private final Map<ItemType, List<SupplierDiscountHistory>> supplierDiscountHistory;
    private int itemTypeIdCounter;
    private int itemIdCounter;

    public InventoryController() {
        this.categoryController = new CategoryController();
        this.discountController = new DiscountController();
        this.supplierDiscountHistory = new HashMap<>();
        this.itemTypes = new HashMap<>();
        this.items = new HashMap<>();
        this.itemTypeIdCounter = 1;
        this.itemIdCounter = 1;
    }

    // ==================== CATEGORY OPERATIONS ====================
    /**
     * Adds a root category (no parent) to the system.
     *
     * @param name the name of the new category
     * @return "OK" if added successfully
     */
    public String addCategory(String name) {
        return categoryController.addCategory(name);
    }

    /**
     * Adds a sub-category under an existing parent category.
     *
     * @param name the name of the new sub-category
     * @param parentId the ID of the parent category
     * @return "OK" if added, "ERROR" if parent not found
     */
    public String addCategory(String name, int parentId) {
        return categoryController.addCategory(name, parentId);
    }

    /**
     * Returns all categories in the system as a list.
     *
     * @return list of all Category objects
     */
    public List<Category> getAllCategories() {
        return categoryController.getAllCategories();
    }

    /**
     * Finds and returns a single category by its ID.
     *
     * @param id the category ID to look up
     * @return the Category if found, null otherwise
     */
    public Category getCategoryById(int id) {
        return categoryController.getCategoryById(id);
    }

    /**
     * Returns all root categories (no parent). Used for tree display in
     * presentation layer.
     *
     * @return list of root Category objects
     */
    public List<Category> getRootCategories() {
        return categoryController.getRootCategories();
    }

    // ==================== DISCOUNT OPERATIONS ====================
    /**
     * Creates a new discount on a specific list of items. Resolves item IDs to
     * objects before delegating to DiscountController.
     *
     * @param percentage the discount percentage (e.g. 10.0 for 10%)
     * @param startDate the date the discount becomes active
     * @param endDate the date the discount expires
     * @param itemIds list of ItemType IDs to apply the discount to
     * @return "OK" if created, "ERROR" if any item ID not found
     */
    public String addItemDiscount(double percentage,
            LocalDate startDate, LocalDate endDate,
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

    /**
     * Creates a new discount on a specific list of categories. Resolves
     * category IDs to objects before delegating to DiscountController.
     *
     * @param percentage the discount percentage (e.g. 15.0 for 15%)
     * @param startDate the date the discount becomes active
     * @param endDate the date the discount expires
     * @param categoryIds list of category IDs to apply the discount to
     * @return "OK" if created, "ERROR" if any category ID not found
     */
    public String addCategoryDiscount(double percentage,
            LocalDate startDate, LocalDate endDate,
            List<Integer> categoryIds) {
        List<Category> targetCategories = new ArrayList<>();
        for (int id : categoryIds) {
            Category cat = categoryController.getCategoryById(id);
            if (cat == null) {
                return "ERROR: category " + id + " not found";
            }
            targetCategories.add(cat);
        }
        return discountController.addCategoryDiscount(
                percentage, startDate, endDate, targetCategories);
    }

    /**
     * Returns all active discounts for a given product.
     *
     * @param itemTypeId the ID of the product
     * @return list of active Discount objects, empty if not found
     */
    public List<Discount> getActiveDiscountsForItem(int itemTypeId) {
        ItemType item = itemTypes.get(itemTypeId);
        if (item == null) {
            return new ArrayList<>();
        }
        return discountController.getActiveDiscountsForItem(item);
    }

    /**
     * Returns all discounts in the system (both item and category discounts).
     * Used for displaying all promotions to the user.
     *
     * @return list of all Discount objects
     */
    public List<Discount> getAllDiscounts() {
        return discountController.getAllDiscounts();
    }

    /**
     * Calculates final selling price after best active discount.
     *
     * @param itemTypeId the ID of the product
     * @return final price, or -1 if item not found
     */
    public double getFinalPrice(int itemTypeId) {
        ItemType item = itemTypes.get(itemTypeId);
        if (item == null) {
            return -1;
        }
        return discountController.getFinalPrice(item);
    }

    //option1  save in itemType
    /**
     * Records a supplier discount for a specific product.
     *
     * @param itemTypeId the ID of the product
     * @param percentage the discount percentage from supplier
     * @param date the date the discount was given
     * @param supplierName the name of the supplier
     * @return "OK" if recorded, "ERROR" if item not found
     */
    public String addSupplierDiscount(int itemTypeId, double percentage,
            LocalDate date, String supplierName) {
        ItemType item = itemTypes.get(itemTypeId);
        if (item == null) {
            return "ERROR: item not found";
        }
        return discountController.addSupplierDiscount(
                item, percentage, date, supplierName);
    }

    /**
     * Returns the full supplier discount history for a specific product. Shows
     * all past discounts received from suppliers for this product.
     *
     * @param itemTypeId the ID of the product
     * @return list of SupplierDiscountHistory, empty list if item not found
     */
    public List<SupplierDiscountHistory> getSupplierDiscountHistory(int itemTypeId) {
        ItemType item = itemTypes.get(itemTypeId);
        if (item == null) {
            return new ArrayList<>();
        }
        return discountController.getSupplierDiscountHistory(item);
    }

    ////option 2 to save here
    /**
     * Records a supplier discount for a specific product. Stored in the
     * controller's history map, not inside ItemType.
     */
    public String addSupplierDiscount2(int itemTypeId, double percentage,
            LocalDate date, String supplierName) {
        ItemType item = itemTypes.get(itemTypeId);
        if (item == null) {
            return "ERROR: item not found";
        }

        SupplierDiscountHistory history = new SupplierDiscountHistory(
                itemTypeId, percentage, date, supplierName
        );
        supplierDiscountHistory.get(item).add(history);
        return "OK";
    }

    /**
     * Returns full supplier discount history for a specific product.
     */
    public List<SupplierDiscountHistory> getSupplierDiscountHistory2(int itemTypeId) {
        ItemType item = itemTypes.get(itemTypeId);
        if (item == null) {
            return new ArrayList<>();
        }
        return supplierDiscountHistory.get(item);
    }
}
