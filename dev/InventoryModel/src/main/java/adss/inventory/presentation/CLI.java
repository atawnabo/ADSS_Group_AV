package adss.inventory.presentation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import adss.inventory.domain.Alert;
import adss.inventory.domain.Location;
import adss.inventory.domain.Category;
import adss.inventory.domain.CategoryInventoryReport;
import adss.inventory.domain.Discount;
import adss.inventory.domain.Item;
import adss.inventory.domain.ItemType;
import adss.inventory.domain.DefectiveItemReport;
import adss.inventory.domain.PurchasingReport;
import adss.inventory.init.DataLoader;

public class CLI {

    private final PresentationController controller;
    private final Scanner scanner;
    private final DataLoader dataLoader;

    public CLI() {
        this.controller = new PresentationController();
        this.scanner = new Scanner(System.in);
        this.dataLoader = new DataLoader(controller);
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }

    public void start() {
        boolean running = true;

        System.out.println("==================================");
        System.out.println(" Welcome to Inventory Management ");
        System.out.println("==================================");

        // ask user to load sample data
        System.out.println("\n1. Load sample data");
        System.out.println("2. Start empty");
        int loadChoice = readInt("Choose: ");

        if (loadChoice == 1) {
            dataLoader.load();
            System.out.println("✓ Sample data loaded successfully");
        } else {
            System.out.println("✓ Starting with empty system");
        }

        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 ->
                    categoryMenu();
                case 2 ->
                    itemTypeMenu();
                case 3 ->
                    itemMenu();
                case 4 ->
                    discountMenu();
                case 5 ->
                    reportMenu();
                case 6 ->
                    alertMenu();
                case 0 -> {
                    running = false;
                    System.out.println("Exiting system...");
                }
                default ->
                    System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    private void printMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Category operations");
        System.out.println("2. ItemType operations");
        System.out.println("3. Item operations");
        System.out.println("4. Discount operations");
        System.out.println("5. Reports");
        System.out.println("6. Alerts");
        System.out.println("0. Exit");
    }

    // =========================================================
    // CATEGORY MENU
    // =========================================================
    private void categoryMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n====== Category Menu ======");
            System.out.println("1. Add root category");
            System.out.println("2. Add sub-category");
            System.out.println("3. Show all categories");
            System.out.println("4. Show category by ID");
            System.out.println("5. Show root categories");
            System.out.println("6. Show category tree");
            System.out.println("0. Back");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addRootCategory();
                    break;
                case 2:
                    addSubCategory();
                    break;
                case 3:
                    showAllCategories();
                    break;
                case 4:
                    showCategoryById();
                    break;
                case 5:
                    showRootCategories();
                    break;
                case 6:
                    viewCategoryTree();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addRootCategory() {
        System.out.print("Enter category name: ");
        String name = scanner.nextLine();
        String result = controller.addCategory(name);
        System.out.println(result.equals("OK")
                ? "Category '" + name + "' added successfully"
                : result);
    }

    private void addSubCategory() {
        List<Category> cats = controller.getAllCategories();
        if (cats.isEmpty()) {
            System.out.println("No categories exist. Add a root category first.");
            return;
        }

        // show all categories for parent selection
        System.out.println("\nSelect parent category:");
        for (Category cat : cats) {
            System.out.println(cat.getId() + ". " + cat.getFullPath());
        }

        System.out.print("Enter parent number: ");
        int parentId = scanner.nextInt();
        scanner.nextLine();

        // validate parent exists
        Category parent = controller.getCategoryById(parentId);
        if (parent == null) {
            System.out.println("ERROR: category not found");
            return;
        }

        System.out.print("Enter sub-category name: ");
        String name = scanner.nextLine();

        String result = controller.addCategory(name, parentId);
        System.out.println(result.equals("OK")
                ? "Sub-category '" + name + "' added under '" + parent.getFullPath() + "'"
                : result);
    }

    private void showAllCategories() {
        List<Category> cats = controller.getAllCategories();
        if (cats.isEmpty()) {
            System.out.println("No categories exist.");
            return;
        }
        System.out.println("\n====== CATEGORIES ======");
        for (Category cat : cats) {
            System.out.println(cat.getId() + ". " + cat.getFullPath());
        }
        System.out.println("========================");
    }

