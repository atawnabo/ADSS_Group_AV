package adss.inventory.domain;

import java.time.LocalDate;

public abstract class Discount {

    private int id;
    private double percentage;
    private LocalDate startDate;
    private LocalDate endDate;

    public Discount(int id, double percentage,
                    LocalDate startDate, LocalDate endDate) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        this.id = id;
        this.percentage = percentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public abstract boolean appliesTo(ItemType item);

    public boolean isActiveToday() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public double getFinalPrice(ItemType item) {
        if (item == null || !appliesTo(item) || !isActiveToday()) {
            return item != null ? item.getSellingPrice() : 0;
        }
        return item.getSellingPrice() * (1 - percentage / 100);
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