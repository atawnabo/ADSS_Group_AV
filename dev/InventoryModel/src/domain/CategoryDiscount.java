package domain;
import java.time.LocalDate;
import java.util.List;
public class CategoryDiscount extends Discount {

    private List<Category> targetCategories;

    public CategoryDiscount(int id, double percentage,
            LocalDate startDate, LocalDate endDate,
            List<Category> targetCategories) {
        super(id, percentage, startDate, endDate); // calls abstract constructor
        this.targetCategories = targetCategories;
    }

    @Override
    public boolean appliesTo(ItemType item) {
        for (Category target : targetCategories) {
            if (item.getCategory()
                    .getCategoryPath()
                    .contains(target)) {
                return true;
            }
        }
        return false;
    }
     public List<Category> getTargetCategories() {
        return targetCategories;
    }
}