package superli.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.data.DatabaseManager;
import superli.domain.Employee;
import superli.domain.EmployeeTerms;
import superli.domain.Role;
import superli.domain.StoreBranch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeControllerDatabaseTest {

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
    }

    @Test
    public void employeeIsLoadedFromDatabaseByNewController() {
        EmployeeController firstController = new EmployeeController();

        StoreBranch branch =
                new StoreBranch(800, "Persistence Branch", "Beer Sheva");

        EmployeeTerms terms =
                new EmployeeTerms(
                        new Date(1700000000000L),
                        "Hourly",
                        0,
                        55,
                        12
                );

        firstController.addEmployee(
                8001,
                "Database Employee",
                "Hapoalim",
                123456,
                new ArrayList<Role>(
                        List.of(Role.CASHIER, Role.STOCK_KEEPER)
                ),
                terms,
                branch
        );

        EmployeeController secondController = new EmployeeController();

        Employee loadedEmployee = secondController.getEmployee(8001);

        assertNotNull(loadedEmployee);
        assertEquals(8001, loadedEmployee.getId());
        assertEquals("Database Employee", loadedEmployee.getName());
        assertEquals(800, loadedEmployee.getBranch().getBranchId());
        assertTrue(loadedEmployee.hasRole(Role.CASHIER));
        assertTrue(loadedEmployee.hasRole(Role.STOCK_KEEPER));
    }
}