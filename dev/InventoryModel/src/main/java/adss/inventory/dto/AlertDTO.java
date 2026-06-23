package adss.inventory.dto;

public class AlertDTO {

    private int id;

    private String description;

    private int itemTypeId;

    public AlertDTO(
            int id,
            String description,
            int itemTypeId) {

        this.id = id;
        this.description = description;
        this.itemTypeId = itemTypeId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }
}