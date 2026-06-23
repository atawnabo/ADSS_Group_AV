package adss.inventory.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            createCategoriesTable(statement);
            createItemTypesTable(statement);
            createItemsTable(statement);
            createDiscountsTable(statement);
            createItemDiscountTargetsTable(statement);
            createCategoryDiscountTargetsTable(statement);
            createAlertsTable(statement);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void createCategoriesTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    parent_id INTEGER,
                    FOREIGN KEY(parent_id) REFERENCES categories(id)
                )
                """);
    }

    private static void createItemTypesTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS item_types (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    shelf_num INTEGER,
                    aisle_num INTEGER,
                    shelf_quantity INTEGER NOT NULL,
                    warehouse_quantity INTEGER NOT NULL,
                    min_quantity INTEGER NOT NULL,
                    cost_price INTEGER NOT NULL,
                    selling_price INTEGER NOT NULL,
                    category_id INTEGER NOT NULL,
                    manufacturer TEXT,
                    FOREIGN KEY(category_id) REFERENCES categories(id)
                )
                """);
    }

    private static void createItemsTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY,
                    item_type_id INTEGER NOT NULL,
                    sell_discount INTEGER NOT NULL,
                    buy_discount INTEGER NOT NULL,
                    item_price INTEGER NOT NULL,
                    item_sell_price INTEGER NOT NULL,
                    expiration_date TEXT,
                    damaged INTEGER NOT NULL,
                    in_warehouse INTEGER NOT NULL,
                    FOREIGN KEY(item_type_id) REFERENCES item_types(id)
                )
                """);
    }

    private static void createDiscountsTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS discounts (
                    id INTEGER PRIMARY KEY,
                    discount_type TEXT NOT NULL,
                    percentage REAL NOT NULL,
                    start_date TEXT NOT NULL,
                    end_date TEXT NOT NULL
                )
                """);
    }

    private static void createItemDiscountTargetsTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS item_discount_targets (
                    discount_id INTEGER NOT NULL,
                    item_type_id INTEGER NOT NULL,
                    PRIMARY KEY(discount_id, item_type_id),
                    FOREIGN KEY(discount_id) REFERENCES discounts(id),
                    FOREIGN KEY(item_type_id) REFERENCES item_types(id)
                )
                """);
    }

    private static void createCategoryDiscountTargetsTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS category_discount_targets (
                    discount_id INTEGER NOT NULL,
                    category_id INTEGER NOT NULL,
                    PRIMARY KEY(discount_id, category_id),
                    FOREIGN KEY(discount_id) REFERENCES discounts(id),
                    FOREIGN KEY(category_id) REFERENCES categories(id)
                )
                """);
    }

    private static void createAlertsTable(Statement statement) throws SQLException {
        statement.execute("""
                CREATE TABLE IF NOT EXISTS alerts (
                    id INTEGER PRIMARY KEY,
                    description TEXT NOT NULL,
                    item_type_id INTEGER NOT NULL,
                    FOREIGN KEY(item_type_id) REFERENCES item_types(id)
                )
                """);
    }
}