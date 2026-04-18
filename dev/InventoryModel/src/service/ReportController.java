package service;

import domain.Category;
import domain.CategoryInventoryReport;
import domain.DefectiveItemReport;
import domain.InventoryController;
import domain.ItemType;
import domain.PurchasingReport;

import java.util.List;
import java.util.Map;

public class ReportController {
    private final InventoryController inventoryController;

    public ReportController(InventoryController inventoryController) {
        if (inventoryController == null) {
            throw new IllegalArgumentException("InventoryController cannot be null");
        }
        this.inventoryController = inventoryController;
    }

    public CategoryInventoryReport createCategoryInventoryReport(List<Integer> categoryIds) {
        return inventoryController.createCategoryInventoryReport(categoryIds);
    }

    public Map<Category, List<ItemType>> getInventoryByCategories(List<Integer> categoryIds) {
        return inventoryController.getInventoryByCategories(categoryIds);
    }

    public DefectiveItemReport createDefectiveItemReport() {
        return inventoryController.createDefectiveItemReport();
    }

    public PurchasingReport createPurchasingReport() {
        return inventoryController.createPurchasingReport();
    }
}