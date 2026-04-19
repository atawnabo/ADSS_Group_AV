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
    /**
     * Adds a root category.
     * Validates name is not empty.
     */
    public String addRootCategory(String name) {
        if (name == null || name.trim().isEmpty())
            return "ERROR: name cannot be empty";
        return inventoryController.addCategory(name.trim());
    }

    /**
     * Adds a sub-category under a parent.
     * Validates name and parent ID.
     */
    public String addSubCategory(String name, int parentId) {
        if (name == null || name.trim().isEmpty())
            return "ERROR: name cannot be empty";
        if (parentId <= 0)
            return "ERROR: invalid parent ID";
        return inventoryController.addCategory(name.trim(), parentId);
    }

    /**
     * Returns all categories for display.
     */
    public List<Category> getAllCategories() {
        return inventoryController.getAllCategories();
    }

    /**
     * Returns all root categories for tree display.
     */
    public List<Category> getRootCategories() {
        return inventoryController.getRootCategories();
    }

    /**
     * Returns a single category by ID.
     */
    public Category getCategoryById(int id) {
        if (id <= 0) return null;
        return inventoryController.getCategoryById(id);
    }
}
