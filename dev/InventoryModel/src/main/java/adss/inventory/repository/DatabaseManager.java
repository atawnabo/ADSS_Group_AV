package adss.inventory.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static  String DB_URL = "jdbc:sqlite:inventory.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        return connection;
    }
       // Lets tests point at a different database file so they don't touch the real one.
    public static void setDatabasePath(String path) {
        DB_URL = "jdbc:sqlite:" + path;
    }
}