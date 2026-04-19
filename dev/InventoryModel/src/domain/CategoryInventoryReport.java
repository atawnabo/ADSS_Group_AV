package domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CategoryInventoryReport extends Report {

    private final String reportType = "Category Inventory Report";
    private List<Category> categories;
    private Map<Category, List<ItemType>> itemsByCategory; // ← add this

    public CategoryInventoryReport(int id, LocalDate date,
                                   List<Category> categories,
                                   Map<Category, List<ItemType>> itemsByCategory) {
        super(id, date);
        if (categories == null)
            throw new IllegalArgumentException("Categories list cannot be null");
        if (itemsByCategory == null)
            throw new IllegalArgumentException("Items map cannot be null");

        this.categories = categories;
        this.itemsByCategory = itemsByCategory;
    }

    @Override
    public String getReportType() {
        return reportType;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Map<Category, List<ItemType>> getItemsByCategory() {
        return itemsByCategory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(reportType).append(" ===\n");
        sb.append("Report ID: ").append(id).append("\n");
        sb.append("Date:      ").append(date).append("\n");
        sb.append("─────────────────────────────────\n");

        if (categories.isEmpty()) {
            sb.append("No categories in this report.\n");
        } else {
            for (Category category : categories) {
                sb.append("\nCategory: ").append(category.getFullPath()).append("\n");
                List<ItemType> items = itemsByCategory.get(category);
                if (items == null || items.isEmpty()) {
                    sb.append("  No items in this category.\n");
                } else {
                    sb.append(String.format("  %-5s %-25s %-10s %-10s %-8s%n",
                            "ID", "Name", "Cost", "Price", "Stock"));
                    sb.append("  ─────────────────────────────────────────────\n");
                    for (ItemType item : items) {
                        sb.append(String.format("  %-5d %-25s %-10d %-10d %-8d%n",
                                item.getId(),
                                item.getName(),
                                item.getCostPrice(),
                                item.getSellingPrice(),
                                item.getTotalQuantity()));
                    }
                }
            }
        }

        sb.append("=================================\n");
        return sb.toString();
    }
}