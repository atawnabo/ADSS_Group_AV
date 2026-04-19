package presentation;

import domain.Alert;
import domain.Category;
import domain.CategoryInventoryReport;
import domain.DefectiveItemReport;
import domain.Discount;
import domain.Item;
import domain.ItemType;
import domain.Location;
import domain.PurchasingReport;
import domain.SupplierDiscountHistory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import service.ServiceController;

public class PresentationController {
    private final ServiceController serviceController;

    public PresentationController() {
        this.serviceController = new ServiceController();
    }

    // ==================== CATEGORY ====================

    public String addCategory(String name) {
        return serviceController.getCategoryController().addRootCategory(name);
    }

    public String addCategory(String name, int parentId) {
        return serviceController.getCategoryController().addSubCategory(name, parentId);
    }

    public List<Category> getAllCategories() {
        return serviceController.getCategoryController().getAllCategories();
    }

    public Category getCategoryById(int id) {
        return serviceController.getCategoryController().getCategoryById(id);
    }

    public List<Category> getRootCategories() {
        return serviceController.getCategoryController().getRootCategories();
    }

    // ==================== ITEM TYPE / ITEMS ====================

    public int addItemType(String name,
                           Location storeLocation,
                           int minQuantity,
                           int costPrice,
                           int sellingPrice,
                           int categoryId,
                           String manufacturer) {
        return serviceController.getItemController().addItemType(
                name, storeLocation, minQuantity, costPrice, sellingPrice, categoryId, manufacturer
        );
    }

    public ItemType getItemTypeById(int itemTypeId) {
        return serviceController.getItemController().getItemTypeById(itemTypeId);
    }

    public List<ItemType> getAllItemTypes() {
        return serviceController.getItemController().getAllItemTypes();
    }

    public boolean updateMinQuantity(int itemTypeId, int minQuantity) {
        return serviceController.getItemController().updateMinQuantity(itemTypeId, minQuantity);
    }

    public int addItem(int itemTypeId,
                       int sellDiscount,
                       int buyDiscount,
                       LocalDate expirationDate,
                       boolean damaged,
                       boolean inWarehouse) {
        return serviceController.getItemController().addItem(
                itemTypeId, sellDiscount, buyDiscount, expirationDate, damaged, inWarehouse
        );
    }

    public Item getItemById(int itemId) {
        return serviceController.getItemController().getItemById(itemId);
    }

    public List<Item> getAllItems() {
        return serviceController.getItemController().getAllItems();
    }

    public List<Item> getItemsByType(int itemTypeId) {
        return serviceController.getItemController().getItemsByType(itemTypeId);
    }

    public boolean moveItemToShelf(int itemId) {
        return serviceController.getItemController().moveItemToShelf(itemId);
    }

    public boolean moveItemsToShelf(int itemTypeId, int amount) {
        return serviceController.getItemController().moveItemsToShelf(itemTypeId, amount);
    }

    public boolean moveItemToWarehouse(int itemId) {
        return serviceController.getItemController().moveItemToWarehouse(itemId);
    }

    public boolean markItemAsDamaged(int itemId) {
        return serviceController.getItemController().markItemAsDamaged(itemId);
    }

    public boolean unmarkItemAsDamaged(int itemId) {
        return serviceController.getItemController().unmarkItemAsDamaged(itemId);
    }

    public boolean updateItemExpirationDate(int itemId, LocalDate newDate) {
        return serviceController.getItemController().updateItemExpirationDate(itemId, newDate);
    }

    public boolean removeItem(int itemId) {
        return serviceController.getItemController().removeItem(itemId);
    }

    public List<ItemType> getItemTypesByCategory(int categoryId) {
        return serviceController.getItemController().getItemTypesByCategory(categoryId);
    }

    // ==================== DISCOUNTS ====================

    public String addItemDiscount(double percentage,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  List<Integer> itemIds) {
        return serviceController.getDiscountController().addItemDiscount(
                percentage, startDate, endDate, itemIds
        );
    }

    public String addCategoryDiscount(double percentage,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      List<Integer> categoryIds) {
        return serviceController.getDiscountController().addCategoryDiscount(
                percentage, startDate, endDate, categoryIds
        );
    }

    public List<Discount> getActiveDiscountsForItem(int itemTypeId) {
        return serviceController.getDiscountController().getActiveDiscountsForItem(itemTypeId);
    }

    public List<Discount> getAllDiscounts() {
        return serviceController.getDiscountController().getAllDiscounts();
    }

    public double getFinalPrice(int itemTypeId) {
        return serviceController.getDiscountController().getFinalPrice(itemTypeId);
    }

    public String addSupplierDiscount(int itemTypeId,
                                      double percentage,
                                      LocalDate date,
                                      String supplierName) {
        return serviceController.getDiscountController().addSupplierDiscount(
                itemTypeId, percentage, date, supplierName
        );
    }

    public List<SupplierDiscountHistory> getSupplierDiscountHistory(int itemTypeId) {
        return serviceController.getDiscountController().getSupplierDiscountHistory(itemTypeId);
    }

    // ==================== REPORTS ====================

    public CategoryInventoryReport createCategoryInventoryReport(List<Integer> categoryIds) {
        return serviceController.getReportController().createCategoryInventoryReport(categoryIds);
    }

    public Map<Category, List<ItemType>> getInventoryByCategories(List<Integer> categoryIds) {
        return serviceController.getReportController().getInventoryByCategories(categoryIds);
    }

    public DefectiveItemReport createDefectiveItemReport() {
        return serviceController.getReportController().createDefectiveItemReport();
    }

    public PurchasingReport createPurchasingReport() {
        return serviceController.getReportController().createPurchasingReport();
    }

    // ==================== ALERTS ====================

    public List<Alert> getAllAlerts() {
        return serviceController.getAlertController().getAllAlerts();
    }

    public Alert getAlertForItemType(int itemTypeId) {
        return serviceController.getAlertController().getAlertForItemType(itemTypeId);
    }
}