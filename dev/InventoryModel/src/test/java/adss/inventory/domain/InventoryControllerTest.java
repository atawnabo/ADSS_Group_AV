package adss.inventory.domain;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import adss.inventory.repository.DatabaseManager;

public class InventoryControllerTest {

    private InventoryController controller;

    @BeforeEach
    void setUp() {
        // Use a separate test database so the real inventory.db is never touched.
        DatabaseManager.setDatabasePath("inventory_test.db");
        controller = new InventoryController();
        controller.clearSystemData();   // clean DB + memory before each test
    }

    @AfterAll
    static void restoreDatabase() {
        // Point back to the real database and remove the test file.
        DatabaseManager.setDatabasePath("inventory.db");
        new java.io.File("inventory_test.db").delete();
    }

    // =========================================================
    // ORIGINAL TESTS (Assignment 1)
    // =========================================================

    // TEST 1: Add root category
    @Test
    void testAddRootCategory() {
        String result = controller.addCategory("Dairy");
        assertEquals("OK", result);
        Category cat = controller.getCategoryById(1);
        assertNotNull(cat);
        assertEquals("Dairy", cat.getName());
        assertNull(cat.getParent());
    }

    // TEST 2: Add sub-category
    @Test
    void testAddSubCategory() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        Category milk = controller.getCategoryById(2);
        assertNotNull(milk);
        assertEquals("Milk", milk.getName());
        assertEquals("Dairy > Milk", milk.getFullPath());
        assertEquals("Dairy", milk.getParent().getName());
    }

    // TEST 3: Add sub-category to non-existent parent
    @Test
    void testAddSubCategoryInvalidParent() {
        String result = controller.addCategory("Milk", 999);
        assertTrue(result.startsWith("ERROR"));
    }

    // TEST 4: isLeaf detection
    @Test
    void testIsLeaf() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        Category dairy = controller.getCategoryById(1);
        Category milk  = controller.getCategoryById(2);
        assertFalse(dairy.isLeaf());
        assertTrue(milk.isLeaf());
    }

    // TEST 5: Add ItemType successfully
    @Test
    void testAddItemType() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int id = controller.addItemType("Milk 3% Tnuva", 1, 1, 20, 4, 6, 2, "Tnuva");
        assertTrue(id > 0);
        ItemType type = controller.getItemTypeById(id);
        assertNotNull(type);
        assertEquals("Milk 3% Tnuva", type.getName());
        assertEquals(4, type.getCostPrice());
        assertEquals(6, type.getSellingPrice());
        assertEquals(20, type.getMinQuantity());
    }

    // TEST 6: Add ItemType with invalid category
    @Test
    void testAddItemTypeInvalidCategory() {
        int id = controller.addItemType("Milk", 1, 1, 10, 4, 6, 999, "Tnuva");
        assertEquals(-1, id);
    }

    // TEST 7: needsRestock logic
    @Test
    void testNeedsRestock() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        ItemType type = controller.getItemTypeById(typeId);
        assertTrue(type.needsRestock());
        controller.addItems(typeId, 25, LocalDate.of(2027, 1, 1), false);
        assertFalse(type.needsRestock());
    }

    // TEST 8: Move item from warehouse to shelf
    @Test
    void testMoveItemToShelf() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, true);
        ItemType type = controller.getItemTypeById(typeId);
        assertEquals(1, type.getWarehouseQuantity());
        assertEquals(0, type.getShelfQuantity());
        boolean moved = controller.moveItemToShelf(itemId);
        assertTrue(moved);
        assertEquals(0, type.getWarehouseQuantity());
        assertEquals(1, type.getShelfQuantity());
    }

    // TEST 9: Mark item as damaged
    @Test
    void testMarkItemAsDamaged() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, false);
        Item item = controller.getItemById(itemId);
        assertFalse(item.isDamaged());
        assertTrue(controller.markItemAsDamaged(itemId));
        assertTrue(item.isDamaged());
    }

    // TEST 10: Item expiration detection
    @Test
    void testItemExpiration() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        int expiredId = controller.addItem(typeId, 0, 0, LocalDate.of(2020, 1, 1), false, false);
        int validId   = controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), false, false);
        assertTrue(controller.getItemById(expiredId).isExpired());
        assertFalse(controller.getItemById(validId).isExpired());
    }

    // TEST 11: CategoryDiscount applies to item
    @Test
    void testCategoryDiscountApplies() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        String result = controller.addCategoryDiscount(
                10.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                List.of(1));
        assertEquals("OK", result);
        List<Discount> discounts = controller.getActiveDiscountsForItem(typeId);
        assertFalse(discounts.isEmpty());
        assertEquals(10.0, discounts.get(0).getPercentage());
    }

    // TEST 12: Best discount wins
    @Test
    void testBestDiscountWins() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 10, 2, "Tnuva");
        controller.addCategoryDiscount(10.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), List.of(1));
        controller.addItemDiscount(20.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), List.of(typeId));
        double finalPrice = controller.getFinalPrice(typeId);
        assertEquals(8.0, finalPrice, 0.001);
    }

    // TEST 13: Expired discount not applied
    @Test
    void testExpiredDiscountNotApplied() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 10, 2, "Tnuva");
        controller.addItemDiscount(50.0,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                List.of(typeId));
        double finalPrice = controller.getFinalPrice(typeId);
        assertEquals(10.0, finalPrice, 0.001);
    }

    // TEST 14: Remove item triggers alert
    @Test
    void testRemoveItemTriggersAlert() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 5, 4, 6, 2, "Tnuva");
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, false);
        controller.addItems(typeId, 4, LocalDate.of(2027, 1, 1), false);
        Alert alert = controller.removeItem(itemId);
        assertNotNull(alert);
        assertEquals("Milk 3%", alert.getItemType().getName());
    }

    // TEST 15: Remove all defective items
    @Test
    void testRemoveAllDefectiveItems() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), false, false);
        controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), false, false);
        controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), true,  false);
        controller.addItem(typeId, 0, 0, LocalDate.of(2020, 1, 1), false, false);
        assertEquals(4, controller.getAllItems().size());
        controller.removeAllDefectiveItems();
        List<Item> remaining = controller.getAllItems();
        assertEquals(2, remaining.size());
        assertTrue(remaining.stream().noneMatch(Item::isDamaged));
        assertTrue(remaining.stream().noneMatch(Item::isExpired));
    }

    // =========================================================
    // NEW TESTS (Assignment 2) — integration features
    // =========================================================

    // NEW TEST 16: The new Warehouse entity stores items and counts them.
    @Test
    void testWarehouseAddRemoveCount() {
        Category cat = new Category(1, "Dairy");
        Location loc = new Location(1, 1);
        ItemType type = new ItemType(1, "Milk 3%", loc, 0, 0, 5, 4, 6, cat, "Tnuva");

        Item a = new Item(type, 1, 0, 0, LocalDate.of(2027, 1, 1), false, true);
        Item b = new Item(type, 2, 0, 0, LocalDate.of(2027, 1, 1), false, true);

        Warehouse warehouse = new Warehouse(1, "Test Warehouse", 100);
        warehouse.addItem(a);
        warehouse.addItem(b);
        assertEquals(2, warehouse.countOf(type));

        warehouse.removeItem(a);
        assertEquals(1, warehouse.countOf(type));
    }

    // NEW TEST 17: Removing below the minimum auto-places a supplier order
    //              (via SupplierMock) and returns a low-stock alert.
    @Test
    void testRemovalBelowMinimumPlacesOrder() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 5, 4, 6, 2, "Tnuva");
        controller.addItems(typeId, 5, LocalDate.of(2027, 1, 1), false);

        ItemType type = controller.getItemTypeById(typeId);
        assertFalse(type.needsRestock());   // exactly at minimum -> ok

        int itemId = controller.getItemsByType(typeId).get(0).getId();
        Alert alert = controller.removeItem(itemId);

        assertNotNull(alert);                                    // shortage alert generated
        assertTrue(alert.getDescription().contains("Ordered"));  // an order was placed
        assertFalse(type.needsRestock());                        // incoming now covers the gap
    }

    // NEW TEST 18: While an order is already on the way (incoming stock),
    //              a second removal must NOT place another order.
    @Test
    void testNoDuplicateOrderWhileIncoming() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 5, 4, 6, 2, "Tnuva");
        controller.addItems(typeId, 5, LocalDate.of(2027, 1, 1), false);

        int first = controller.getItemsByType(typeId).get(0).getId();
        assertNotNull(controller.removeItem(first));   // first removal -> order placed

        int second = controller.getItemsByType(typeId).get(0).getId();
        assertNull(controller.removeItem(second));     // no new order while incoming covers it
    }

    // NEW TEST 19: Moving an item to the warehouse updates both quantities
    //              and the item's location flag consistently.
    @Test
    void testMoveItemToWarehouseUpdatesQuantities() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, false);

        ItemType type = controller.getItemTypeById(typeId);
        assertEquals(1, type.getShelfQuantity());
        assertEquals(0, type.getWarehouseQuantity());

        assertTrue(controller.moveItemToWarehouse(itemId));
        assertEquals(0, type.getShelfQuantity());
        assertEquals(1, type.getWarehouseQuantity());
        assertTrue(controller.getItemById(itemId).isInWarehouse());
    }

    // NEW TEST 20 (INTEGRATION): data saved by one controller is persisted
    //              in SQLite and loaded back correctly by a fresh controller.
    @Test
    void testDataPersistsToDatabase() {
        controller.addCategory("Dairy");
        controller.addCategory("Milk", 1);
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, true);

        InventoryController reloaded = new InventoryController();
        reloaded.loadDataFromDatabase();

        ItemType type = reloaded.getItemTypeById(typeId);
        assertNotNull(type);
        assertEquals("Milk 3%", type.getName());
        assertEquals(1, type.getWarehouseQuantity());
        assertEquals(1, reloaded.getItemsByType(typeId).size());
    }
}