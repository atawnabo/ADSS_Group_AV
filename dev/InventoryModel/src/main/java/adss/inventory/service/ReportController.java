package adss.inventory.service;

import java.util.List;
import java.util.Map;

import adss.inventory.domain.Category;
import adss.inventory.domain.CategoryInventoryReport;
import adss.inventory.domain.DefectiveItemReport;
import adss.inventory.domain.InventoryController;
import adss.inventory.domain.ItemType;
import adss.inventory.domain.PurchasingReport;

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