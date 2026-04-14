package superli.presentation;

import superli.controller.EmployeeController;
import superli.domain.Employee;
import superli.domain.Role;
import superli.domain.ShiftType;
import superli.service.EmployeeService;
import superli.service.ShiftService;

import java.time.LocalDate;
import java.util.Scanner;

public class ManagerUI {

    private final ShiftService shiftService;
    private final EmployeeController employeeController; // we should change it to employeeService when it is ready
    private final Scanner scanner;

    public ManagerUI(ShiftService shiftService ,EmployeeController employeeController) {
        this.shiftService = shiftService;
        this.employeeController = employeeController; // we should change it to employeeService when it is ready
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. Create shift");
            System.out.println("2. Add required role");
            System.out.println("3. Assign employee to shift");
            System.out.println("4. Check if shift is valid");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    createShiftUI();
                    break;
                case "2":
                    addRequiredRoleUI();
                    break;
                case "3":
                    assignEmployeeUI();
                    break;
                case "4":
                    checkShiftValidityUI();
                    break;
                case "5":
                    running = false;
                    System.out.println("Exiting Manager Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void createShiftUI() {
        try {
            LocalDate date = readDate();
            ShiftType shiftType = readShiftType();

            shiftService.createShift(date, shiftType);
            System.out.println("Shift created successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addRequiredRoleUI() {
        try {
            LocalDate date = readDate();
            ShiftType shiftType = readShiftType();
            Role role = readRole();
            int amount = readPositiveInt("Enter required amount: ");

            shiftService.addRequiredRole(date, shiftType, role, amount);
            System.out.println("Required role added successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void assignEmployeeUI() {
        try {
            LocalDate date = readDate();
            ShiftType shiftType = readShiftType();
            Role role = readRole();

            System.out.print("Enter employee id: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee employee = employeeController.getEmployee(id); // we should change it to employeeService when it is ready

            shiftService.assignEmployeeToShift(employee, date, shiftType, role);
            System.out.println("Employee assigned successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkShiftValidityUI() {
        try {
            LocalDate date = readDate();
            ShiftType shiftType = readShiftType();

            boolean isValid = shiftService.getShift(date ,shiftType).isShiftValid();

            if (isValid) {
                System.out.println("Shift is valid.");
            } else {
                System.out.println("Shift is not valid.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private LocalDate readDate() {
        System.out.print("Enter date (yyyy-mm-dd): ");
        String input = scanner.nextLine();
        return LocalDate.parse(input);
    }

    private ShiftType readShiftType() {
        System.out.print("Enter shift type (MORNING/EVENING): ");
        String input = scanner.nextLine().trim().toUpperCase();
        return ShiftType.valueOf(input);
    }

    private Role readRole() {
        System.out.print("Enter role: ");
        String input = scanner.nextLine().trim().toUpperCase();
        return Role.valueOf(input);
    }

    private int readPositiveInt(String message) {
        System.out.print(message);
        int value = Integer.parseInt(scanner.nextLine());

        if (value <= 0) {
            throw new IllegalArgumentException("Value must be positive.");
        }

        return value;
    }

   
    
}