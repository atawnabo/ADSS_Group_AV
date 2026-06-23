package adss.inventory.dto;

public class ItemDTO {

    private int id;

    private int itemTypeId;

    private int sellDiscount;
    private int buyDiscount;

    private int itemPrice;
    private int itemSellPrice;

    private String expirationDate;

    private boolean damaged;
    private boolean inWarehouse;

    public ItemDTO(
            int id,
            int itemTypeId,
            int sellDiscount,
            int buyDiscount,
            int itemPrice,
            int itemSellPrice,
            String expirationDate,
            boolean damaged,
            boolean inWarehouse) {

        this.id = id;
        this.itemTypeId = itemTypeId;
        this.sellDiscount = sellDiscount;
        this.buyDiscount = buyDiscount;
        this.itemPrice = itemPrice;
        this.itemSellPrice = itemSellPrice;
        this.expirationDate = expirationDate;
        this.damaged = damaged;
        this.inWarehouse = inWarehouse;
    }

    public int getId() {
        return id;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public int getSellDiscount() {
        return sellDiscount;
    }

    public int getBuyDiscount() {
        return buyDiscount;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getItemSellPrice() {
        return itemSellPrice;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public boolean isInWarehouse() {
        return inWarehouse;
    }
}