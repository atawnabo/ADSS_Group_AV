package superli.data;

import superli.domain.Availability;
import superli.domain.BankAccount;
import superli.domain.Driver;
import superli.domain.Employee;
import superli.domain.EmployeeTerms;
import superli.domain.Role;
import superli.domain.StoreBranch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeDAO {

    private final BranchDAO branchDAO = new BranchDAO();

    public void save(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        if (employee.getBranch() == null) {
            throw new IllegalArgumentException("Employee must belong to a branch.");
        }

        // קודם שומרים את הסניף, כי employee תלוי בו עם Foreign Key.
        branchDAO.save(employee.getBranch());

        String employeeSql =
                "INSERT INTO employees(" +
                        "employee_id, name, bank_name, account_number, " +
                        "employment_type, start_date, global_salary, hourly_salary, " +
                        "vacation_days, active, branch_id" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT(employee_id) DO UPDATE SET " +
                        "name = excluded.name, " +
                        "bank_name = excluded.bank_name, " +
                        "account_number = excluded.account_number, " +
                        "employment_type = excluded.employment_type, " +
                        "start_date = excluded.start_date, " +
                        "global_salary = excluded.global_salary, " +
                        "hourly_salary = excluded.hourly_salary, " +
                        "vacation_days = excluded.vacation_days, " +
                        "active = excluded.active, " +
                        "branch_id = excluded.branch_id";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(employeeSql)) {

                    BankAccount bankAccount = employee.getBankDetails();
                    EmployeeTerms terms = employee.getEmployeeTerms();

                    statement.setInt(1, employee.getId());
                    statement.setString(2, employee.getName());
                    statement.setString(3, bankAccount.getBankName());
                    statement.setInt(4, bankAccount.getAccountNumber());
                    statement.setString(5, terms.getEmploymentType());
                    statement.setString(6,
                            String.valueOf(terms.getStartDate().getTime()));
                    statement.setDouble(7, terms.getGlobalSalary());
                    statement.setDouble(8, terms.getHourlySalary());
                    statement.setInt(9, terms.getVacationDays());
                    statement.setInt(10, employee.isActive() ? 1 : 0);
                    statement.setInt(11, employee.getBranch().getBranchId());

                    statement.executeUpdate();
                }

                replaceRoles(connection, employee);
                replaceAvailability(connection, employee);
                replaceDriverData(connection, employee);

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not save employee.", e);
        }
    }

    public Employee findById(int employeeId) {
        String sql =
                "SELECT e.employee_id, e.name AS employee_name, " +
                        "e.bank_name, e.account_number, " +
                        "e.employment_type, e.start_date, " +
                        "e.global_salary, e.hourly_salary, e.vacation_days, " +
                        "e.active, " +
                        "b.branch_id, b.name AS branch_name, b.address AS branch_address, " +
                        "d.license_type " +
                        "FROM employees e " +
                        "JOIN branches b ON e.branch_id = b.branch_id " +
                        "LEFT JOIN drivers d ON e.employee_id = d.employee_id " +
                        "WHERE e.employee_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                StoreBranch branch = new StoreBranch(
                        resultSet.getInt("branch_id"),
                        resultSet.getString("branch_name"),
                        resultSet.getString("branch_address")
                );

                BankAccount bankAccount = new BankAccount(
                        resultSet.getString("bank_name"),
                        resultSet.getInt("account_number"),
                        resultSet.getString("employee_name")
                );

                Date startDate = new Date(
                        Long.parseLong(resultSet.getString("start_date"))
                );

                EmployeeTerms employeeTerms = new EmployeeTerms(
                        startDate,
                        resultSet.getString("employment_type"),
                        resultSet.getDouble("global_salary"),
                        resultSet.getDouble("hourly_salary"),
                        resultSet.getInt("vacation_days")
                );

                List<Role> roles = loadRoles(connection, employeeId);
                String licenseType = resultSet.getString("license_type");

                Employee employee;

                if (licenseType != null) {
                    employee = new Driver(
                            employeeId,
                            resultSet.getString("employee_name"),
                            bankAccount,
                            employeeTerms,
                            roles,
                            branch,
                            licenseType
                    );
                } else {
                    employee = new Employee(
                            employeeId,
                            resultSet.getString("employee_name"),
                            bankAccount,
                            employeeTerms,
                            roles,
                            branch
                    );
                }

                employee.setActive(resultSet.getInt("active") == 1);
                loadAvailability(connection, employee);

                return employee;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load employee.", e);
        }
    }

    private void replaceRoles(Connection connection, Employee employee)
            throws SQLException {

        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM employee_roles WHERE employee_id = ?")) {

            deleteStatement.setInt(1, employee.getId());
            deleteStatement.executeUpdate();
        }

        String insertSql =
                "INSERT INTO employee_roles(employee_id, role) VALUES (?, ?)";

        try (PreparedStatement insertStatement =
                     connection.prepareStatement(insertSql)) {

            for (Role role : employee.getRoles()) {
                insertStatement.setInt(1, employee.getId());
                insertStatement.setString(2, role.name());
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        }
    }

    private void replaceAvailability(Connection connection, Employee employee)
            throws SQLException {

        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM availability WHERE employee_id = ?")) {

            deleteStatement.setInt(1, employee.getId());
            deleteStatement.executeUpdate();
        }

        String insertSql =
                "INSERT INTO availability(" +
                        "employee_id, day_of_week, morning_available, evening_available" +
                        ") VALUES (?, ?, ?, ?)";

        try (PreparedStatement insertStatement =
                     connection.prepareStatement(insertSql)) {

            for (Availability availability : employee.getAvailability()) {
                insertStatement.setInt(1, employee.getId());
                insertStatement.setInt(2, availability.getDay());
                insertStatement.setInt(
                        3,
                        availability.isMorningShift() ? 1 : 0
                );
                insertStatement.setInt(
                        4,
                        availability.isEveningShift() ? 1 : 0
                );
                insertStatement.addBatch();
            }

            insertStatement.executeBatch();
        }
    }

    private void replaceDriverData(Connection connection, Employee employee)
            throws SQLException {

        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM drivers WHERE employee_id = ?")) {

            deleteStatement.setInt(1, employee.getId());
            deleteStatement.executeUpdate();
        }

        if (!(employee instanceof Driver)) {
            return;
        }

        Driver driver = (Driver) employee;

        String sql =
                "INSERT INTO drivers(employee_id, license_type) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, driver.getId());
            statement.setString(2, driver.getLicenseType());
            statement.executeUpdate();
        }
    }

    private List<Role> loadRoles(Connection connection, int employeeId)
            throws SQLException {

        List<Role> roles = new ArrayList<>();

        String sql =
                "SELECT role FROM employee_roles WHERE employee_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    roles.add(Role.valueOf(resultSet.getString("role")));
                }
            }
        }

        return roles;
    }

    private void loadAvailability(Connection connection, Employee employee)
            throws SQLException {

        String sql =
                "SELECT day_of_week, morning_available, evening_available " +
                        "FROM availability WHERE employee_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employee.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employee.addAvailability(new Availability(
                            employee.getId(),
                            resultSet.getInt("day_of_week"),
                            resultSet.getInt("morning_available") == 1,
                            resultSet.getInt("evening_available") == 1
                    ));
                }
            }
        }
    }

    public List<Employee> findAll() {
    List<Integer> employeeIds = new ArrayList<>();

    String sql = "SELECT employee_id FROM employees ORDER BY employee_id";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            employeeIds.add(resultSet.getInt("employee_id"));
        }

    } catch (SQLException e) {
        throw new IllegalStateException("Could not load employees.", e);
    }

    List<Employee> employees = new ArrayList<>();

    for (Integer employeeId : employeeIds) {
        Employee employee = findById(employeeId);

        if (employee != null) {
            employees.add(employee);
        }
    }

    return employees;
}
}