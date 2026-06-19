package superli.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftDomainTest {

    private final StoreBranch testBranch = new StoreBranch(1, "Main Branch", "beer sheva");

    private Employee createEmployee(int id, String name, Role... roles) {
        BankAccount bankAccount = new BankAccount("Hapoalim", 12345 + id, name);
        EmployeeTerms employeeTerms = new EmployeeTerms(new Date(), "Hourly", 0, 50, 10);
        return new Employee(id, name, bankAccount, employeeTerms, new ArrayList<>(List.of(roles)), testBranch);
    }

    @Test
    public void testShiftConstructorInitializesFieldsCorrectly() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);

        assertEquals(date, shift.getDate());
        assertEquals(ShiftType.MORNING, shift.getShiftType());
        assertNotNull(shift.getAssignments());
        assertNotNull(shift.getRequiredRoles());
        assertTrue(shift.getAssignments().isEmpty());
        assertTrue(shift.getRequiredRoles().isEmpty());
    }

    @Test
    public void testAddRequiredRoleAddsRoleToMap() {
        Shift shift = new Shift(LocalDate.of(2026, 4, 15), ShiftType.MORNING, testBranch);

        shift.addRequiredRole(Role.CASHIER, 2);

        assertEquals(1, shift.getRequiredRoles().size());
        assertEquals(2, shift.getRequiredRoles().get(Role.CASHIER));
    }

    @Test
    public void testAddRequiredRoleOverridesExistingAmount() {
        Shift shift = new Shift(LocalDate.of(2026, 4, 15), ShiftType.MORNING, testBranch);

        shift.addRequiredRole(Role.CASHIER, 1);
        shift.addRequiredRole(Role.CASHIER, 3);

        assertEquals(3, shift.getRequiredRoles().get(Role.CASHIER));
    }

    @Test
    public void testAddAssignmentAddsAssignmentToList() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);
        Employee employee = createEmployee(1, "Dana", Role.CASHIER);

        ShiftAssignment assignment =
                new ShiftAssignment(employee, Role.CASHIER, date, ShiftType.MORNING);

        shift.addAssignment(assignment);

        assertEquals(1, shift.getAssignments().size());
        assertEquals(assignment, shift.getAssignments().get(0));
    }

    @Test
    public void testHasManagerReturnsTrueWhenShiftManagerAssigned() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);
        Employee employee = createEmployee(2, "Noa", Role.SHIFT_MANAGER);

        ShiftAssignment assignment =
                new ShiftAssignment(employee, Role.SHIFT_MANAGER, date, ShiftType.MORNING);

        shift.addAssignment(assignment);

        assertTrue(shift.hasManager());
    }

    @Test
    public void testHasManagerReturnsFalseWhenNoShiftManagerAssigned() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);
        Employee employee = createEmployee(3, "Yossi", Role.CASHIER);

        ShiftAssignment assignment =
                new ShiftAssignment(employee, Role.CASHIER, date, ShiftType.MORNING);

        shift.addAssignment(assignment);

        assertFalse(shift.hasManager());
    }

    @Test
    public void testIsFullyStaffedReturnsFalseWhenRequiredRoleHasNoAssignments() {
        Shift shift = new Shift(LocalDate.of(2026, 4, 15), ShiftType.MORNING, testBranch);
        shift.addRequiredRole(Role.CASHIER, 1);

        assertFalse(shift.isFullyStaffed());
    }

    @Test
    public void testIsFullyStaffedReturnsTrueWhenAllRequiredRolesAreFilled() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);

        shift.addRequiredRole(Role.CASHIER, 1);
        shift.addRequiredRole(Role.SHIFT_MANAGER, 1);

        Employee cashier = createEmployee(4, "Amit", Role.CASHIER);
        Employee manager = createEmployee(5, "Roni", Role.SHIFT_MANAGER);

        shift.addAssignment(new ShiftAssignment(cashier, Role.CASHIER, date, ShiftType.MORNING));
        shift.addAssignment(new ShiftAssignment(manager, Role.SHIFT_MANAGER, date, ShiftType.MORNING));

        assertTrue(shift.isFullyStaffed());
    }

    @Test
    public void testIsFullyStaffedReturnsFalseWhenOneRequiredRoleIsMissing() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);

        shift.addRequiredRole(Role.CASHIER, 1);
        shift.addRequiredRole(Role.SHIFT_MANAGER, 1);

        Employee cashier = createEmployee(6, "Lior", Role.CASHIER);
        shift.addAssignment(new ShiftAssignment(cashier, Role.CASHIER, date, ShiftType.MORNING));

        assertFalse(shift.isFullyStaffed());
    }

    @Test
    public void testIsFullyStaffedReturnsTrueWhenRequiredAmountIsTwoAndTwoAssigned() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);

        shift.addRequiredRole(Role.CASHIER, 2);

        Employee cashier1 = createEmployee(7, "Maya", Role.CASHIER);
        Employee cashier2 = createEmployee(8, "Gil", Role.CASHIER);

        shift.addAssignment(new ShiftAssignment(cashier1, Role.CASHIER, date, ShiftType.MORNING));
        shift.addAssignment(new ShiftAssignment(cashier2, Role.CASHIER, date, ShiftType.MORNING));

        assertTrue(shift.isFullyStaffed());
    }

    @Test
    public void testIsFullyStaffedCountsAssignedRoleAndNotEmployeeCapabilities() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Shift shift = new Shift(date, ShiftType.MORNING, testBranch);

        shift.addRequiredRole(Role.CASHIER, 1);

        Employee employee = createEmployee(9, "Adi", Role.CASHIER, Role.SHIFT_MANAGER);

        shift.addAssignment(new ShiftAssignment(employee, Role.SHIFT_MANAGER, date, ShiftType.MORNING));

        assertFalse(shift.isFullyStaffed());
    }

    @Test
    public void testShiftAssignmentStoresAllFieldsCorrectly() {
        LocalDate date = LocalDate.of(2026, 4, 15);
        Employee employee = createEmployee(10, "Shir", Role.CASHIER);

        ShiftAssignment assignment =
                new ShiftAssignment(employee, Role.CASHIER, date, ShiftType.EVENING);

        assertEquals(employee, assignment.getEmployee());
        assertEquals(Role.CASHIER, assignment.getRole());
        assertEquals(date, assignment.getDate());
        assertEquals(ShiftType.EVENING, assignment.getShiftType());
    }
}