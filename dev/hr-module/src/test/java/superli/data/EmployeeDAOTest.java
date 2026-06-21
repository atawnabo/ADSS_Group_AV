package superli.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.domain.Availability;
import superli.domain.BankAccount;
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

public class EmployeeDAOTest {

    private EmployeeDAO employeeDAO;

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
        employeeDAO = new EmployeeDAO();
    }

    @Test
    public void saveAndFindEmployeeSuccessfully() {
        StoreBranch branch =
                new StoreBranch(900, "Database Test Branch", "Beer Sheva");

        BankAccount bankAccount =
                new BankAccount("Hapoalim", 55555, "Dana");

        EmployeeTerms terms =
                new EmployeeTerms(
                        new Date(1700000000000L),
                        "Hourly",
                        0,
                        50,
                        10
                );

        Employee employee = new Employee(
                9001,
                "Dana",
                bankAccount,
                terms,
                new ArrayList<Role>(
                        List.of(Role.CASHIER, Role.STOCK_KEEPER)
                ),
                branch
        );

        employee.addAvailability(
                new Availability(9001, 1, true, false)
        );

        employeeDAO.save(employee);

        Employee loadedEmployee = employeeDAO.findById(9001);

        assertNotNull(loadedEmployee);
        assertEquals(9001, loadedEmployee.getId());
        assertEquals("Dana", loadedEmployee.getName());
        assertEquals(900, loadedEmployee.getBranch().getBranchId());
        assertEquals("Database Test Branch", loadedEmployee.getBranch().getName());
        assertTrue(loadedEmployee.hasRole(Role.CASHIER));
        assertTrue(loadedEmployee.hasRole(Role.STOCK_KEEPER));
        assertEquals(1, loadedEmployee.getAvailability().size());
    }
}