    private void showCategoryById() {
        int id = readInt("Enter category ID: ");
        Category category = controller.getCategoryById(id);
        if (category == null) {
            System.out.println("Category not found.");
            return;
        }

        System.out.println(category);
    }

    private void showRootCategories() {
        List<Category> categories = controller.getRootCategories();
        if (categories.isEmpty()) {
            System.out.println("No root categories exist.");
            return;
        }
        System.out.println("\n====== ROOT CATEGORIES ======");
        for (Category cat : categories) {
            System.out.println(cat.getId() + ". " + cat.getName());
        }
        System.out.println("=============================");
    }

    public void viewCategoryTree() {
        List<Category> roots = controller.getRootCategories();
        if (roots.isEmpty()) {
            System.out.println("No categories exist.");
            return;
        }
        System.out.println("\n====== CATEGORY TREE ======");
        for (Category root : roots) {
            printTree(root, "");
        }
        System.out.println("===========================");
    }

    private void printTree(Category cat, String indent) {
        System.out.println(indent + cat.getName());
        for (Category child : cat.getChildren()) {
            printTree(child, indent + "  ");
        }
    }

    public int selectCategory() {
        List<Category> cats = controller.getAllCategories();
        if (cats.isEmpty()) {
            System.out.println("No categories exist.");
            return -1;
        }

        System.out.println("\nAvailable categories:");
        for (Category cat : cats) {
            System.out.println(cat.getId() + ". " + cat.getFullPath());
        }
        System.out.println("0. Cancel");
        System.out.print("Choose category: ");

        int id = scanner.nextInt();
        scanner.nextLine();

        if (id == 0) {
            return -1;
        }

        Category selected = controller.getCategoryById(id);
        if (selected == null) {
            System.out.println("ERROR: category not found");
            return -1;
        }

        System.out.println("Selected: " + selected.getFullPath());
        return id;
    }

    // =========================================================
    // ITEM TYPE MENU
    // =========================================================
    private void itemTypeMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n====== ItemType Menu ======");
            System.out.println("1. Add item type");
            System.out.println("2. Show all item types");
            System.out.println("3. Show item type by ID");
            System.out.println("4. Update minimum quantity");
            System.out.println("5. Show item types by category");
            System.out.println("0. Back");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addItemType();
                    break;
                case 2:
                    showAllItemTypes();
                    break;
                case 3:
                    showItemTypeById();
                    break;
                case 4:
                    updateMinQuantity();
                    break;
                case 5:
                    showItemTypesByCategory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

private void addItemType() {
    System.out.println("\n====== Add Item Type ======");

    String name = readLine("Enter item type name: ");
    int shelfNum = readInt("Enter store shelf number: ");
    int aisleNum = readInt("Enter store aisle number: ");
    int minQuantity = readInt("Enter minimum quantity: ");
    int costPrice = readInt("Enter cost price: ");
    int sellingPrice = readInt("Enter selling price: ");
    String manufacturer = readLine("Enter manufacturer: ");

    System.out.println("\nSelect a category for this item:");
    int categoryId = selectCategory();
    if (categoryId == -1) {
        System.out.println("Cancelled. Item type not added.");
        return;
    }

    // pass ints, NOT new Location() — controller handles creation
    int id = controller.addItemType(
            name,
            shelfNum,   // ← int
            aisleNum,   // ← int
            minQuantity,
            costPrice,
            sellingPrice,
            categoryId,
            manufacturer
    );

    if (id == -1) {
        System.out.println("Failed to add item type.");
    } else {
        System.out.println("\nItem type added successfully!");
        System.out.println("  ID:       " + id);
        System.out.println("  Name:     " + name);
        System.out.println("  Category: " + controller.getCategoryById(categoryId).getFullPath());
        System.out.println("  Min Qty:  " + minQuantity);
        System.out.println("  Cost:     " + costPrice);
        System.out.println("  Price:    " + sellingPrice);
    }
}

    private void showAllItemTypes() {
        List<ItemType> itemTypes = controller.getAllItemTypes();
        if (itemTypes.isEmpty()) {
            System.out.println("No item types found.");
            return;
        }

        for (ItemType itemType : itemTypes) {
            System.out.println(itemType);
        }
    }

