package adss.inventory.init;

import java.time.LocalDate;
import java.util.List;

import adss.inventory.presentation.PresentationController;

public class DataLoader {

    private final PresentationController pc;

    public DataLoader(PresentationController pc) {
        this.pc = pc;
    }

    public void load() {

        // ==================== CATEGORIES ====================
        // Dairy
        pc.addCategory("Dairy");                    // id 1
        pc.addCategory("Milk", 1);                  // id 2
        pc.addCategory("3%", 2);                    // id 3
        pc.addCategory("1%", 2);                    // id 4
        pc.addCategory("5%", 2);                    // id 5
        pc.addCategory("Cheese", 1);                // id 6
        pc.addCategory("Yellow", 6);                // id 7
        pc.addCategory("White", 6);                 // id 8
        pc.addCategory("Butter", 1);                // id 9
        pc.addCategory("250g", 9);                  // id 10
        pc.addCategory("500g", 9);                  // id 11

        // Hygiene
        pc.addCategory("Hygiene");                  // id 12
        pc.addCategory("Shampoo", 12);              // id 13
        pc.addCategory("250ml", 13);                // id 14
        pc.addCategory("500ml", 13);                // id 15
        pc.addCategory("Soap", 12);                 // id 16
        pc.addCategory("Bar", 16);                  // id 17
        pc.addCategory("Liquid", 16);               // id 18

        // Beverages
        pc.addCategory("Beverages");                // id 19
        pc.addCategory("Juice", 19);                // id 20
        pc.addCategory("Orange", 20);               // id 21
        pc.addCategory("Apple", 20);                // id 22
        pc.addCategory("Water", 19);                // id 23
        pc.addCategory("500ml", 23);                // id 24
        pc.addCategory("1.5L", 23);                 // id 25

        // Bakery
        pc.addCategory("Bakery");                   // id 26
        pc.addCategory("Bread", 26);                // id 27
        pc.addCategory("White", 27);                // id 28
        pc.addCategory("Whole Wheat", 27);          // id 29

        // Snacks
        pc.addCategory("Snacks");                   // id 30
        pc.addCategory("Chips", 30);                // id 31
        pc.addCategory("Regular", 31);              // id 32
        pc.addCategory("Baked", 31);                // id 33
        pc.addCategory("Chocolate", 30);            // id 34
        pc.addCategory("Dark", 34);                 // id 35
        pc.addCategory("Milk Choc", 34);            // id 36

        // ==================== ITEM TYPES ====================
        // signature: name, shelfNum, aisleNum, minQuantity, costPrice, sellingPrice, categoryId, manufacturer
        // Dairy
        pc.addItemType("Milk 3% Tnuva 1L", 1, 1, 20, 4, 6, 3, "Tnuva");
        pc.addItemType("Milk 1% Tnuva 1L", 1, 2, 20, 4, 6, 4, "Tnuva");
        pc.addItemType("Milk 5% Tnuva 1L", 1, 3, 15, 4, 7, 5, "Tnuva");
        pc.addItemType("Yellow Cheese 200g", 2, 1, 10, 8, 12, 7, "Tnuva");
        pc.addItemType("White Cheese 200g", 2, 2, 10, 6, 9, 8, "Tnuva");
        pc.addItemType("Butter 250g", 2, 3, 8, 6, 9, 10, "Tnuva");
        pc.addItemType("Butter 500g", 2, 4, 5, 11, 16, 11, "Tnuva");

        // Hygiene
        pc.addItemType("Pinuk Shampoo 250ml", 3, 1, 8, 8, 14, 14, "Pinuk");
        pc.addItemType("Pinuk Shampoo 500ml", 3, 2, 5, 14, 22, 15, "Pinuk");
        pc.addItemType("Dove Soap Bar", 3, 3, 10, 3, 6, 17, "Dove");
        pc.addItemType("Dove Soap Liquid", 3, 4, 8, 7, 12, 18, "Dove");

        // Beverages
        pc.addItemType("Prigat Orange Juice", 4, 1, 15, 4, 7, 21, "Prigat");
        pc.addItemType("Prigat Apple Juice", 4, 2, 15, 4, 7, 22, "Prigat");
        pc.addItemType("Water 500ml", 4, 3, 30, 1, 3, 24, "Neviot");
        pc.addItemType("Water 1.5L", 4, 4, 20, 2, 5, 25, "Neviot");

        // Bakery
        pc.addItemType("White Bread", 5, 1, 10, 3, 6, 28, "Berman");
        pc.addItemType("Whole Wheat Bread", 5, 2, 10, 4, 7, 29, "Berman");

        // Snacks
        pc.addItemType("Regular Chips 100g", 6, 1, 20, 3, 6, 32, "Elite");
        pc.addItemType("Baked Chips 100g", 6, 2, 15, 4, 7, 33, "Elite");
        pc.addItemType("Dark Chocolate 100g", 6, 3, 10, 5, 9, 35, "Elite");
        pc.addItemType("Milk Choc 100g", 6, 4, 12, 4, 8, 36, "Elite");

        // ==================== ITEMS (physical units) ====================
// addItems(itemTypeId, amount, expirationDate, inWarehouse)
// addItem (itemTypeId, sellDiscount, buyDiscount, expirationDate, damaged, inWarehouse)
        LocalDate fresh = LocalDate.of(2027, 3, 1);   // valid (not expired)
        LocalDate past = LocalDate.of(2026, 4, 25);  // expired (before today)

// --- WELL ABOVE minimum: healthy, removing one will NOT trigger an order ---
// Water 500ml (min 30) -> 35
        pc.addItems(14, 20, fresh, false);   // shelf
        pc.addItems(14, 15, fresh, true);    // warehouse
// Milk 3% (min 20) -> 24
        pc.addItems(1, 14, fresh, false);
        pc.addItems(1, 10, fresh, true);

// --- EXACTLY at minimum: removing ONE item drops below -> auto order ---
// Yellow Cheese (min 10) -> 10
        pc.addItems(4, 6, fresh, false);
        pc.addItems(4, 4, fresh, true);

// --- ALREADY under minimum: shows "restock = YES" in the list ---
// Milk 1% (min 20) -> 5
        pc.addItems(2, 3, fresh, false);
        pc.addItems(2, 2, fresh, true);
// Pinuk Shampoo 250ml (min 8) -> 3
        pc.addItems(8, 2, fresh, false);
        pc.addItems(8, 1, fresh, true);

// --- AT minimum WITH a damaged unit: "remove all defective" drops below -> order ---
// Butter 250g (min 8) -> 7 good + 1 damaged = 8
        pc.addItems(6, 4, fresh, false);
        pc.addItems(6, 3, fresh, true);
        pc.addItem(6, 0, 0, fresh, true, false);   // 1 damaged, on shelf

// --- EXPIRED units (show "Expired: YES") ---
// Whole Wheat Bread (min 10) -> 2 expired
        pc.addItem(17, 0, 0, past, false, false);
        pc.addItem(17, 0, 0, past, false, false);

// --- EMPTY on purpose (0 stock, far under minimum): ---
// Milk 5%, White Cheese, Butter 500g, Shampoo 500ml, Dove Soap, juices,
// Water 1.5L, White Bread, chips, chocolates -> left at 0
        // ==================== PROMOTIONS ====================
        pc.addCategoryDiscount(15.0,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30),
                List.of(1));   // 15% off all Dairy

        pc.addCategoryDiscount(10.0,
                LocalDate.of(2026, 4, 15),
                LocalDate.of(2026, 4, 25),
                List.of(12));  // 10% off all Hygiene

        pc.addItemDiscount(20.0,
                LocalDate.of(2026, 4, 18),
                LocalDate.of(2026, 4, 20),
                List.of(1, 2, 3)); // 20% off Milk items
    }
}
