package adss.inventory.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscountController {

    private final Map<Integer, Discount> discounts = new HashMap<>();
    private int discountIdCounter = 1;

    /**
     * Creates a new discount on a specific list of items.
     * Validates that all given item IDs exist before creating the discount.
     * 
     * @param percentage  the discount percentage (e.g. 10.0 for 10%)
     * @param startDate   the date the discount becomes active
     * @param endDate     the date the discount expires
     * @param targetItems list of resolved ItemType objects
     * @return "OK" if created successfully
     */
    public String addItemDiscount(double percentage,
            LocalDate startDate, LocalDate endDate,
            List<ItemType> targetItems) {
        ItemsDiscount discount = new ItemsDiscount(
                discountIdCounter, percentage,
                startDate, endDate, targetItems);
        discounts.put(discountIdCounter++, discount);
        return "OK";
    }

    /**
     * Creates a new discount on a specific list of categories.
     * The discount applies to all items belonging to any of the given categories.
     * 
     * @param percentage       the discount percentage (e.g. 15.0 for 15%)
     * @param startDate        the date the discount becomes active
     * @param endDate          the date the discount expires
     * @param targetCategories list of resolved Category objects
     * @return "OK" if created successfully
     */
    public String addCategoryDiscount(double percentage,
            LocalDate startDate, LocalDate endDate,
            List<Category> targetCategories) {
        CategoryDiscount discount = new CategoryDiscount(
                discountIdCounter, percentage,
                startDate, endDate, targetCategories);
        discounts.put(discountIdCounter++, discount);
        return "OK";
    }

    /**
     * Returns all discounts that are currently active and apply to a given item.
     * 
     * @param item the ItemType to check
     * @return list of active Discount objects
     */
    public List<Discount> getActiveDiscountsForItem(ItemType item) {
        List<Discount> active = new ArrayList<>();
        for (Discount d : discounts.values()) {
            if (d.isActiveToday() && d.appliesTo(item))
                active.add(d);
        }
        return active;
    }

    /**
     * Calculates the final selling price of an item after applying the best
     * available discount. Only the highest percentage discount is applied.
     * 
     * @param item the ItemType to calculate price for
     * @return final price after best discount, or original sell price if no
     *         discount
     */
    public double getFinalPrice(ItemType item) {
        List<Discount> activeDiscounts = getActiveDiscountsForItem(item);
        if (activeDiscounts.isEmpty())
            return item.getSellingPrice();

        Discount bestDiscount = activeDiscounts.get(0);
        for (Discount d : activeDiscounts) {
            if (d.getPercentage() > bestDiscount.getPercentage())
                bestDiscount = d;
        }
        return bestDiscount.getFinalPrice(item);
    }

    /**
     * Returns all discounts in the system.
     * 
     * @return list of all Discount objects
     */
    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(discounts.values());
    }

    public void loadDiscounts(List<Discount> loadedDiscounts) {
        discounts.clear();

        int maxId = 0;
        for (Discount discount : loadedDiscounts) {
            discounts.put(discount.getId(), discount);
            if (discount.getId() > maxId) {
                maxId = discount.getId();
            }
        }

        discountIdCounter = maxId + 1;
    }
}