    private void showItemTypeById() {
        int id = readInt("Enter item type ID: ");
        ItemType itemType = controller.getItemTypeById(id);

        if (itemType == null) {
            System.out.println("ItemType not found.");
            return;
        }

        System.out.println(itemType);
    }

    private void updateMinQuantity() {
        int itemTypeId = readInt("Enter item type ID: ");
        int minQuantity = readInt("Enter new minimum quantity: ");

        boolean success = controller.updateMinQuantity(itemTypeId, minQuantity);
        System.out.println(success ? "Minimum quantity updated." : "ERROR.");
    }

    private void showItemTypesByCategory() {
        int categoryId = selectCategory();
        if (categoryId == -1) {
            System.out.println("Cancelled.");
            return;
        }

        List<ItemType> itemTypes = controller.getItemTypesByCategory(categoryId);

        if (itemTypes.isEmpty()) {
            System.out.println("No item types found for this category.");
            return;
        }

        for (ItemType itemType : itemTypes) {
            System.out.println(itemType);
        }
    }

    // =========================================================
    // ITEM MENU
    // =========================================================
    private void itemMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n------ Item Menu ------");
            System.out.println("1.  Add single item");
            System.out.println("2.  Add multiple items of same type");
            System.out.println("3.  Show all items");
            System.out.println("4.  Show item by ID");
            System.out.println("5.  Show items by item type");
            System.out.println("6.  Move one item to shelf");
            System.out.println("7.  Move multiple items to shelf");
            System.out.println("8.  Move one item to warehouse");
            System.out.println("9.  Mark item as damaged");
            System.out.println("10. Unmark item as damaged");
            System.out.println("11. Update item expiration date");
            System.out.println("12. Remove item");
            System.out.println("13. Remove all defective items");
            System.out.println("0.  Back");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    addItems();
                    break;
                case 3:
                    showAllItems();
                    break;
                case 4:
                    showItemById();
                    break;
                case 5:
                    showItemsByType();
                    break;
                case 6:
                    moveItemToShelf();
                    break;
                case 7:
                    moveItemsToShelf();
                    break;
                case 8:
                    moveItemToWarehouse();
                    break;
                case 9:
                    markItemAsDamaged();
                    break;
                case 10:
                    unmarkItemAsDamaged();
                    break;
                case 11:
                    updateExpirationDate();
                    break;
                case 12:
                    removeItem();
                    break;
                case 13:
                    removeAllDefectiveItems();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

 private void removeAllDefectiveItems() {
    System.out.println("\nCurrent defective items:");
    DefectiveItemReport report = controller.createDefectiveItemReport();
    System.out.println(report);

    boolean confirm = readBoolean("Are you sure? (true/false): ");
    if (!confirm) {
        System.out.println("Cancelled.");
        return;
    }

    List<Alert> alerts = controller.removeAllDefectiveItems();
    System.out.println("Defective items removed.");

    // print all alerts ONCE after everything is done
    if (!alerts.isEmpty()) {
        System.out.println("\n⚠ LOW STOCK ALERTS:");
        for (Alert alert : alerts)
            System.out.println(alert);
    }
}

    private void addItem() {
        System.out.println("\n====== ADD ITEM ======");

        // select item type
        System.out.println("\n====== SELECT ITEM TYPE ======");
        int itemTypeId = selectItemType();
        if (itemTypeId == -1) {
            System.out.println("Cancelled. Item not added.");
            return;
        }

        int sellDiscount = readInt("Enter sell discount (%): ");
        int buyDiscount = readInt("Enter buy discount (%): ");
        LocalDate expirationDate = readOptionalDate("Enter expiration date (yyyy-mm-dd) or leave empty: ");
        boolean damaged = readBoolean("Is item damaged? (true/false): ");
        boolean inWarehouse = readBoolean("Is item in warehouse? (true/false): ");

        int itemId = controller.addItem(
                itemTypeId,
                sellDiscount,
                buyDiscount,
                expirationDate,
                damaged,
                inWarehouse
        );

        if (itemId == -1) {
            System.out.println("==============================");
            System.out.println("ERROR: Item type not found.");
            System.out.println("==============================");
        } else {
            System.out.println("\n====== ITEM ADDED ======");
        }
    }

