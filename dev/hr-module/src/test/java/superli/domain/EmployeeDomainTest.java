package superli.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDomainTest {

    private Employee createEmployee(int id, String name, Role... roles) {
        BankAccount bankAccount = new BankAccount("Hapoalim", 1000 + id, name);
        EmployeeTerms employeeTerms = new EmployeeTerms(new Date(), "Hourly", 0, 50, 10);
        StoreBranch testBranch = new StoreBranch(1, "Main branch", "beer sheva");
        return new Employee(id, name, bankAccount, employeeTerms, new ArrayList<>(List.of(roles)), testBranch);
    }

    private ShiftAssignment createAssignment(Employee employee, Role role, LocalDate date, ShiftType shiftType) {
        return new ShiftAssignment(employee, role, date, shiftType);
    }

    @Test
    public void testEmployeeConstructorInitializesFieldsCorrectly() {
        Employee employee = createEmployee(1, "Dana", Role.CASHIER);

        assertEquals(1, employee.getId());
        assertEquals("Dana", employee.getName());
        assertNotNull(employee.getBankDetails());
        assertNotNull(employee.getEmployeeTerms());
        assertNotNull(employee.getRoles());
        assertNotNull(employee.getShiftScheduled());
        assertNotNull(employee.getAvailability());
        assertTrue(employee.isActive());
        assertTrue(employee.hasRole(Role.CASHIER));
    }

    @Test
    public void testHasRoleReturnsTrueWhenEmployeeHasRole() {
        Employee employee = createEmployee(2, "Noa", Role.CASHIER, Role.SHIFT_MANAGER);

        assertTrue(employee.hasRole(Role.CASHIER));
        assertTrue(employee.hasRole(Role.SHIFT_MANAGER));
    }

    @Test
    public void testHasRoleReturnsFalseWhenEmployeeDoesNotHaveRole() {
        Employee employee = createEmployee(3, "Yossi", Role.CASHIER);

        assertFalse(employee.hasRole(Role.STOCK_KEEPER));
    }

    @Test
    public void testAddRoleAddsNewRole() {
        Employee employee = createEmployee(4, "Roni", Role.CASHIER);

        employee.addRole(Role.STOCK_KEEPER);

        assertTrue(employee.hasRole(Role.STOCK_KEEPER));
        assertEquals(2, employee.getRoles().size());
    }

    @Test
    public void testAddRoleDoesNotDuplicateExistingRole() {
        Employee employee = createEmployee(5, "Amit", Role.CASHIER);

        employee.addRole(Role.CASHIER);

        assertEquals(1, employee.getRoles().size());
    }

    @Test
    public void testRemoveRoleRemovesExistingRole() {
        Employee employee = createEmployee(6, "Maya", Role.CASHIER, Role.STOCK_KEEPER);

        employee.removeRole(Role.STOCK_KEEPER);

        assertFalse(employee.hasRole(Role.STOCK_KEEPER));
        assertTrue(employee.hasRole(Role.CASHIER));
    }

    @Test
    public void testSetActiveChangesEmployeeStatus() {
        Employee employee = createEmployee(7, "Gil", Role.CASHIER);

        employee.setActive(false);

        assertFalse(employee.isActive());
    }

    @Test
    public void testAddAvailabilityAddsAvailabilitySuccessfully() {
        Employee employee = createEmployee(8, "Lior", Role.CASHIER);
        Availability availability = new Availability(employee.getId(), 3, true, false);

        boolean result = employee.addAvailability(availability);

        assertTrue(result);
        assertEquals(1, employee.getAvailability().size());
        assertEquals(availability, employee.getAvailability().get(0));
    }

    @Test
    public void testAddAvailabilityReturnsFalseWhenAvailabilityIsNull() {
        Employee employee = createEmployee(9, "Shir", Role.CASHIER);

        boolean result = employee.addAvailability(null);

        assertFalse(result);
        assertTrue(employee.getAvailability().isEmpty());
    }

    @Test
    public void testCanWorkReturnsTrueWhenAvailabilityListIsEmpty() {
        Employee employee = createEmployee(10, "Adi", Role.CASHIER);

        assertTrue(employee.canWork(3, true, false));
    }

    @Test
    public void testCanWorkReturnsTrueForMatchingMorningAvailability() {
        Employee employee = createEmployee(11, "Tomer", Role.CASHIER);
        employee.addAvailability(new Availability(employee.getId(), 3, true, false));

        assertTrue(employee.canWork(3, true, false));
    }

    @Test
    public void testCanWorkReturnsTrueForMatchingEveningAvailability() {
        Employee employee = createEmployee(12, "Neta", Role.CASHIER);
        employee.addAvailability(new Availability(employee.getId(), 4, false, true));

        assertTrue(employee.canWork(4, false, true));
    }

    @Test
    public void testCanWorkReturnsFalseForWrongDay() {
        Employee employee = createEmployee(13, "Omer", Role.CASHIER);
        employee.addAvailability(new Availability(employee.getId(), 3, false, false));

        assertFalse(employee.canWork(3, true, false));
    }

    @Test
    public void testCanWorkReturnsFalseForWrongShiftType() {
        Employee employee = createEmployee(14, "Yael", Role.CASHIER);
        employee.addAvailability(new Availability(employee.getId(), 3, true, false));

        assertFalse(employee.canWork(3, false, true));
    }

    @Test
    public void testRemoveShiftRemovesExistingAssignment() {
        Employee employee = createEmployee(19, "Moran", Role.CASHIER);
        ShiftAssignment assignment = createAssignment(
                employee,
                Role.CASHIER,
                LocalDate.of(2026, 4, 15),
                ShiftType.MORNING
        );

        employee.addShift(assignment);
        boolean result = employee.removeShift(assignment);

        assertTrue(result);
        assertTrue(employee.getShiftScheduled().isEmpty());
    }

    @Test
    public void testRemoveShiftReturnsFalseWhenAssignmentIsNull() {
        Employee employee = createEmployee(20, "Ofir", Role.CASHIER);

        boolean result = employee.removeShift(null);

        assertFalse(result);
    }

    @Test
    public void testResetForNewWeekClearsScheduledShiftsAndAvailability() {
        Employee employee = createEmployee(21, "Ariel", Role.CASHIER);

        ShiftAssignment assignment = createAssignment(
                employee,
                Role.CASHIER,
                LocalDate.of(2026, 4, 15),
                ShiftType.MORNING
        );

        employee.addShift(assignment);
        employee.addAvailability(new Availability(employee.getId(), 3, true, false));

        employee.resetForNewWeek();

        assertTrue(employee.getShiftScheduled().isEmpty());
        assertTrue(employee.getAvailability().isEmpty());
    }
}