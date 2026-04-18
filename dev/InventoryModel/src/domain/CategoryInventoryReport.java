import java.time.LocalDate;
import java.util.List;
import java.util.Locale.Category;

public class CategoryInventoryReport extends Report {

    private final String reportType = "Category Inventory Report";
    private List<Category> categories;

    public CategoryInventoryReport(int id, LocalDate date, List<Category> categories) {
        super(id, date);

        if (categories == null) {
            throw new IllegalArgumentException("Categories list cannot be null");
        }

        this.categories = categories;
    }

    @Override
    public String getReportType() {
        return reportType;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        if (categories == null) {
            throw new IllegalArgumentException("Categories list cannot be null");
        }
        this.categories = categories;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ").append(reportType).append(" ===\n");
        sb.append("Report ID: ").append(id).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Categories:\n");

        if (categories.isEmpty()) {
            sb.append("No categories in this report.\n");
        } else {
            for (Category category : categories) {
                sb.append("- ").append(category).append("\n");
            }
        }

        return sb.toString();
    }
}