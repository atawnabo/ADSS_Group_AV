package superli.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String DEFAULT_DATABASE_PATH = "data/company_management.db";
    private static final String DATABASE_PATH_PROPERTY = "company.db.path";

private static Path getDatabasePath() {
    String databasePath = System.getProperty(
            DATABASE_PATH_PROPERTY,
            DEFAULT_DATABASE_PATH
    );
    return Paths.get(databasePath).toAbsolutePath();
}

private static String getDatabaseUrl() {
    return "jdbc:sqlite:" +
            getDatabasePath().toString().replace("\\", "/");
}

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        Path databasePath = getDatabasePath();
        Path databaseDirectory = databasePath.getParent();
        try {
            if (databaseDirectory != null) {
                Files.createDirectories(databaseDirectory);
            }
        } catch (IOException e) {
            throw new SQLException("Could not create database directory.", e);
        }
        Connection connection = DriverManager.getConnection(getDatabaseUrl());
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }
    

    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            createBranchTable(statement);
            createEmployeeTable(statement);
            createDriverTable(statement);
            createEmployeeRolesTable(statement);
            createAvailabilityTable(statement);
            createShiftTable(statement);
            createShiftRequiredRolesTable(statement);
            createShiftAssignmentsTable(statement);

        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize database.", e);
        }
    }

    private static void createBranchTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS branches (" +
                        "branch_id INTEGER PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "address TEXT NOT NULL" +
                        ")"
        );
    }

    private static void createEmployeeTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                        "employee_id INTEGER PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "bank_name TEXT NOT NULL, " +
                        "account_number INTEGER NOT NULL, " +
                        "employment_type TEXT NOT NULL, " +
                        "start_date TEXT NOT NULL, " +
                        "global_salary REAL NOT NULL, " +
                        "hourly_salary REAL NOT NULL, " +
                        "vacation_days INTEGER NOT NULL, " +
                        "active INTEGER NOT NULL DEFAULT 1 CHECK(active IN (0, 1)), " +
                        "branch_id INTEGER NOT NULL, " +
                        "FOREIGN KEY(branch_id) REFERENCES branches(branch_id)" +
                        ")"
        );
    }

    private static void createDriverTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS drivers (" +
                        "employee_id INTEGER PRIMARY KEY, " +
                        "license_type TEXT NOT NULL, " +
                        "FOREIGN KEY(employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                        ")"
        );
    }

    private static void createEmployeeRolesTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS employee_roles (" +
                        "employee_id INTEGER NOT NULL, " +
                        "role TEXT NOT NULL, " +
                        "PRIMARY KEY(employee_id, role), " +
                        "FOREIGN KEY(employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                        ")"
        );
    }

    private static void createAvailabilityTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS availability (" +
                        "employee_id INTEGER NOT NULL, " +
                        "day_of_week INTEGER NOT NULL CHECK(day_of_week BETWEEN 1 AND 7), " +
                        "morning_available INTEGER NOT NULL CHECK(morning_available IN (0, 1)), " +
                        "evening_available INTEGER NOT NULL CHECK(evening_available IN (0, 1)), " +
                        "PRIMARY KEY(employee_id, day_of_week), " +
                        "FOREIGN KEY(employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                        ")"
        );
    }

    private static void createShiftTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS shifts (" +
                        "shift_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "shift_date TEXT NOT NULL, " +
                        "shift_type TEXT NOT NULL, " +
                        "branch_id INTEGER NOT NULL, " +
                        "UNIQUE(shift_date, shift_type, branch_id), " +
                        "FOREIGN KEY(branch_id) REFERENCES branches(branch_id)" +
                        ")"
        );
    }

    private static void createShiftRequiredRolesTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS shift_required_roles (" +
                        "shift_id INTEGER NOT NULL, " +
                        "role TEXT NOT NULL, " +
                        "required_amount INTEGER NOT NULL CHECK(required_amount > 0), " +
                        "PRIMARY KEY(shift_id, role), " +
                        "FOREIGN KEY(shift_id) REFERENCES shifts(shift_id) ON DELETE CASCADE" +
                        ")"
        );
    }

    private static void createShiftAssignmentsTable(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS shift_assignments (" +
                        "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "shift_id INTEGER NOT NULL, " +
                        "employee_id INTEGER NOT NULL, " +
                        "role TEXT NOT NULL, " +
                        "UNIQUE(shift_id, employee_id), " +
                        "FOREIGN KEY(shift_id) REFERENCES shifts(shift_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY(employee_id) REFERENCES employees(employee_id)" +
                        ")"
        );
    }

    public static void clearDatabase() {
    try (Connection connection = getConnection();
         Statement statement = connection.createStatement()) {

        connection.setAutoCommit(false);

        try {
            statement.executeUpdate("DELETE FROM shift_assignments");
            statement.executeUpdate("DELETE FROM shift_required_roles");
            statement.executeUpdate("DELETE FROM shifts");
            statement.executeUpdate("DELETE FROM availability");
            statement.executeUpdate("DELETE FROM employee_roles");
            statement.executeUpdate("DELETE FROM drivers");
            statement.executeUpdate("DELETE FROM employees");
            statement.executeUpdate("DELETE FROM branches");

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

    } catch (SQLException e) {
        throw new IllegalStateException("Could not clear database.", e);
    }
}
}