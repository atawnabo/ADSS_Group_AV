package superli.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.domain.BankAccount;
import superli.domain.Employee;
import superli.domain.EmployeeTerms;
import superli.domain.Role;
import superli.domain.Shift;
import superli.domain.ShiftAssignment;
import superli.domain.ShiftType;
import superli.domain.StoreBranch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShiftDAOTest {

    private ShiftDAO shiftDAO;
    private EmployeeDAO employeeDAO;

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();

        shiftDAO = new ShiftDAO();
        employeeDAO = new EmployeeDAO();
    }

    @Test
    public void saveAndFindShiftSuccessfully() {
        StoreBranch branch =
                new StoreBranch(920, "Shift Test Branch", "Tel Aviv");

        List<Role> roles = new ArrayList<Role>();
        roles.add(Role.SHIFT_MANAGER);

        Employee employee = new Employee(
                9201,
                "Shift Manager",
                new BankAccount("Leumi", 12345, "Shift Manager"),
                new EmployeeTerms(
                        new Date(1700000000000L),
                        "Hourly",
                        0,
                        55,
                        10
                ),
                roles,
                branch
        );

        employeeDAO.save(employee);

        LocalDate date = LocalDate.of(2026, 6, 15);

        Shift shift = new Shift(date, ShiftType.MORNING, branch);
        shift.addRequiredRole(Role.SHIFT_MANAGER, 1);

        ShiftAssignment assignment = new ShiftAssignment(
                employee,
                Role.SHIFT_MANAGER,
                date,
                ShiftType.MORNING
        );

        shift.addAssignment(assignment);

        shiftDAO.save(shift);

        Shift loadedShift = shiftDAO.findByDetails(
                date,
                ShiftType.MORNING,
                branch
        );

        assertNotNull(loadedShift);
        assertEquals(date, loadedShift.getDate());
        assertEquals(ShiftType.MORNING, loadedShift.getShiftType());
        assertEquals(920, loadedShift.getBranch().getBranchId());

        assertEquals(
                Integer.valueOf(1),
                loadedShift.getRequiredRoles().get(Role.SHIFT_MANAGER)
        );

        assertEquals(1, loadedShift.getAssignments().size());

        assertEquals(
                9201,
                loadedShift.getAssignments().get(0).getEmployee().getId()
        );

        assertTrue(loadedShift.isShiftValid());
    }
}