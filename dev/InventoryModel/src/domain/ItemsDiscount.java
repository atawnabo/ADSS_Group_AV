
import java.time.LocalDate;
import java.util.List;

public class ItemsDiscount extends Discount {

    private List<ItemType> targetItems;

    public ItemsDiscount(int id, double percentage,
            LocalDate startDate, LocalDate endDate,
            List<ItemType> targetItems) {
        super(id, percentage, startDate, endDate); 
        this.targetItems = targetItems;
    }

    @Override
    public boolean appliesTo(ItemType item) {
        return targetItems.contains(item);
    }
     public List<ItemType> getTargetItems() {
        return targetItems;
    }
}