    private void addItems() {
        System.out.println("\n====== ADD MULTIPLE ITEMS ======");

        int itemTypeId = selectItemType();
        if (itemTypeId == -1) {
            System.out.println("Cancelled.");
            return;
        }

        int amount = readInt("Enter amount to add: ");
        LocalDate expirationDate = readOptionalDate("Enter expiration date (yyyy-mm-dd) or leave empty: ");
        boolean inWarehouse = readBoolean("Store in warehouse? (true/false): ");

        int result = controller.addItems(itemTypeId, amount, expirationDate, inWarehouse);

        if (result == -1) {
            System.out.println("ERROR: item type not found");
        } else {
            System.out.println("✓ " + result + " items added successfully");
        }
    }

    private void showAllItems() {
        List<Item> items = controller.getAllItems();
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }

        for (Item item : items) {
            System.out.println(item);
        }
    }

    private void showItemById() {
        int itemId = readInt("Enter item ID: ");
        Item item = controller.getItemById(itemId);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println(item);
    }

    private void showItemsByType() {
        int itemTypeId = readInt("Enter item type ID: ");
        List<Item> items = controller.getItemsByType(itemTypeId);

        if (items.isEmpty()) {
            System.out.println("No items found for this item type.");
            return;
        }

        for (Item item : items) {
            System.out.println(item);
        }
    }

    private void moveItemToShelf() {
        int itemId = readInt("Enter item ID: ");
        boolean success = controller.moveItemToShelf(itemId);
        System.out.println(success ? "Item moved to shelf." : "Failed to move item to shelf.");
    }

    private void moveItemsToShelf() {
        int itemTypeId = readInt("Enter item type ID: ");
        int amount = readInt("Enter amount to move: ");
        boolean success = controller.moveItemsToShelf(itemTypeId, amount);
        System.out.println(success ? "Items moved to shelf." : "Failed to move items.");
    }

    private void moveItemToWarehouse() {
        int itemId = readInt("Enter item ID: ");
        boolean success = controller.moveItemToWarehouse(itemId);
        System.out.println(success ? "Item moved to warehouse." : "Failed to move item.");
    }

    private void markItemAsDamaged() {
        int itemId = readInt("Enter item ID: ");
        boolean success = controller.markItemAsDamaged(itemId);
        System.out.println(success ? "Item marked as damaged." : "Item not found.");
    }

    private void unmarkItemAsDamaged() {
        int itemId = readInt("Enter item ID: ");
        boolean success = controller.unmarkItemAsDamaged(itemId);
        System.out.println(success ? "Item unmarked as damaged." : "Item not found.");
    }

    private void updateExpirationDate() {
        int itemId = readInt("Enter item ID: ");
        LocalDate newDate = readDate("Enter new expiration date (yyyy-mm-dd): ");
        boolean success = controller.updateItemExpirationDate(itemId, newDate);
        System.out.println(success ? "Expiration date updated." : "Item not found.");
    }

    private void removeItem() {
    int itemId = readInt("Enter item ID: ");
    Alert alert = controller.removeItem(itemId);

    if (alert == null && controller.getItemById(itemId) != null) {
        System.out.println("ERROR: could not remove item.");
        return;
    }

    System.out.println("Item removed.");

    if (alert != null) {
        System.out.println("\n⚠ LOW STOCK ALERT:");
        System.out.println(alert);
    }
}

    public int selectItemType() {
        List<ItemType> itemTypes = controller.getAllItemTypes();
        if (itemTypes.isEmpty()) {
            System.out.println("No item types exist.");
            return -1;
        }

        System.out.println("\n====== AVAILABLE ITEM TYPES ======");
        for (ItemType type : itemTypes) {
            System.out.printf("ID %-3d | %-22s | Category: %s%n", 
                  type.getId(), 
                  type.getName(), 
                  type.getCategory().getFullPath());
        }
        System.out.println("----------------------------------");
        System.out.println("  -1. Done selecting");
        System.out.println("==================================");
        System.out.print("Choose item type (-1 to finish): ");

        int id = scanner.nextInt();
        scanner.nextLine();

        if (id == -1) {
            return -1;
        }

        ItemType selected = controller.getItemTypeById(id);
        if (selected == null) {
            System.out.println("ERROR: item type not found");
            return -1;
        }

        System.out.println("Selected: " + selected.getName());
        return id;
    }

    // =========================================================
