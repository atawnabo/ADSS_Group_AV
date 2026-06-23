package adss.inventory.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Item {

    private final ItemType itemType;
    private final int itemID;
    private int sellDiscount;
    private int buyDiscount;
    private int itemPrice;
    private int itemSellPrice;
    private LocalDate expirationDate;
    private boolean damaged;
    private boolean inWarehouse;

    public Item(ItemType itemType, int id,
            int sellDiscount, int buyDiscount,
            LocalDate expirationDate,
            boolean damaged, boolean inWarehouse) {

        this.itemType = itemType;
        this.itemID = id;
        this.sellDiscount = sellDiscount;
        this.buyDiscount = buyDiscount;
        this.expirationDate = expirationDate;
        this.damaged = damaged;
        this.inWarehouse = inWarehouse;

        updatePrices();
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getId() {
        return itemID;
    }

    public int getSellDiscount() {
        return sellDiscount;
    }

    public void setSellDiscount(int sellDiscount) {
        this.sellDiscount = sellDiscount;
        updatePrices();
    }

    public int getBuyDiscount() {
        return buyDiscount;
    }

    public void setBuyDiscount(int buyDiscount) {
        this.buyDiscount = buyDiscount;
        updatePrices();
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getItemSellPrice() {
        return itemSellPrice;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

    public boolean isInWarehouse() {
        return inWarehouse;
    }

    public void setInWarehouse(boolean inWarehouse) {
        this.inWarehouse = inWarehouse;
    }

    public boolean isExpired() {
        return expirationDate != null
                && expirationDate.isBefore(LocalDate.now());
    }

    public void updatePrices() {
        this.itemPrice = itemType.getCostPrice()
                - (itemType.getCostPrice() * buyDiscount / 100);

        this.itemSellPrice = itemType.getSellingPrice()
                - (itemType.getSellingPrice() * sellDiscount / 100);
    }

    public boolean isAvailableForSale() {
        return !damaged && !isExpired();
    }

    @Override
    public String toString() {
        return String.format(
                "ID %-3d | Type: %-22s | Loc: %-9s | Cost: %-5s | Sell: %-5s | Exp: %-10s | Dmg: %-3s | Expired: %-3s | Avail: %-3s",
                itemID,
                itemType.getName(),
                inWarehouse ? "Warehouse" : "Shelf",
                itemPrice,
                itemSellPrice,
                expirationDate != null ? expirationDate : "N/A",
                damaged ? "YES" : "No",
                isExpired() ? "YES" : "No",
                isAvailableForSale() ? "Yes" : "NO"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return itemID == item.itemID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID);
    }
}
