package app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import adss.inventory.presentation.CLI;
import superli.presentation.EmployeeUI;
import superli.presentation.ManagerUI;
import superli.service.EmployeeService;
import superli.service.ShiftService;

public class Main {

    // 1. הטריק: מחלקה פנימית שמגנה על הסטרים המרכזי מפני סגירה
    private static class UnclosableInputStream extends InputStream {
        private final InputStream rawStream;

        public UnclosableInputStream(InputStream rawStream) {
            this.rawStream = rawStream;
        }

        @Override
        public int read() throws IOException {
            return rawStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return rawStream.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            // פקודת הסגירה מגיעה לכאן - ואנחנו פשוט מתעלמים ממנה!
            // זה שומר על הצינור המרכזי פתוח לרווחה עבור המניו הראשי.
        }
    }

    public static void main(String[] args) {
        // 2. מחליפים את הצינור של המערכת בצינור המוגן שלנו, ממש בשורה הראשונה!
        System.setIn(new UnclosableInputStream(System.in));

        adss.inventory.repository.DatabaseManager.setDatabasePath("company.db");
        System.setProperty("company.db.path", "company.db");
        adss.inventory.repository.DatabaseInitializer.initialize(); // איתחול המלאי
        superli.data.DatabaseManager.initializeDatabase();          // איתחול העובדים

        System.out.println("Initialization complete.\n");

        Scanner scanner = new Scanner(System.in);
        boolean systemRunning = true;

        while (systemRunning) {
            System.out.println("==========================================");
            System.out.println("    SUPERLI STORE MANAGEMENT SYSTEM");
            System.out.println("==========================================");
            System.out.println("1. Inventory Management Module");
            System.out.println("2. Human Resources (HR) Module");
            System.out.println("3. Exit System");
            System.out.print("Choose an option: ");

            String mainChoice = scanner.nextLine();

            switch (mainChoice) {
                case "1":
                    // Route to Inventory Module
                    launchInventoryModule();
                    break;
                case "2":
                    // Route to HR Module
                    launchHRModule(scanner);
                    break;
                case "3":
                    systemRunning = false;
                    System.out.println("Exiting Superli Store Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    /**
     * Launches the Inventory Module without modifying its original code.
     */
    private static void launchInventoryModule() {
        System.out.println("\n[ Entering Inventory Module... ]");
        CLI inventoryCLI = new CLI();
        inventoryCLI.start();
        System.out.println("[ Exited Inventory Module. Returning to Main System Menu. ]\n");
    }

    /**
     * Launches the HR Module using the exact logic provided by your partner.
     */
    private static void launchHRModule(Scanner scanner) {
        System.out.println("\n[ Entering Human Resources Module... ]");

        EmployeeService employeeService = new EmployeeService();
        ShiftService shiftService = new ShiftService();
        EmployeeUI employeeUI = new EmployeeUI(employeeService);
        ManagerUI managerUI = new ManagerUI(shiftService, employeeService);

        boolean hrRunning = true;
        while (hrRunning) {
            System.out.println("\n=== HR Main Menu ===");
            System.out.println("1. Employee");
            System.out.println("2. Manager");
            System.out.println("3. Return to Main System Menu");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    employeeUI.showMenu();
                    break;
                case "2":
                    if (managerUI.hasAccess(scanner)) {
                        managerUI.showMenu();
                    } else {
                        System.out.println("Access denied.");
                    }
                    break;
                case "3":
                    hrRunning = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
        System.out.println("[ Exited Human Resources Module. Returning to Main System Menu. ]\n");
    }
}