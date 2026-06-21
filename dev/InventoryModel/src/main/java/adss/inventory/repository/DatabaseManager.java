package adss.inventory.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        return connection;
    }
}