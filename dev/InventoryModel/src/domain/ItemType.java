import java.util.Locale.Category;
import java.util.Objects;

public class ItemType {
    private int id;
    private String name;
    private Location storeLocation;
    private int shelfQuantity;
    private int warehouseQuantity;
    private int minQuantity;
    private int costPrice;
    private int sellingPrice;
    private Category category;
    private String manufacturer;

    public ItemType(int id, String name, Location storeLocation,
                    int shelfQuantity, int warehouseQuantity, int minQuantity,
                    int costPrice, int sellingPrice,
                    Category category, String manufacturer) {

        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (storeLocation == null) {
            throw new IllegalArgumentException("Store location cannot be null");
        }

        if (shelfQuantity < 0 || warehouseQuantity < 0) {
            throw new IllegalArgumentException("Quantities must be non-negative");
        }

        if (minQuantity < 0) {
            throw new IllegalArgumentException("Minimum quantity must be non-negative");
        }

        if (costPrice < 0 || sellingPrice < 0) {
            throw new IllegalArgumentException("Prices must be non-negative");
        }

        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer cannot be null or empty");
        }

        this.id = id;
        this.name = name;
        this.storeLocation = storeLocation;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantity = minQuantity;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.category = category;
        this.manufacturer = manufacturer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(Location storeLocation) {
        if (storeLocation == null) {
            throw new IllegalArgumentException("Store location cannot be null");
        }
        this.storeLocation = storeLocation;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(int shelfQuantity) {
        if (shelfQuantity < 0) {
            throw new IllegalArgumentException("Shelf quantity must be non-negative");
        }
        this.shelfQuantity = shelfQuantity;
    }

    public int getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public void setWarehouseQuantity(int warehouseQuantity) {
        if (warehouseQuantity < 0) {
            throw new IllegalArgumentException("Warehouse quantity must be non-negative");
        }
        this.warehouseQuantity = warehouseQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        if (minQuantity < 0) {
            throw new IllegalArgumentException("Minimum quantity must be non-negative");
        }
        this.minQuantity = minQuantity;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(int costPrice) {
        if (costPrice < 0) {
            throw new IllegalArgumentException("Cost price must be non-negative");
        }
        this.costPrice = costPrice;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(int sellingPrice) {
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price must be non-negative");
        }
        this.sellingPrice = sellingPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer cannot be null or empty");
        }
        this.manufacturer = manufacturer;
    }

    public int getTotalQuantity() {
        return shelfQuantity + warehouseQuantity;
    }

    public boolean needsRestock() {
        return getTotalQuantity() <= minQuantity;
    }

    public void addToShelf(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }

        if (amount > warehouseQuantity) {
            throw new IllegalArgumentException("Not enough items in warehouse");
        }

        warehouseQuantity -= amount;
        shelfQuantity += amount;
    }

    public void addToWarehouse(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        warehouseQuantity += amount;
    }

    public void removeFromShelf(int amount) {
        if (amount < 0 || amount > shelfQuantity) {
            throw new IllegalArgumentException("Invalid amount");
        }
        shelfQuantity -= amount;
    }

    public void removeFromWarehouse(int amount) {
        if (amount < 0 || amount > warehouseQuantity) {
            throw new IllegalArgumentException("Invalid amount");
        }
        warehouseQuantity -= amount;
    }

    @Override
    public String toString() {
        return "ItemType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shelfQuantity=" + shelfQuantity +
                ", warehouseQuantity=" + warehouseQuantity +
                ", minQuantity=" + minQuantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemType)) return false;
        ItemType itemType = (ItemType) o;
        return id == itemType.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}