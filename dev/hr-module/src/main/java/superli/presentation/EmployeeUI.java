package superli.presentation;

import superli.domain.Role;
import superli.domain.ShiftAssignment;
import superli.service.EmployeeService;

import java.util.List;
import java.util.Scanner;

public class EmployeeUI {

    private EmployeeService service;
    private Scanner scanner;
    private int loggedInId = -1;

    public EmployeeUI(EmployeeService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean running = true;

        while (running) {

           System.out.println("\n=== Employee Menu ===");

            if (loggedInId == -1) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
            } else {
                System.out.println("1. Enter availability");
                System.out.println("2. View my shifts");
                System.out.println("3. View my roles");
                System.out.println("4. Logout");
                System.out.println("5. Exit");
            }

            System.out.print("Choose option: ");
            String input = scanner.nextLine();

            if (loggedInId == -1) {
                switch (input) {
                    case "1":
                        register();
                        break;
                    case "2":
                        login();
                        break;
                    case "3":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            }
             else 
                
                {
                switch (input) {
                    case "1":
                        enterAvailability();
                        break;
                    case "2":
                        viewMyShifts();
                        break;
                    case "3":
                        viewMyRoles();
                        break;
                    case "4":
                        logout();
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            }
        }
    }

    private void register() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (service.register(id, password)) {
                System.out.println("Registered successfully");
            } else {
                System.out.println("Registration failed");
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    private void login() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (service.login(id, password)) {
                loggedInId = id;
                System.out.println("Login successful");
            } else {
                System.out.println("Login failed");
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    private void logout() {
        if (loggedInId == -1) {
            System.out.println("You are not logged in");
            return;
        }

        if (service.logout(loggedInId)) {
            loggedInId = -1;
            System.out.println("Logged out");
        } else {
            System.out.println("Logout failed");
        }
    }

    private void enterAvailability() {
    try {
        System.out.print("Enter day (1-7): ");
        int day = Integer.parseInt(scanner.nextLine());
        day = ((day + 6) % 7 == 0) ? 7 : (day + 6) % 7 ; // because in java monday is 1 ,..., sunday is 7

        if (day < 1 || day > 7) {
            System.out.println("Invalid day");
            return;
        }

        System.out.print("Morning shift? (yes/no): ");
        String inputMorning = scanner.nextLine();
        boolean morning = inputMorning.equalsIgnoreCase("yes");

        System.out.print("Evening shift? (yes/no): ");
        String inputEvening = scanner.nextLine();
        boolean evening = inputEvening.equalsIgnoreCase("yes");

        if (service.enterAvailability(loggedInId, day, morning, evening)) {
            System.out.println("Availability saved");
        } else {
            System.out.println("Failed");
        }

    } catch (Exception e) {
        System.out.println("Error");
    }
}

    private void viewMyShifts() {
        List<ShiftAssignment> shifts = service.viewScheduledShifts(loggedInId);

        if (shifts.isEmpty()) {
            System.out.println("No shifts");
        } else {
            for (ShiftAssignment s : shifts) {
              System.out.println("Date: " + s.getDate() +
                   " | Shift: " + s.getShiftType() +
                   " | Role: " + s.getRole());
            }
        }
    }

    private void viewMyRoles() {
        List<Role> roles = service.getEmployeeRoles(loggedInId);

        if (roles.isEmpty()) {
            System.out.println("No roles");
        } else {
            for (Role r : roles) {
                System.out.println(r);
            }
        }
    }
}