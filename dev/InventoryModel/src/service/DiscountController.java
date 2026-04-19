package service;

import java.time.LocalDate;
import java.util.List;

import domain.Discount;
import domain.InventoryController;

public class DiscountController {
    private final InventoryController inventoryController;

    public DiscountController(InventoryController inventoryController) {
        if (inventoryController == null) {
            throw new IllegalArgumentException("InventoryController cannot be null");
        }
        this.inventoryController = inventoryController;
    }

    public String addItemDiscount(double percentage,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  List<Integer> itemIds) {
        return inventoryController.addItemDiscount(
                percentage,
                startDate,
                endDate,
                itemIds
        );
    }

    public String addCategoryDiscount(double percentage,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      List<Integer> categoryIds) {
        return inventoryController.addCategoryDiscount(
                percentage,
                startDate,
                endDate,
                categoryIds
        );
    }

    public List<Discount> getActiveDiscountsForItem(int itemTypeId) {
        return inventoryController.getActiveDiscountsForItem(itemTypeId);
    }

    public List<Discount> getAllDiscounts() {
        return inventoryController.getAllDiscounts();
    }

    public double getFinalPrice(int itemTypeId) {
        return inventoryController.getFinalPrice(itemTypeId);
    }


}