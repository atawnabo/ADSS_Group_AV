package adss.inventory.mock;

import adss.inventory.domain.ItemType;

public class SupplierMock {

    private static int orderIdCounter = 1;

    public static int createOrder(ItemType itemType, int quantity) {
        System.out.println("[SupplierMock] Order #" + orderIdCounter +
                ": " + quantity + " units of " + itemType.getName());
        return orderIdCounter++;
    }
}