// DISCOUNT MENU
// =========================================================
   private void discountMenu() {
    boolean back = false;

    while (!back) {
        System.out.println("\n==========================================");
        System.out.println("            DISCOUNT MENU");
        System.out.println("==========================================");
        System.out.println("  1. Add item discount");
        System.out.println("  2. Add category discount");
        System.out.println("  3. Show active discounts for item type");
        System.out.println("  4. Show all discounts");
        System.out.println("  5. Show final price for item type");
        System.out.println("  0. Back");
        System.out.println("==========================================");

        int choice = readInt("Choose an option: ");

        switch (choice) {
            case 1:
                addItemDiscount();
                break;
            case 2:
                addCategoryDiscount();
                break;
            case 3:
                showActiveDiscountsForItem();
                break;
            case 4:
                showAllDiscounts();
                break;
            case 5:
                showFinalPrice();
                break;
            case 0:
                back = true;
                break;
            default:
                System.out.println("  [!] Invalid option. Try again.");
        }
    }
}
    private void addItemDiscount() {
        System.out.println("\n--- Add Item Discount ---");
        double percentage = readDouble("Enter discount percentage: ");
        LocalDate startDate = readDate("Enter start date (yyyy-mm-dd): ");
        LocalDate endDate = readDate("Enter end date (yyyy-mm-dd): ");

        List<Integer> itemIds = new ArrayList<>();
        System.out.println("Select item types (enter 0 when done):");

        while (true) {
            int id = selectItemType();
            if (id == -1) {
                break;
            }
            itemIds.add(id);
            System.out.println("  [+] Added. Select another or 0 to finish.");
        }

        if (itemIds.isEmpty()) {
            System.out.println("  [!] No items selected. Cancelled.");
            return;
        }

        String result = controller.addItemDiscount(percentage, startDate, endDate, itemIds);
        System.out.println(">> " + result);
    }

    private void addCategoryDiscount() {
        System.out.println("\n--- Add Category Discount ---");
        double percentage = readDouble("Enter discount percentage: ");
        LocalDate startDate = readDate("Enter start date (yyyy-mm-dd): ");
        LocalDate endDate = readDate("Enter end date (yyyy-mm-dd): ");

        List<Integer> categoryIds = new ArrayList<>();
        System.out.println("Select categories (enter 0 when done):");

        while (true) {
            int id = selectCategory();
            if (id == -1) {
                break;
            }
            categoryIds.add(id);
            System.out.println("  [+] Added. Select another or 0 to finish.");
        }

        if (categoryIds.isEmpty()) {
            System.out.println("  [!] No categories selected. Cancelled.");
            return;
        }

        String result = controller.addCategoryDiscount(percentage, startDate, endDate, categoryIds);
        System.out.println(">> " + result);
    }

    private void showActiveDiscountsForItem() {
        System.out.println("\n--- Active Discounts for Item Type ---");
        int itemTypeId = selectItemType();
        if (itemTypeId == -1) {
            return;
        }

        List<Discount> discounts = controller.getActiveDiscountsForItem(itemTypeId);

        if (discounts.isEmpty()) {
            System.out.println("  No active discounts found.");
            return;
        }

        System.out.printf("\n  %-6s %-12s %-14s %-14s%n", "ID", "Discount%", "Start", "End");
        System.out.println("  --------------------------------------------------");
        for (Discount discount : discounts) {
            System.out.printf("  %-6d %-12.2f %-14s %-14s%n",
                    discount.getId(),
                    discount.getPercentage(),
                    discount.getStartDate(),
                    discount.getEndDate());
        }
    }

    private void showAllDiscounts() {
        System.out.println("\n--- All Discounts ---");
        List<Discount> discounts = controller.getAllDiscounts();

        if (discounts.isEmpty()) {
            System.out.println("  No discounts found.");
            return;
        }

        System.out.printf("\n  %-6s %-12s %-14s %-14s%n", "ID", "Discount%", "Start", "End");
        System.out.println("  --------------------------------------------------");
        for (Discount discount : discounts) {
            System.out.printf("  %-6d %-12.2f %-14s %-14s%n",
                    discount.getId(),
                    discount.getPercentage(),
                    discount.getStartDate(),
                    discount.getEndDate());
        }
    }

    private void showFinalPrice() {
        System.out.println("\n--- Final Price for Item Type ---");
        int itemTypeId = selectItemType();
        if (itemTypeId == -1) {
            return;
        }

        double finalPrice = controller.getFinalPrice(itemTypeId);

        if (finalPrice == -1) {
            System.out.println("  [!] ItemType not found.");
        } else {
            System.out.printf("  Final price: %.2f%n", finalPrice);
        }
    }
    // =========================================================
    // REPORT MENU
    // =========================================================
    private void reportMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n------ Report Menu ------");
            System.out.println("1. Create category inventory report");
            System.out.println("2. Show inventory by categories");
            System.out.println("3. Create defective item report");
            System.out.println("4. Create purchasing report");
            System.out.println("0. Back");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    createCategoryInventoryReport();
                    break;
                case 2:
                    showInventoryByCategories();
                    break;
                case 3:
                    createDefectiveItemReport();
                    break;
                case 4:
                    createPurchasingReport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void createCategoryInventoryReport() {
        List<Integer> categoryIds = readIntegerList("Enter category IDs separated by commas: ");
        CategoryInventoryReport report = controller.createCategoryInventoryReport(categoryIds);
        System.out.println(report);
    }

    private void showInventoryByCategories() {
    List<Integer> categoryIds = new ArrayList<>();

    System.out.println("Select categories (0 to finish):");
    while (true) {
        int id = selectCategory();
        if (id == -1) break;
        categoryIds.add(id);
        System.out.println("  [+] Added. Select another or 0 to finish.");
    }

    if (categoryIds.isEmpty()) {
        System.out.println("No categories selected.");
        return;
    }

    Map<Category, List<ItemType>> inventoryMap = controller.getInventoryByCategories(categoryIds);

    if (inventoryMap.isEmpty()) {
        System.out.println("No inventory found.");
        return;
    }

    for (Map.Entry<Category, List<ItemType>> entry : inventoryMap.entrySet()) {
        System.out.println("\nCategory: " + entry.getKey().getFullPath());
        List<ItemType> itemTypes = entry.getValue();

        if (itemTypes.isEmpty()) {
            System.out.println("  No item types in this category.");
        } else {
            for (ItemType itemType : itemTypes) {
                System.out.println("  - " + itemType);
            }
        }
    }
}

    private void createDefectiveItemReport() {
        DefectiveItemReport report = controller.createDefectiveItemReport();
        System.out.println(report);
    }

    private void createPurchasingReport() {
        PurchasingReport report = controller.createPurchasingReport();
        System.out.println(report);
    }

    // =========================================================
    // ALERT MENU
    // =========================================================
    private void alertMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n------ Alert Menu ------");
            System.out.println("1. Show all alerts");
            System.out.println("2. Show alert for item type");
            System.out.println("0. Back");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    showAllAlerts();
                    break;
                case 2:
                    showAlertForItemType();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void showAllAlerts() {
        List<Alert> alerts = controller.getAllAlerts();

        if (alerts.isEmpty()) {
            System.out.println("No alerts found.");
            return;
        }

        for (Alert alert : alerts) {
            System.out.println(alert);
        }
    }

    private void showAlertForItemType() {
        int itemTypeId = readInt("Enter item type ID: ");
        Alert alert = controller.getAlertForItemType(itemTypeId);

        if (alert == null) {
            System.out.println("No alert for this item type.");
            return;
        }

        System.out.println(alert);
    }

    // =========================================================
    // INPUT HELPERS
    // =========================================================
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Please try again.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("true")) {
                return true;
            }
            if (input.equals("false")) {
                return false;
            }

            System.out.println("Invalid boolean. Enter true or false.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-mm-dd.");
            }
        }
    }

    private LocalDate readOptionalDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-mm-dd.");
            }
        }
    }

    private List<Integer> readIntegerList(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return new ArrayList<>();
            }

            try {
                String[] parts = input.split(",");
                List<Integer> numbers = new ArrayList<>();

                for (String part : parts) {
                    numbers.add(Integer.parseInt(part.trim()));
                }

                return numbers;
            } catch (NumberFormatException e) {
                System.out.println("Invalid list. Use comma-separated integers.");
            }
        }
    }
}
