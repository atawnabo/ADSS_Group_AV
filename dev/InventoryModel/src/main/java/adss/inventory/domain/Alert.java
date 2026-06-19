package adss.inventory.domain;

import java.util.Objects;

public class Alert {

    private final int id;
    private String description;
    private ItemType itemType;

    public Alert(int id, String description, ItemType itemType) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("ItemType cannot be null");
        }

        this.id = id;
        this.description = description;
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        if (itemType == null) {
            throw new IllegalArgumentException("ItemType cannot be null");
        }
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return String.format(
                "=== Alert ===%n"
                + "ID: %d%n"
                + "Description: %s%n",
                id, description
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alert)) {
            return false;
        }
        Alert alert = (Alert) o;
        return id == alert.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
