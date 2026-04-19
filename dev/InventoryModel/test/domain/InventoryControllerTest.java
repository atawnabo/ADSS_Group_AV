package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



 * Unit tests for the Inventory domain layer.
 * Tests cover: Category, ItemType, Item, Discount, Alert logic.
 * Each test is independent — a fresh InventoryController is created in @BeforeEach.
 */
public class InventoryControllerTest {

    private InventoryController controller;

    // ==================== SETUP ====================

    @BeforeEach
    void setUp() {
        controller = new InventoryController();
    }

    // ==================== TEST 1: Add root category ====================

    /**
     * Test that a root category is added successfully and retrievable by ID.
     */
    @Test
    void testAddRootCategory() {
        String result = controller.addCategory("Dairy");
        assertEquals("OK", result);

        Category cat = controller.getCategoryById(1);
        assertNotNull(cat);
        assertEquals("Dairy", cat.getName());
        assertNull(cat.getParent());
    }

    // ==================== TEST 2: Add sub-category ====================

    /**
     * Test that a sub-category is added under a parent and has correct path.
     */
    @Test
    void testAddSubCategory() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2

        Category milk = controller.getCategoryById(2);
        assertNotNull(milk);
        assertEquals("Milk", milk.getName());
        assertEquals("Dairy > Milk", milk.getFullPath());
        assertNotNull(milk.getParent());
        assertEquals("Dairy", milk.getParent().getName());
    }

    // ==================== TEST 3: Add sub-category to non-existent parent ====================

    /**
     * Test that adding a sub-category to a non-existent parent returns an error.
     */
    @Test
    void testAddSubCategoryInvalidParent() {
        String result = controller.addCategory("Milk", 999);
        assertTrue(result.startsWith("ERROR"));
    }

    // ==================== TEST 4: isLeaf detection ====================

    /**
     * Test that a category with children is not a leaf,
     * and a category without children is a leaf.
     */
    @Test
    void testIsLeaf() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2

        Category dairy = controller.getCategoryById(1);
        Category milk  = controller.getCategoryById(2);

        assertFalse(dairy.isLeaf(), "Dairy should NOT be a leaf");
        assertTrue(milk.isLeaf(),   "Milk should BE a leaf");
    }

    // ==================== TEST 5: Add ItemType successfully ====================

    /**
     * Test that an ItemType is added successfully with correct fields.
     */
    @Test
    void testAddItemType() {
        controller.addCategory("Dairy");        // id 1
        controller.addCategory("Milk", 1);      // id 2

        int id = controller.addItemType("Milk 3% Tnuva", 1, 1, 20, 4, 6, 2, "Tnuva");
        assertTrue(id > 0, "addItemType should return a positive ID");

        ItemType type = controller.getItemTypeById(id);
        assertNotNull(type);
        assertEquals("Milk 3% Tnuva", type.getName());
        assertEquals(4, type.getCostPrice());
        assertEquals(6, type.getSellingPrice());
        assertEquals(20, type.getMinQuantity());
        assertEquals("Tnuva", type.getManufacturer());
    }

    // ==================== TEST 6: Add ItemType with non-existent category ====================

    /**
     * Test that adding an ItemType with a non-existent category returns -1.
     */
    @Test
    void testAddItemTypeInvalidCategory() {
        int id = controller.addItemType("Milk", 1, 1, 10, 4, 6, 999, "Tnuva");
        assertEquals(-1, id, "Should return -1 for non-existent category");
    }

    // ==================== TEST 7: needsRestock logic ====================

    /**
     * Test that needsRestock returns true when total quantity is below minimum.
     */
    @Test
    void testNeedsRestock() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");

        ItemType type = controller.getItemTypeById(typeId);

        // no items added → total = 0 < min 20 → needs restock
        assertTrue(type.needsRestock(), "Should need restock when quantity is 0");

        // add items to go above threshold
        controller.addItems(typeId, 25, LocalDate.of(2027, 1, 1), false);
        assertFalse(type.needsRestock(), "Should NOT need restock when quantity >= min");
    }

    // ==================== TEST 8: Move item from warehouse to shelf ====================

    /**
     * Test that moving an item from warehouse to shelf updates quantities correctly.
     */
    @Test
    void testMoveItemToShelf() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");

        // add one item to warehouse
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, true);

        ItemType type = controller.getItemTypeById(typeId);
        assertEquals(1, type.getWarehouseQuantity());
        assertEquals(0, type.getShelfQuantity());

        // move to shelf
        boolean moved = controller.moveItemToShelf(itemId);
        assertTrue(moved);
        assertEquals(0, type.getWarehouseQuantity());
        assertEquals(1, type.getShelfQuantity());
    }

    // ==================== TEST 9: Mark item as damaged ====================

    /**
     * Test that marking an item as damaged sets the damaged flag correctly.
     */
    @Test
    void testMarkItemAsDamaged() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, false);

        Item item = controller.getItemById(itemId);
        assertFalse(item.isDamaged(), "Item should NOT be damaged initially");

        boolean result = controller.markItemAsDamaged(itemId);
        assertTrue(result);
        assertTrue(item.isDamaged(), "Item SHOULD be damaged after marking");
    }

    // ==================== TEST 10: Item expiration detection ====================

    /**
     * Test that isExpired returns true for past date and false for future date.
     */
    @Test
    void testItemExpiration() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");

        // expired item
        int expiredId = controller.addItem(typeId, 0, 0, LocalDate.of(2020, 1, 1), false, false);
        // valid item
        int validId   = controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), false, false);

        assertTrue(controller.getItemById(expiredId).isExpired(),  "Past date should be expired");
        assertFalse(controller.getItemById(validId).isExpired(),   "Future date should NOT be expired");
    }

    // ==================== TEST 11: CategoryDiscount applies to item ====================

    /**
     * Test that a CategoryDiscount applies to items in the target category.
     */
    @Test
    void testCategoryDiscountApplies() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");

        // add category discount: 10% off Dairy
        String result = controller.addCategoryDiscount(
                10.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                List.of(1) // Dairy
        );
        assertEquals("OK", result);

        List<Discount> discounts = controller.getActiveDiscountsForItem(typeId);
        assertFalse(discounts.isEmpty(), "Should have at least one active discount");
        assertEquals(10.0, discounts.get(0).getPercentage());
    }

    // ==================== TEST 12: Best discount wins ====================

    /**
     * Test that when two discounts apply, the highest percentage is used for final price.
     */
    @Test
    void testBestDiscountWins() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 10, 2, "Tnuva");
        // sell price = 10

        // add 10% category discount
        controller.addCategoryDiscount(10.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), List.of(1));

        // add 20% item discount
        controller.addItemDiscount(20.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), List.of(typeId));

        // best discount should be 20% → final price = 10 * 0.80 = 8.0
        double finalPrice = controller.getFinalPrice(typeId);
        assertEquals(8.0, finalPrice, 0.001, "Best discount (20%) should be applied");
    }

    // ==================== TEST 13: Expired discount not applied ====================

    /**
     * Test that an expired discount is not applied to the final price.
     */
    @Test
    void testExpiredDiscountNotApplied() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 10, 2, "Tnuva");

        // add expired discount (ended yesterday)
        controller.addItemDiscount(50.0,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1), // expired
                List.of(typeId));

        // no active discounts → final price = original sell price = 10
        double finalPrice = controller.getFinalPrice(typeId);
        assertEquals(10.0, finalPrice, 0.001, "Expired discount should NOT be applied");
    }

    // ==================== TEST 14: Remove item triggers alert ====================

    /**
     * Test that removing an item that causes stock to drop below minimum
     * returns a non-null alert.
     */
    @Test
    void testRemoveItemTriggersAlert() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        // min quantity = 5, we add exactly 5 items
        int typeId = controller.addItemType("Milk 3%", 1, 1, 5, 4, 6, 2, "Tnuva");
        int itemId = controller.addItem(typeId, 0, 0, LocalDate.of(2027, 1, 1), false, false);
        controller.addItems(typeId, 4, LocalDate.of(2027, 1, 1), false);
        // total = 5 = min → still ok (needsRestock returns total <= min)

        // remove one → total = 4 < 5 → should trigger alert
        Alert alert = controller.removeItem(itemId);
        assertNotNull(alert, "Alert should be triggered when stock drops below minimum");
        assertEquals("Milk 3%", alert.getItemType().getName());
    }

    // ==================== TEST 15: Remove all defective items ====================

    /**
     * Test that removeAllDefectiveItems removes only damaged/expired items
     * and leaves healthy items intact.
     */
    @Test
    void testRemoveAllDefectiveItems() {
        controller.addCategory("Dairy");       // id 1
        controller.addCategory("Milk", 1);     // id 2
        int typeId = controller.addItemType("Milk 3%", 1, 1, 20, 4, 6, 2, "Tnuva");

        // add 2 healthy items
        controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), false, false);
        controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), false, false);

        // add 1 damaged item
        int damagedId = controller.addItem(typeId, 0, 0, LocalDate.of(2030, 1, 1), true, false);

        // add 1 expired item
        controller.addItem(typeId, 0, 0, LocalDate.of(2020, 1, 1), false, false);

        // before: 4 items total
        assertEquals(4, controller.getAllItems().size());

        controller.removeAllDefectiveItems();

        // after: only 2 healthy items remain
        List<Item> remaining = controller.getAllItems();
        assertEquals(2, remaining.size(), "Only healthy items should remain");
        assertTrue(remaining.stream().noneMatch(Item::isDamaged), "No damaged items should remain");
        assertTrue(remaining.stream().noneMatch(Item::isExpired), "No expired items should remain");
    }
}