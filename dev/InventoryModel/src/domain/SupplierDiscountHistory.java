package domain;
import java.time.LocalDate;

public class SupplierDiscountHistory {
    private int id;
    private double discountPercentage;
    private LocalDate date;        // when this discount was given
    private String supplierName;   // who gave the discount

    public SupplierDiscountHistory(int id, double discountPercentage,
                                   LocalDate date, String supplierName) {
        this.id = id;
        this.discountPercentage = discountPercentage;
        this.date = date;
        this.supplierName = supplierName;
    }

    // getters
    public int getId() { return id; }
    public double getDiscountPercentage() { return discountPercentage; }
    public LocalDate getDate() { return date; }
    public String getSupplierName() { return supplierName; }
}