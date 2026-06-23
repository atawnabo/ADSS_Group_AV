package superli.data;

import superli.domain.Employee;
import superli.domain.Role;
import superli.domain.Shift;
import superli.domain.ShiftAssignment;
import superli.domain.ShiftType;
import superli.domain.StoreBranch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShiftDAO {

    private final BranchDAO branchDAO;
    private final EmployeeDAO employeeDAO;

    public ShiftDAO() {
        this.branchDAO = new BranchDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    public void save(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }

        if (shift.getBranch() == null) {
            throw new IllegalArgumentException("Shift must belong to a branch.");
        }

        branchDAO.save(shift.getBranch());

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                saveShiftRow(connection, shift);

                int shiftId = findShiftId(connection, shift);

                if (shiftId == -1) {
                    throw new SQLException("Could not find saved shift.");
                }

                replaceRequiredRoles(connection, shiftId, shift);
                replaceAssignments(connection, shiftId, shift);

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not save shift.", e);
        }
    }

    public Shift findByDetails(LocalDate date,
                               ShiftType shiftType,
                               StoreBranch branch) {

        if (date == null || shiftType == null || branch == null) {
            return null;
        }

        String sql =
                "SELECT s.shift_id, b.branch_id, b.name AS branch_name, " +
                "b.address AS branch_address " +
                "FROM shifts s " +
                "JOIN branches b ON s.branch_id = b.branch_id " +
                "WHERE s.shift_date = ? " +
                "AND s.shift_type = ? " +
                "AND s.branch_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, date.toString());
            statement.setString(2, shiftType.name());
            statement.setInt(3, branch.getBranchId());

            int shiftId;
            StoreBranch storedBranch;

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                shiftId = resultSet.getInt("shift_id");

                storedBranch = new StoreBranch(
                        resultSet.getInt("branch_id"),
                        resultSet.getString("branch_name"),
                        resultSet.getString("branch_address")
                );
            }

            Shift shift = new Shift(date, shiftType, storedBranch);

            loadRequiredRoles(connection, shiftId, shift);
            loadAssignments(connection, shiftId, shift);

            return shift;

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load shift.", e);
        }
    }

    public List<Shift> findAll() {
        List<Shift> shifts = new ArrayList<Shift>();

        String sql =
                "SELECT s.shift_id, s.shift_date, s.shift_type, " +
                "b.branch_id, b.name AS branch_name, b.address AS branch_address " +
                "FROM shifts s " +
                "JOIN branches b ON s.branch_id = b.branch_id " +
                "ORDER BY s.shift_date, s.shift_type, s.branch_id";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int shiftId = resultSet.getInt("shift_id");

                LocalDate date = LocalDate.parse(
                        resultSet.getString("shift_date")
                );

                ShiftType shiftType = ShiftType.valueOf(
                        resultSet.getString("shift_type")
                );

                StoreBranch branch = new StoreBranch(
                        resultSet.getInt("branch_id"),
                        resultSet.getString("branch_name"),
                        resultSet.getString("branch_address")
                );

                Shift shift = new Shift(date, shiftType, branch);

                loadRequiredRoles(connection, shiftId, shift);
                loadAssignments(connection, shiftId, shift);

                shifts.add(shift);
            }

            return shifts;

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load shifts.", e);
        }
    }

    private void saveShiftRow(Connection connection, Shift shift)
            throws SQLException {

        String sql =
                "INSERT INTO shifts(shift_date, shift_type, branch_id) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT(shift_date, shift_type, branch_id) DO NOTHING";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, shift.getDate().toString());
            statement.setString(2, shift.getShiftType().name());
            statement.setInt(3, shift.getBranch().getBranchId());

            statement.executeUpdate();
        }
    }

    private int findShiftId(Connection connection, Shift shift)
            throws SQLException {

        String sql =
                "SELECT shift_id FROM shifts " +
                "WHERE shift_date = ? " +
                "AND shift_type = ? " +
                "AND branch_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, shift.getDate().toString());
            statement.setString(2, shift.getShiftType().name());
            statement.setInt(3, shift.getBranch().getBranchId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("shift_id");
                }
            }
        }

        return -1;
    }

    private void replaceRequiredRoles(Connection connection,
                                      int shiftId,
                                      Shift shift) throws SQLException {

        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM shift_required_roles WHERE shift_id = ?")) {

            deleteStatement.setInt(1, shiftId);
            deleteStatement.executeUpdate();
        }

        if (shift.getRequiredRoles().isEmpty()) {
            return;
        }

        String sql =
                "INSERT INTO shift_required_roles(shift_id, role, required_amount) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Map.Entry<Role, Integer> entry :
                    shift.getRequiredRoles().entrySet()) {

                statement.setInt(1, shiftId);
                statement.setString(2, entry.getKey().name());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private void replaceAssignments(Connection connection,
                                    int shiftId,
                                    Shift shift) throws SQLException {

        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM shift_assignments WHERE shift_id = ?")) {

            deleteStatement.setInt(1, shiftId);
            deleteStatement.executeUpdate();
        }

        if (shift.getAssignments().isEmpty()) {
            return;
        }

        String sql =
                "INSERT INTO shift_assignments(shift_id, employee_id, role) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ShiftAssignment assignment : shift.getAssignments()) {
                Employee employee = assignment.getEmployee();

                if (employee == null) {
                    throw new SQLException("Assignment employee cannot be null.");
                }

                statement.setInt(1, shiftId);
                statement.setInt(2, employee.getId());
                statement.setString(3, assignment.getRole().name());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private void loadRequiredRoles(Connection connection,
                                   int shiftId,
                                   Shift shift) throws SQLException {

        String sql =
                "SELECT role, required_amount " +
                "FROM shift_required_roles " +
                "WHERE shift_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shiftId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Role role = Role.valueOf(resultSet.getString("role"));
                    int amount = resultSet.getInt("required_amount");

                    shift.addRequiredRole(role, amount);
                }
            }
        }
    }

    private void loadAssignments(Connection connection,
                                 int shiftId,
                                 Shift shift) throws SQLException {

        String sql =
                "SELECT employee_id, role " +
                "FROM shift_assignments " +
                "WHERE shift_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shiftId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int employeeId = resultSet.getInt("employee_id");
                    Role role = Role.valueOf(resultSet.getString("role"));

                    Employee employee = employeeDAO.findById(employeeId);

                    if (employee != null) {
                        ShiftAssignment assignment = new ShiftAssignment(
                                employee,
                                role,
                                shift.getDate(),
                                shift.getShiftType()
                        );

                        shift.addAssignment(assignment);
                        employee.addShift(assignment);
                    }
                }
            }
        }
    }
}