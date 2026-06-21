package superli.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.data.DatabaseManager;
import superli.domain.BankAccount;
import superli.domain.Employee;
import superli.domain.EmployeeTerms;
import superli.domain.Role;
import superli.domain.Shift;
import superli.domain.ShiftType;
import superli.domain.StoreBranch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShiftControllerDatabaseTest {

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearDatabase();
    }

    @Test
    public void shiftIsLoadedFromDatabaseByNewController() {
        StoreBranch branch =
                new StoreBranch(700, "Persistence Branch", "Beer Sheva");

        Employee employee = new Employee(
                7001,
                "Shift Manager",
                new BankAccount("Hapoalim", 12345, "Shift Manager"),
                new EmployeeTerms(
                        new Date(1700000000000L),
                        "Hourly",
                        0,
                        50,
                        10
                ),
                new ArrayList<Role>(List.of(Role.SHIFT_MANAGER)),
                branch
        );

        LocalDate date = LocalDate.of(2026, 6, 20);

        ShiftController firstController = new ShiftController();

        firstController.createShift(date, ShiftType.MORNING, branch);

        firstController.addRequiredRole(
                date,
                ShiftType.MORNING,
                Role.SHIFT_MANAGER,
                1,
                branch
        );

        firstController.assignEmployeeToShift(
                employee,
                date,
                ShiftType.MORNING,
                Role.SHIFT_MANAGER,
                true,
                branch
        );

        ShiftController secondController = new ShiftController();

        Shift loadedShift = secondController.getShift(
                date,
                ShiftType.MORNING,
                branch
        );

        assertNotNull(loadedShift);
        assertEquals(
                Integer.valueOf(1),
                loadedShift.getRequiredRoles().get(Role.SHIFT_MANAGER)
        );
        assertEquals(1, loadedShift.getAssignments().size());
        assertEquals(
                7001,
                loadedShift.getAssignments().get(0).getEmployee().getId()
        );
        assertTrue(loadedShift.isShiftValid());
    }
}