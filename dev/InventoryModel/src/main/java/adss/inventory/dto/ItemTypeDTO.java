package adss.inventory.dto;

public class ItemTypeDTO {

    private int id;
    private String name;

    private Integer shelfNum;
    private Integer aisleNum;

    private int shelfQuantity;
    private int warehouseQuantity;
    private int minQuantity;

    private int costPrice;
    private int sellingPrice;

    private int categoryId;

    private String manufacturer;

    public ItemTypeDTO(
            int id,
            String name,
            Integer shelfNum,
            Integer aisleNum,
            int shelfQuantity,
            int warehouseQuantity,
            int minQuantity,
            int costPrice,
            int sellingPrice,
            int categoryId,
            String manufacturer) {

        this.id = id;
        this.name = name;
        this.shelfNum = shelfNum;
        this.aisleNum = aisleNum;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantity = minQuantity;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.categoryId = categoryId;
        this.manufacturer = manufacturer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getShelfNum() {
        return shelfNum;
    }

    public Integer getAisleNum() {
        return aisleNum;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public int getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getManufacturer() {
        return manufacturer;
    }
}