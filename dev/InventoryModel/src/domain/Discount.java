import java.time.LocalDate;

abstract class Discount {

    private int id;
    private double percentage;
    private LocalDate startDate;
    private LocalDate endDate;

    public Discount(int id, double percentage,
            LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.percentage = percentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // does this discount apply to this item?
    public abstract boolean appliesTo(ItemType item);

    // is it active today?
    public boolean isActiveToday() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate)
                && !today.isAfter(endDate);
    }

    // final price calculation - once, here
    public double getFinalPrice(ItemType item) {
        if (!appliesTo(item) || !isActiveToday()) {
            return item.getSellPrice();
        }
        return item.getSellPrice() * (1 - percentage / 100);
    }
       public int getId() {
        return id;
    }

    public double getPercentage() {
        return percentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}