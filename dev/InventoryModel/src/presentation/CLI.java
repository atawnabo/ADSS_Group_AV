package presentation;

import domain.Alert;
import domain.Category;
import domain.CategoryInventoryReport;
import domain.DefectiveItemReport;
import domain.Discount;
import domain.Item;
import domain.ItemType;
import domain.Location;
import domain.PurchasingReport;
import domain.SupplierDiscountHistory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CLI {
    private final PresentationController controller;
    private final Scanner scanner;

    public CLI() {
        this.controller = new PresentationController();
        this.scanner = new Scanner(System.in);
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

        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    categoryMenu();
                    break;
                case 2:
                    itemTypeMenu();
                    break;
                case 3:
                    itemMenu();
                    break;
                case 4:
                    discountMenu();
                    break;
                case 5:
                    reportMenu();
                    break;
                case 6:
                    alertMenu();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting system...");
                    break;
                default:
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
            System.out.println("\n------ Category Menu ------");
            System.out.println("1. Add root category");
            System.out.println("2. Add sub-category");
            System.out.println("3. Show all categories");
            System.out.println("4. Show category by ID");
            System.out.println("5. Show root categories");
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
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addRootCategory() {
        String name = readLine("Enter category name: ");
        String result = controller.addCategory(name);
        System.out.println(result);
    }

    private void addSubCategory() {
        String name = readLine("Enter sub-category name: ");
        int parentId = readInt("Enter parent category ID: ");
        String result = controller.addCategory(name, parentId);
        System.out.println(result);
    }

    private void showAllCategories() {
        List<Category> categories = controller.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        for (Category category : categories) {
            System.out.println(category);
        }
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
            System.out.println("No root categories found.");
            return;
        }

        for (Category category : categories) {
            System.out.println(category);
        }
    }

    // =========================================================
    // ITEM TYPE MENU
    // =========================================================

    private void itemTypeMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n------ ItemType Menu ------");
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
        String name = readLine("Enter item type name: ");
        int shelfNum = readInt("Enter store shelf number: ");
        int aisleNum = readInt("Enter store aisle number: ");
        int minQuantity = readInt("Enter minimum quantity: ");
        int costPrice = readInt("Enter cost price: ");
        int sellingPrice = readInt("Enter selling price: ");
        int categoryId = readInt("Enter category ID: ");
        String manufacturer = readLine("Enter manufacturer: ");

        int id = controller.addItemType(
                name,
                new Location(shelfNum, aisleNum),
                minQuantity,
                costPrice,
                sellingPrice,
                categoryId,
                manufacturer
        );

        if (id == -1) {
            System.out.println("Failed to add item type. Category not found.");
        } else {
            System.out.println("ItemType added successfully. New ID: " + id);
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
        System.out.println(success ? "Minimum quantity updated." : "ItemType not found.");
    }

    private void showItemTypesByCategory() {
        int categoryId = readInt("Enter category ID: ");
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
            System.out.println("1. Add item");
            System.out.println("2. Show all items");
            System.out.println("3. Show item by ID");
            System.out.println("4. Show items by item type");
            System.out.println("5. Move one item to shelf");
            System.out.println("6. Move multiple items to shelf");
            System.out.println("7. Move one item to warehouse");
            System.out.println("8. Mark item as damaged");
            System.out.println("9. Unmark item as damaged");
            System.out.println("10. Update item expiration date");
            System.out.println("11. Remove item");
            System.out.println("0. Back");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    showAllItems();
                    break;
                case 3:
                    showItemById();
                    break;
                case 4:
                    showItemsByType();
                    break;
                case 5:
                    moveItemToShelf();
                    break;
                case 6:
                    moveItemsToShelf();
                    break;
                case 7:
                    moveItemToWarehouse();
                    break;
                case 8:
                    markItemAsDamaged();
                    break;
                case 9:
                    unmarkItemAsDamaged();
                    break;
                case 10:
                    updateExpirationDate();
                    break;
                case 11:
                    removeItem();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addItem() {
        int itemTypeId = readInt("Enter item type ID: ");
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
            System.out.println("Failed to add item. ItemType not found.");
        } else {
            System.out.println("Item added successfully. New ID: " + itemId);
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
        boolean success = controller.removeItem(itemId);
        System.out.println(success ? "Item removed." : "Item not found.");
    }

    // =========================================================
    // DISCOUNT MENU
    // =========================================================

    private void discountMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n------ Discount Menu ------");
            System.out.println("1. Add item discount");
            System.out.println("2. Add category discount");
            System.out.println("3. Show active discounts for item type");
            System.out.println("4. Show all discounts");
            System.out.println("5. Show final price for item type");
            System.out.println("6. Add supplier discount history");
            System.out.println("7. Show supplier discount history");
            System.out.println("0. Back");

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
                case 6:
                    addSupplierDiscount();
                    break;
                case 7:
                    showSupplierDiscountHistory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addItemDiscount() {
        double percentage = readDouble("Enter discount percentage: ");
        LocalDate startDate = readDate("Enter start date (yyyy-mm-dd): ");
        LocalDate endDate = readDate("Enter end date (yyyy-mm-dd): ");
        List<Integer> itemIds = readIntegerList("Enter item type IDs separated by commas: ");

        String result = controller.addItemDiscount(percentage, startDate, endDate, itemIds);
        System.out.println(result);
    }

    private void addCategoryDiscount() {
        double percentage = readDouble("Enter discount percentage: ");
        LocalDate startDate = readDate("Enter start date (yyyy-mm-dd): ");
        LocalDate endDate = readDate("Enter end date (yyyy-mm-dd): ");
        List<Integer> categoryIds = readIntegerList("Enter category IDs separated by commas: ");

        String result = controller.addCategoryDiscount(percentage, startDate, endDate, categoryIds);
        System.out.println(result);
    }

    private void showActiveDiscountsForItem() {
        int itemTypeId = readInt("Enter item type ID: ");
        List<Discount> discounts = controller.getActiveDiscountsForItem(itemTypeId);

        if (discounts.isEmpty()) {
            System.out.println("No active discounts found.");
            return;
        }

        for (Discount discount : discounts) {
            System.out.println("Discount ID: " + discount.getId()
                    + ", Percentage: " + discount.getPercentage()
                    + ", Start: " + discount.getStartDate()
                    + ", End: " + discount.getEndDate());
        }
    }

    private void showAllDiscounts() {
        List<Discount> discounts = controller.getAllDiscounts();

        if (discounts.isEmpty()) {
            System.out.println("No discounts found.");
            return;
        }

        for (Discount discount : discounts) {
            System.out.println("Discount ID: " + discount.getId()
                    + ", Percentage: " + discount.getPercentage()
                    + ", Start: " + discount.getStartDate()
                    + ", End: " + discount.getEndDate());
        }
    }

    private void showFinalPrice() {
        int itemTypeId = readInt("Enter item type ID: ");
        double finalPrice = controller.getFinalPrice(itemTypeId);

        if (finalPrice == -1) {
            System.out.println("ItemType not found.");
        } else {
            System.out.println("Final price: " + finalPrice);
        }
    }

    private void addSupplierDiscount() {
        int itemTypeId = readInt("Enter item type ID: ");
        double percentage = readDouble("Enter supplier discount percentage: ");
        LocalDate date = readDate("Enter date (yyyy-mm-dd): ");
        String supplierName = readLine("Enter supplier name: ");

        String result = controller.addSupplierDiscount(itemTypeId, percentage, date, supplierName);
        System.out.println(result);
    }

    private void showSupplierDiscountHistory() {
        int itemTypeId = readInt("Enter item type ID: ");
        List<SupplierDiscountHistory> history = controller.getSupplierDiscountHistory(itemTypeId);

        if (history.isEmpty()) {
            System.out.println("No supplier discount history found.");
            return;
        }

        for (SupplierDiscountHistory record : history) {
            System.out.println("History ID: " + record.getId()
                    + ", Percentage: " + record.getDiscountPercentage()
                    + ", Date: " + record.getDate()
                    + ", Supplier: " + record.getSupplierName());
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
        List<Integer> categoryIds = readIntegerList("Enter category IDs separated by commas: ");
        Map<Category, List<ItemType>> inventoryMap = controller.getInventoryByCategories(categoryIds);

        if (inventoryMap.isEmpty()) {
            System.out.println("No inventory found.");
            return;
        }

        for (Map.Entry<Category, List<ItemType>> entry : inventoryMap.entrySet()) {
            System.out.println("\nCategory: " + entry.getKey());
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

            if (input.equals("true")) return true;
            if (input.equals("false")) return false;

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