package adss.inventory.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DefectiveItemReport extends Report {

    private final String reportType = "Defective Items Report";
    private Map<ItemType, List<Item>> defectiveItems;

    public DefectiveItemReport(int id, LocalDate date, Map<ItemType, List<Item>> defectiveItems) {
        super(id, date);

        if (defectiveItems == null) {
            throw new IllegalArgumentException("Defective items map cannot be null");
        }

        this.defectiveItems = defectiveItems;
    }

    @Override
    public String getReportType() {
        return reportType;
    }

    public Map<ItemType, List<Item>> getDefectiveItems() {
        return defectiveItems;
    }

    public void setDefectiveItems(Map<ItemType, List<Item>> defectiveItems) {
        if (defectiveItems == null) {
            throw new IllegalArgumentException("Defective items map cannot be null");
        }
        this.defectiveItems = defectiveItems;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ").append(reportType).append(" ===\n");
        sb.append("Report ID: ").append(id).append("\n");
        sb.append("Date: ").append(date).append("\n");

        if (defectiveItems.isEmpty()) {
            sb.append("No defective items found.\n");
            return sb.toString();
        }

        sb.append("Defective Items:\n");

        for (Map.Entry<ItemType, List<Item>> entry : defectiveItems.entrySet()) {
            ItemType itemType = entry.getKey();
            List<Item> items = entry.getValue();

            sb.append("\nItem Type: ").append(itemType.getName()).append("\n");

            if (items == null || items.isEmpty()) {
                sb.append("  No specific defective items listed.\n");
            } else {
                for (Item item : items) {
                    sb.append("  - ").append(item).append("\n");
                }
            }
        }

        return sb.toString();
    }
}