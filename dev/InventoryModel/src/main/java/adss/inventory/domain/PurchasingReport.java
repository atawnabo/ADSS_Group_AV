package adss.inventory.domain;
import java.time.LocalDate;
import java.util.Map;

public class PurchasingReport extends Report {

    private final String reportType = "Purchasing Report";
    private Map<ItemType, Integer> itemsToOrder;

    public PurchasingReport(int id, LocalDate date, Map<ItemType, Integer> itemsToOrder) {
        super(id, date);

        if (itemsToOrder == null) {
            throw new IllegalArgumentException("Items to order map cannot be null");
        }

        this.itemsToOrder = itemsToOrder;
    }

    @Override
    public String getReportType() {
        return reportType;
    }

    public Map<ItemType, Integer> getItemsToOrder() {
        return itemsToOrder;
    }

    public void setItemsToOrder(Map<ItemType, Integer> itemsToOrder) {
        if (itemsToOrder == null) {
            throw new IllegalArgumentException("Items to order map cannot be null");
        }
        this.itemsToOrder = itemsToOrder;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ").append(reportType).append(" ===\n");
        sb.append("Report ID: ").append(id).append("\n");
        sb.append("Date: ").append(date).append("\n");

        if (itemsToOrder.isEmpty()) {
            sb.append("No items need to be ordered.\n");
            return sb.toString();
        }

        sb.append("Items to Order:\n");

        for (Map.Entry<ItemType, Integer> entry : itemsToOrder.entrySet()) {
            ItemType itemType = entry.getKey();
            Integer quantity = entry.getValue();

            sb.append("- ")
              .append(itemType.getName())
              .append(" | Quantity to order: ")
              .append(quantity)
              .append("\n");
        }

        return sb.toString();
    }
}