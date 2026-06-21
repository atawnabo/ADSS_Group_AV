package superli.service;

import java.time.LocalDate;
import java.util.List;

import superli.domain.Employee;
import superli.domain.Role;
import superli.domain.ShiftType;
import superli.domain.StoreBranch;

public class TransportMock {

    private EmployeeService employeeService;
    private ShiftService shiftService;

    public TransportMock(EmployeeService employeeService, ShiftService shiftService) {
        this.employeeService = employeeService;
        this.shiftService = shiftService;
    }

    public boolean canCreateTransport(LocalDate date,
                                      ShiftType shiftType,
                                      String requiredLicenseType,
                                      StoreBranch branch) {

        List<Integer> availableDrivers = employeeService.getAvailableDrivers(
                date,
                shiftType,
                requiredLicenseType,
                branch
        );

        boolean hasAvailableDriver = !availableDrivers.isEmpty();

        boolean hasStockKeeper = shiftService.hasStockKeeper(
                date,
                shiftType,
                branch
        );

        return hasAvailableDriver && hasStockKeeper;
    }

    public boolean createTransport(LocalDate date,
                               ShiftType shiftType,
                               String requiredLicenseType,
                               StoreBranch branch) {

    List<Integer> availableDrivers = employeeService.getAvailableDrivers(
            date,
            shiftType,
            requiredLicenseType,
            branch
    );

    if (availableDrivers.isEmpty()) {
        return false;
    }

    boolean hasStockKeeper = shiftService.hasStockKeeper(
            date,
            shiftType,
            branch
    );

    if (!hasStockKeeper) {
        return false;
    }

    for (Integer driverId : availableDrivers) {
        Employee selectedDriver = employeeService.getEmployee(driverId);

        if (selectedDriver == null) {
            continue;
        }

        try {
            shiftService.assignEmployeeToShift(
                    selectedDriver,
                    date,
                    shiftType,
                    Role.DRIVER,
                    false,
                    branch
            );

            return true;

        } catch (IllegalArgumentException e) {
            continue;
        }
    }

    return false;
}

    public List<Integer> requestAvailableDrivers(LocalDate date,
                                                 ShiftType shiftType,
                                                 String requiredLicenseType,
                                                 StoreBranch branch) {

        return employeeService.getAvailableDrivers(
                date,
                shiftType,
                requiredLicenseType,
                branch
        );
    }

    public boolean requestStockKeeperCheck(LocalDate date,
                                           ShiftType shiftType,
                                           StoreBranch branch) {

        return shiftService.hasStockKeeper(
                date,
                shiftType,
                branch
        );
    }
}