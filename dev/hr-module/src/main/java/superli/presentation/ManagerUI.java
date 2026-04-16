package superli.presentation;
import superli.domain.Employee;
import superli.domain.Role;
import superli.domain.Shift;
import superli.domain.ShiftAssignment;
import superli.domain.ShiftType;
import superli.service.EmployeeService;
import superli.service.ShiftService;
import java.util.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManagerUI {

    private final ShiftService shiftService;
    private final EmployeeService employeeService;
    private final Scanner scanner;
    private final String MANAGER_ACCESS_CODE = "9999";

    public ManagerUI(ShiftService shiftService ,EmployeeService employeeService) {
        this.shiftService = shiftService;
        this.employeeService = employeeService;
        this.scanner = new Scanner(System.in);
    }

    public boolean hasAccess(Scanner scanner) {
        System.out.print("Enter manager access code: ");
        String code = scanner.nextLine();
        return MANAGER_ACCESS_CODE.equals(code);
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. Create shift");
            System.out.println("2. Add required role");
            System.out.println("3. Assign employee to shift");
            System.out.println("4. Check if shift is valid");
            System.out.println("5. Add employee");
            System.out.println("6. View shifts");
            System.out.println("7. Exit");
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
                    addEmployee();
                    break;
                case "6":
                    viewShifts();
                    break;    
                case "7":
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
            Employee employee = employeeService.getEmployee(id); 
            if (employee == null) {
                System.out.println("Employee not found");
                return;
            }

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
        System.out.print("Enter role (SHIFT_MANAGER/CASHIER/STOCK_KEEPER): ");
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
    private void addEmployee() {
    try {
    
        System.out.print("Enter ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter bank name: ");
        String bankName = scanner.nextLine();

        System.out.print("Enter account number: ");
        int accountNumber = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter employment type (hourly/global): ");
        String employmentType = scanner.nextLine();

        System.out.print("Enter hourly salary: ");
        double hourlySalary = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter global salary: ");
        double globalSalary = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter vacation days: ");
        int vacationDays = Integer.parseInt(scanner.nextLine());
        List<Role> roles = new ArrayList<>();

        while (true) {
            System.out.print("Enter role (CASHIER / SHIFT_MANAGER / STOCK_KEEPER) or 'done': ");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("DONE")) 
                break;

            roles.add(Role.valueOf(input));
        }

        Employee employee = employeeService.addEmployee(id,name,bankName,accountNumber,roles,new Date(),employmentType,globalSalary,hourlySalary,vacationDays);

        if (employee != null) {
            System.out.println("Employee added successfully");
        } else {
            System.out.println("Employee already exists");
        }

    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}

private void viewShifts() {
    List<Shift> shifts = shiftService.getShifts();

    if (shifts.isEmpty()) {
        System.out.println("No shifts found.");
        return;
    }

    for (Shift shift : shifts) {
        System.out.println("---------------------------------");
        System.out.println("Date: " + shift.getDate());
        System.out.println("Shift type: " + shift.getShiftType());
        System.out.println("Required roles: " + shift.getRequiredRoles());
        System.out.println("Assignments:");

        if (shift.getAssignments().isEmpty()) {
            System.out.println("  No employees assigned.");
        } else {
            for (ShiftAssignment assignment : shift.getAssignments()) {
                System.out.println("  Employee ID: " + assignment.getEmployee().getId()
                        + " | Name: " + assignment.getEmployee().getName()
                        + " | Role: " + assignment.getRole());
            }
        }

        System.out.println("Valid: " + shift.isShiftValid());
    }
}
   
}