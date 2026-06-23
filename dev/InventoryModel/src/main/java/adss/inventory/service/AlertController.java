package adss.inventory.service;

import adss.inventory.domain.Alert;
import adss.inventory.domain.InventoryController;

import java.util.List;


public class AlertController {
    private final InventoryController inventoryController;

    public AlertController(InventoryController inventoryController) {
        if (inventoryController == null) {
            throw new IllegalArgumentException("InventoryController cannot be null");
        }
        this.inventoryController = inventoryController;
    }

    public List<Alert> getAllAlerts() {
        return inventoryController.getAllAlerts();
    }

    public Alert getAlertForItemType(int itemTypeId) {
        return inventoryController.getAlertForItemType(itemTypeId);
    }
}
