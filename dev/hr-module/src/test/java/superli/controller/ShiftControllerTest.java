package superli.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftControllerTest {

    private ShiftController shiftController;
    private LocalDate testDate;

    @BeforeEach
    public void setUp() {
        shiftController = new ShiftController();
        testDate = LocalDate.of(2026, 4, 15);
    }

    private Employee createEmployee(int id, String name, Role... roles) {
        BankAccount bankAccount = new BankAccount("Hapoalim", 1000 + id, name);
        EmployeeTerms employeeTerms = new EmployeeTerms(new Date(), "Hourly", 0, 50, 10);
        return new Employee(id, name, bankAccount, employeeTerms, new ArrayList<>(List.of(roles)));
    }

    private void addAvailability(Employee employee, LocalDate date, boolean morning, boolean evening) {
        int day = date.getDayOfWeek().getValue();
        employee.addAvailability(new Availability(employee.getId(), day, morning, evening));
    }

    @Test
    public void testAssignEmployeeToShiftSuccess() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        Employee employee = createEmployee(1, "Dana", Role.CASHIER);
        addAvailability(employee, testDate, true, false);

        assertDoesNotThrow(() ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        Shift shift = shiftController.getShift(testDate, ShiftType.MORNING);
        assertEquals(1, shift.getAssignments().size());
        assertEquals(employee, shift.getAssignments().get(0).getEmployee());
        assertEquals(Role.CASHIER, shift.getAssignments().get(0).getRole());

        assertEquals(1, employee.getShiftScheduled().size());
    }

    @Test
    public void testAssignEmployeeToShiftWithSpecialApprovalSucceedsEvenIfNotAvailable() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        Employee employee = createEmployee(2, "Noa", Role.CASHIER);
        // no availability added

        assertDoesNotThrow(() ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, true)
        );

        Shift shift = shiftController.getShift(testDate, ShiftType.MORNING);
        assertEquals(1, shift.getAssignments().size());
        assertEquals(1, employee.getShiftScheduled().size());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenEmployeeIsNull() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(null, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenShiftDoesNotExist() {
        Employee employee = createEmployee(3, "Yossi", Role.CASHIER);
        addAvailability(employee, testDate, true, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("Shift does not exist", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenEmployeeIsInactive() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        Employee employee = createEmployee(4, "Roni", Role.CASHIER);
        employee.setActive(false);
        addAvailability(employee, testDate, true, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("Employee is not active", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenEmployeeDoesNotHaveRole() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        Employee employee = createEmployee(5, "Amit", Role.STOCK_KEEPER);
        addAvailability(employee, testDate, true, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("Employee does not have this role", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenEmployeeIsNotAvailableAndNoSpecialApproval() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        Employee employee = createEmployee(6, "Maya", Role.CASHIER);
        // no matching availability
        employee.addAvailability(new Availability(employee.getId(), testDate.getDayOfWeek().getValue(), false, false));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("Employee is not available for this shift", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenRoleIsNotRequired() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        // no required role added

        Employee employee = createEmployee(7, "Gil", Role.CASHIER);
        addAvailability(employee, testDate, true, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("This role is not required for this shift", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenEmployeeAlreadyAssignedToSameShift() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 2);

        Employee employee = createEmployee(8, "Lior", Role.CASHIER);
        addAvailability(employee, testDate, true, false);

        shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("Employee is already assigned to this shift", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftThrowsWhenRoleCapacityIsFull() {
        shiftController.createShift(testDate, ShiftType.MORNING);
        shiftController.addRequiredRole(testDate, ShiftType.MORNING, Role.CASHIER, 1);

        Employee employee1 = createEmployee(9, "Shir", Role.CASHIER);
        Employee employee2 = createEmployee(10, "Adi", Role.CASHIER);

        addAvailability(employee1, testDate, true, false);
        addAvailability(employee2, testDate, true, false);

        shiftController.assignEmployeeToShift(employee1, testDate, ShiftType.MORNING, Role.CASHIER, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                shiftController.assignEmployeeToShift(employee2, testDate, ShiftType.MORNING, Role.CASHIER, false)
        );

        assertEquals("The amount of employees for this role is full !", exception.getMessage());
    }

    @Test
    public void testAssignEmployeeToShiftAddsAssignmentToShiftAndEmployee() {
        shiftController.createShift(testDate, ShiftType.EVENING);
        shiftController.addRequiredRole(testDate, ShiftType.EVENING, Role.STOCK_KEEPER, 1);

        Employee employee = createEmployee(11, "Tomer", Role.STOCK_KEEPER);
        addAvailability(employee, testDate, false, true);

        shiftController.assignEmployeeToShift(employee, testDate, ShiftType.EVENING, Role.STOCK_KEEPER, false);

        Shift shift = shiftController.getShift(testDate, ShiftType.EVENING);

        assertEquals(1, shift.getAssignments().size());
        assertEquals(1, employee.getShiftScheduled().size());

        ShiftAssignment shiftAssignment = shift.getAssignments().get(0);
        assertEquals(testDate, shiftAssignment.getDate());
        assertEquals(ShiftType.EVENING, shiftAssignment.getShiftType());
        assertEquals(Role.STOCK_KEEPER, shiftAssignment.getRole());
        assertEquals(employee, shiftAssignment.getEmployee());
    }
}