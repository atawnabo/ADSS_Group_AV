package service;

import domain.Category;

import java.util.List;

public class CategoryController {
    private final domain.InventoryController inventoryController;

    public CategoryController(domain.InventoryController inventoryController) {
        if (inventoryController == null) {
            throw new IllegalArgumentException("InventoryController cannot be null");
        }
        this.inventoryController = inventoryController;
    }

    public String addCategory(String name) {
        return inventoryController.addCategory(name);
    }

    public String addCategory(String name, int parentId) {
        return inventoryController.addCategory(name, parentId);
    }

    public List<Category> getAllCategories() {
        return inventoryController.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return inventoryController.getCategoryById(id);
    }

    public List<Category> getRootCategories() {
        return inventoryController.getRootCategories();
    }
}