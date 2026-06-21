package adss.inventory.repository.dto;

public class DiscountDTO {

    private int id;

    private String discountType;

    private double percentage;

    private String startDate;
    private String endDate;

    public DiscountDTO(
            int id,
            String discountType,
            double percentage,
            String startDate,
            String endDate) {

        this.id = id;
        this.discountType = discountType;
        this.percentage = percentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public String getDiscountType() {
        return discountType;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}