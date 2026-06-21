package superli.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.data.DatabaseManager;
import superli.domain.BankAccount;
import superli.domain.Employee;
import superli.domain.EmployeeTerms;
import superli.domain.Role;
import superli.domain.ShiftType;
import superli.domain.StoreBranch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StockKeeperDatabaseTest {

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearDatabase();
    }

    @Test
    public void assignedActiveStockKeeperIsFoundAfterLoadingFromDatabase() {
        StoreBranch branch =
                new StoreBranch(500, "Warehouse Branch", "Beer Sheva");

        List<Role> roles = new ArrayList<Role>();
        roles.add(Role.STOCK_KEEPER);

        Employee stockKeeper = new Employee(
                5001,
                "Warehouse Worker",
                new BankAccount("Hapoalim", 11111, "Warehouse Worker"),
                new EmployeeTerms(
                        new Date(1700000000000L),
                        "Hourly",
                        0,
                        50,
                        10
                ),
                roles,
                branch
        );

        LocalDate date = LocalDate.of(2026, 6, 25);

        ShiftController firstController = new ShiftController();

        firstController.createShift(date, ShiftType.MORNING, branch);

        firstController.addRequiredRole(
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                1,
                branch
        );

        firstController.assignEmployeeToShift(
                stockKeeper,
                date,
                ShiftType.MORNING,
                Role.STOCK_KEEPER,
                true,
                branch
        );

        ShiftController secondController = new ShiftController();

        assertTrue(secondController.hasStockKeeper(
                date,
                ShiftType.MORNING,
                branch
        ));
    }
}