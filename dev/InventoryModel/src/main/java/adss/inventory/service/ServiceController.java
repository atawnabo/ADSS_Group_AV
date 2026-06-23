package adss.inventory.service;

import adss.inventory.domain.InventoryController;

public class ServiceController {
    private final InventoryController inventoryController;

    private final AlertController alertController;
    private final ItemController itemController;
    private final ReportController reportController;
    private final CategoryController categoryController;
    private final DiscountController discountController;

    public ServiceController() {
        this.inventoryController = new InventoryController();

        this.alertController = new AlertController(inventoryController);
        this.itemController = new ItemController(inventoryController);
        this.reportController = new ReportController(inventoryController);
        this.categoryController = new CategoryController(inventoryController);
        this.discountController = new DiscountController(inventoryController);
    }

    public AlertController getAlertController() {
        return alertController;
    }

    public ItemController getItemController() {
        return itemController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public CategoryController getCategoryController() {
        return categoryController;
    }

    public DiscountController getDiscountController() {
        return discountController;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public void loadDataFromDatabase() {
        inventoryController.loadDataFromDatabase();
    }

    public void initializeSimpleData() {
        inventoryController.initializeSimpleData();
    }

    public void clearSystemData() {
        inventoryController.clearSystemData();
    }
}