package adss.inventory.domain;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {

    private int id;
    private String name;
    private List<Item> items;
    private int capacity;

    public Warehouse(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public int countOf(ItemType type) {
        int count = 0;
        for (Item item : items) {
            if (item.getItemType().getId() == type.getId()) {
                count++;
            }
        }
        return count;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Item> getItems() { return items; }
    public int getCapacity() { return capacity; }
}
