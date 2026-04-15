package superli.presentation;
import superli.service.EmployeeService;
import superli.service.ShiftService;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {

        EmployeeService employeeService = new EmployeeService();
        ShiftService shiftService = new ShiftService();

        EmployeeUI employeeUI = new EmployeeUI(employeeService);
        ManagerUI managerUI = new ManagerUI(shiftService, employeeService);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Employee");
            System.out.println("2. Manager");
            System.out.println("3. Exit");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    employeeUI.showMenu();
                    break;
                case "2":
                    managerUI.showMenu();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }
}