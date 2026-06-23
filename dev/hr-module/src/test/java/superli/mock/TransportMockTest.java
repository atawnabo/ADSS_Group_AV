package superli.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.data.DatabaseManager;
import superli.domain.Employee;
import superli.domain.Role;
import superli.domain.Shift;
import superli.domain.ShiftType;
import superli.domain.StoreBranch;
import superli.service.EmployeeService;
import superli.service.ShiftService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransportMockTest {

    private EmployeeService employeeService;
    private ShiftService shiftService;
    private TransportMock transportMock;
    private StoreBranch branch;
    private LocalDate date;

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearDatabase();

        employeeService = new EmployeeService();
        shiftService = new ShiftService();
        transportMock = new TransportMock(employeeService, shiftService);

        branch = new StoreBranch(900, "Mock Branch", "Beer Sheva");
        date = LocalDate.of(2026, 6, 25);
    }

    @Test
    public void transportCanBeCreatedWhenDriverAndStockKeeperExist() {
        Employee driver = employeeService.addDriver(
                9001,
                "Mock Driver",
                "Hapoalim",
                11111,
                new ArrayList<Role>(List.of(Role.DRIVER)),
                new Date(),
                "Hourly",
                0,
                60,
                10,
                branch,
                "C1"
        );

        int day = date.getDayOfWeek().getValue();
        employeeService.enterAvailability(driver.getId(), day, true, false);

        Employee stockKeeper = employeeService.addEmployee(
                9002,
                "Mock Stock Keeper",
                "Hapoalim",
                22222,
                new ArrayList<Role>(List.of(Role.STOCK_KEEPER)),
                new Date(),
                "Hourly",
                0,
                50,
                10,
                branch
        );

        shiftService.createShift(date, ShiftType.MORNING, branch);

        shiftService.addRequiredRole(
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                1,
                branch
        );

        shiftService.assignEmployeeToShift(
                stockKeeper,
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                true,
                branch
        );

        boolean result = transportMock.canCreateTransport(
                date,
                ShiftType.MORNING,
                "C1",
                branch
        );

        assertTrue(result);
    }

    @Test
    public void transportCannotBeCreatedWithoutAvailableDriver() {
        Employee stockKeeper = employeeService.addEmployee(
                9010,
                "Only Stock Keeper",
                "Hapoalim",
                33333,
                new ArrayList<Role>(List.of(Role.STOCK_KEEPER)),
                new Date(),
                "Hourly",
                0,
                50,
                10,
                branch
        );

        shiftService.createShift(date, ShiftType.MORNING, branch);

        shiftService.addRequiredRole(
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                1,
                branch
        );

        shiftService.assignEmployeeToShift(
                stockKeeper,
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                true,
                branch
        );

        boolean result = transportMock.canCreateTransport(
                date,
                ShiftType.MORNING,
                "C1",
                branch
        );

        assertFalse(result);
    }

    @Test
    public void transportCannotBeCreatedWithoutStockKeeper() {
        Employee driver = employeeService.addDriver(
                9020,
                "Driver Without Stock Keeper",
                "Hapoalim",
                44444,
                new ArrayList<Role>(List.of(Role.DRIVER)),
                new Date(),
                "Hourly",
                0,
                60,
                10,
                branch,
                "C1"
        );

        int day = date.getDayOfWeek().getValue();
        employeeService.enterAvailability(driver.getId(), day, true, false);

        shiftService.createShift(date, ShiftType.MORNING, branch);

        boolean result = transportMock.canCreateTransport(
                date,
                ShiftType.MORNING,
                "C1",
                branch
        );

        assertFalse(result);
    }

    @Test
    public void createTransportAssignsDriverToShift() {
        Employee driver = employeeService.addDriver(
                9030,
                "Assigned Driver",
                "Hapoalim",
                55555,
                new ArrayList<Role>(List.of(Role.DRIVER)),
                new Date(),
                "Hourly",
                0,
                60,
                10,
                branch,
                "C1"
        );

        int day = date.getDayOfWeek().getValue();
        employeeService.enterAvailability(driver.getId(), day, true, false);

        Employee stockKeeper = employeeService.addEmployee(
                9031,
                "Assigned Stock Keeper",
                "Hapoalim",
                66666,
                new ArrayList<Role>(List.of(Role.STOCK_KEEPER)),
                new Date(),
                "Hourly",
                0,
                50,
                10,
                branch
        );

        shiftService.createShift(date, ShiftType.MORNING, branch);

        shiftService.addRequiredRole(
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                1,
                branch
        );

        shiftService.addRequiredRole(
                date,
                ShiftType.MORNING,
                Role.DRIVER,
                1,
                branch
        );

        shiftService.assignEmployeeToShift(
                stockKeeper,
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                true,
                branch
        );

        boolean result = transportMock.createTransport(
                date,
                ShiftType.MORNING,
                "C1",
                branch
        );

        assertTrue(result);

        Shift shift = shiftService.getShift(date, ShiftType.MORNING, branch);

        assertNotNull(shift);
        assertEquals(2, shift.getAssignments().size());

        boolean driverAssigned = false;

        for (int i = 0; i < shift.getAssignments().size(); i++) {
            if (shift.getAssignments().get(i).getEmployee().getId() == driver.getId()
                    && shift.getAssignments().get(i).getRole() == Role.DRIVER) {
                driverAssigned = true;
            }
        }

        assertTrue(driverAssigned);
    }
}