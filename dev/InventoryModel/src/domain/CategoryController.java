import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryController {

    private final Map<Integer, Category> categories = new HashMap<>();
    private int categoryIdCounter = 1;

    /**
     * Adds a root category (no parent) to the system.
     * @param name the name of the new category
     * @return "OK" if added successfully
     */
    public String addCategory(String name) {
        Category cat = new Category(categoryIdCounter, name);
        categories.put(categoryIdCounter++, cat);
        return "OK";
    }

    /**
     * Adds a sub-category under an existing parent category.
     * @param name the name of the new sub-category
     * @param parentId the ID of the parent category
     * @return "OK" if added, "ERROR" if parent not found
     */
    public String addCategory(String name, int parentId) {
        Category parent = categories.get(parentId);
        if (parent == null) {
            return "ERROR: parent not found";
        }
        Category cat = new Category(categoryIdCounter, name, parent);
        parent.addChild(cat);
        categories.put(categoryIdCounter++, cat);
        return "OK";
    }

    /**
     * Returns all categories in the system as a list.
     * @return list of all Category objects
     */
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    /**
     * Finds and returns a single category by its ID.
     * @param id the category ID to look up
     * @return the Category if found, null otherwise
     */
    public Category getCategoryById(int id) {
        return categories.get(id);
    }

    /**
     * Returns all root categories (categories with no parent).
     * Used for tree display in presentation layer.
     * @return list of root Category objects
     */
    public List<Category> getRootCategories() {
        List<Category> roots = new ArrayList<>();
        for (Category cat : categories.values()) {
            if (cat.getParent() == null)
                roots.add(cat);
        }
        return roots;
    }
}