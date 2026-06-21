package superli.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.domain.BankAccount;
import superli.domain.Driver;
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

public class DriverDAOTest {

    private EmployeeDAO employeeDAO;

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearDatabase();
        employeeDAO = new EmployeeDAO();
    }

    @Test
    public void saveAndLoadDriverSuccessfully() {
        StoreBranch branch =
                new StoreBranch(600, "Driver Branch", "Tel Aviv");

        Driver driver = new Driver(
                6001,
                "David Driver",
                new BankAccount("Leumi", 98765, "David Driver"),
                new EmployeeTerms(
                        new Date(1700000000000L),
                        "Hourly",
                        0,
                        60,
                        10
                ),
                new ArrayList<Role>(List.of(Role.DRIVER)),
                branch,
                "C"
        );

        employeeDAO.save(driver);

        Employee loadedEmployee = employeeDAO.findById(6001);

        assertNotNull(loadedEmployee);
        assertTrue(loadedEmployee instanceof Driver);

        Driver loadedDriver = (Driver) loadedEmployee;

        assertEquals("C", loadedDriver.getLicenseType());
        assertTrue(loadedDriver.hasRole(Role.DRIVER));
        assertEquals(600, loadedDriver.getBranch().getBranchId());
    }
}