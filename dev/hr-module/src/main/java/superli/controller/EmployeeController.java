package superli.controller;

import superli.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeController {

    private Map<Integer, Employee> employees;

    public EmployeeController() {
        employees = new HashMap<>();
    }

    public Employee addEmployee(int id,String name,String bankName, int accountNumber,List<Role> rolesList,EmployeeTerms employeeTerms, StoreBranch branch) {
          if (branch == null) {
        throw new IllegalArgumentException("Branch is required");
             }
        if (employees.containsKey(id)) {
            System.out.println("Employee already exists");
            return null;
        }

        BankAccount bankDetails = new BankAccount(bankName, accountNumber, name);
        Employee employee = new Employee(id, name, bankDetails, employeeTerms, rolesList, branch);
        employees.put(id, employee);
        branch.addEmployee(employee);
        return employee;

    }
    


    public Employee addEmployee(int id, String name,String bankName,int accountNumber,List<Role> rolesList,Date startDate,String employmentType,double globalSalary,double hourlySalary,int vacationDays,StoreBranch branch) {
          if (branch == null) {
    throw new IllegalArgumentException("Branch is required");
                    }
        EmployeeTerms employeeTerms = new EmployeeTerms(startDate,employmentType,globalSalary,hourlySalary,vacationDays);

          return addEmployee(id, name, bankName, accountNumber, rolesList, employeeTerms, branch);
    }

    public Employee getEmployee(int id) {
        return employees.get(id);
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    public boolean removeEmployee(int id) {
        Employee employee = employees.get(id);

        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        employee.setActive(false);
        return true;
    }

    public boolean enterAvailability(int employeeId, int day, boolean morning, boolean evening) {
        Employee employee = employees.get(employeeId);
        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }
        List<Availability> availabilityList = employee.getAvailability();

      for (int i = 0; i < availabilityList.size(); i++) {
        if (availabilityList.get(i).getDay() == day) {
            availabilityList.remove(i);
                  i--; 
    }
}
        Availability availability = new Availability(employeeId, day, morning, evening);
        return employee.addAvailability(availability);
    }

    public List<ShiftAssignment> viewScheduledShifts(int employeeId) {
        Employee employee = employees.get(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return new ArrayList<>();
        }

        return new ArrayList<>(employee.getShiftScheduled());
    }

    public void resetForNewWeek() {
        for (Employee employee : employees.values()) {
            employee.resetForNewWeek();
        }
    }

    public boolean addRoleToEmployee(int employeeId, Role role) {
        Employee employee = employees.get(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        employee.addRole(role);
        return true;
    }

    public boolean removeRoleFromEmployee(int employeeId, Role role) {
        Employee employee = employees.get(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        employee.removeRole(role);
        return true;
    }

    public List<Integer> getEmployeesByRole(Role role) {
        List<Integer> employeesByRole = new ArrayList<>();

        for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
            Integer employeeId = entry.getKey();
            Employee employee = entry.getValue();

            if (employee.isActive() && employee.hasRole(role)) {
                employeesByRole.add(employeeId);
            }
        }

        return employeesByRole;
    }

    public List<Integer> getAvailableEmployees(Role selectedRole,int dayOfWeek,boolean isMorning,boolean isEvening, StoreBranch branch) {
        List<Integer> availableEmployees = new ArrayList<>();
        for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
            Integer employeeId = entry.getKey();
            Employee employee = entry.getValue();

           if (employee.isActive()
            && employee.hasRole(selectedRole)
            && employee.canWork(dayOfWeek, isMorning, isEvening)
            && employee.getBranch() != null
             && branch != null
             && employee.getBranch().getBranchId() == branch.getBranchId()) {
                
                availableEmployees.add(employeeId);
            }
        }

        return availableEmployees;
    }

    public boolean hasAvailableShiftManager(int day, boolean isMorning, boolean isEvening, StoreBranch branch) {
        return !getAvailableEmployees(Role.SHIFT_MANAGER, day, isMorning, isEvening, branch).isEmpty();
    }

    public List<Role> getEmployeeRoles(int employeeId) {
        Employee employee = employees.get(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return new ArrayList<>();
        }

      return new ArrayList<>(employee.getRoles());
    }

    public EmployeeTerms getEmployeeTerms(int employeeId) {
        Employee employee = employees.get(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return null;
        }

        return employee.getEmployeeTerms();
    }

    public boolean register(int id, String password) {
        Employee employee = employees.get(id);

        if (employee == null || password == null || password.isEmpty()) {
            return false;
        }

        employee.setPassword(password);
        return true;
    }

    public boolean login(int id, String password) {
        Employee employee = employees.get(id);
        if (employee == null) {
            return false;
        }

        if (!employee.checkPassword(password)) {
            return false;
        }

        employee.setLoggedIn(true);
        return true;
    }

    public boolean logout(int id) {
        Employee employee = employees.get(id);

        if (employee == null) {
            return false;
        }

        employee.setLoggedIn(false);
        return true;
    }



  public Employee addDriver(int id, String name, String bankName, int accountNumber,
                          List<Role> rolesList, EmployeeTerms employeeTerms,
                          StoreBranch branch, String licenseType) {

    if (branch == null) {
        throw new IllegalArgumentException("Branch is required");
    }

    if (licenseType == null || licenseType.isEmpty()) {
        throw new IllegalArgumentException("License type is required");
    }

    if (employees.containsKey(id)) {
        System.out.println("Employee already exists");
        return null;
    }

    List<Role> driverRoles = new ArrayList<>(rolesList);

    if (!driverRoles.contains(Role.DRIVER)) {
        driverRoles.add(Role.DRIVER);
    }

    BankAccount bankDetails = new BankAccount(bankName, accountNumber, name);

    Driver driver = new Driver(
            id,
            name,
            bankDetails,
            employeeTerms,
            driverRoles,
            branch,
            licenseType
    );

    employees.put(id, driver);
    branch.addEmployee(driver);

    return driver;
}


    public List<Integer> getAvailableDrivers(LocalDate date,
                                          ShiftType shiftType,
                                          String licenseType,
                                          StoreBranch branch) {

    if (date == null) {
        throw new IllegalArgumentException("Date is required");
    }

    if (shiftType == null) {
        throw new IllegalArgumentException("Shift type is required");
    }

    if (licenseType == null || licenseType.isEmpty()) {
        throw new IllegalArgumentException("License type is required");
    }

    if (branch == null) {
        throw new IllegalArgumentException("Branch is required");
    }

    List<Integer> availableDrivers = new ArrayList<>();

    boolean isMorning = shiftType == ShiftType.MORNING;
    boolean isEvening = shiftType == ShiftType.EVENING;
    int day = date.getDayOfWeek().getValue();

    for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
        Integer employeeId = entry.getKey();
        Employee employee = entry.getValue();

        if (!employee.isActive()) {
            continue;
        }

        if (!employee.hasRole(Role.DRIVER)) {
            continue;
        }

        if (employee.getBranch() == null ||
                employee.getBranch().getBranchId() != branch.getBranchId()) {
            continue;
        }

        if (!(employee instanceof Driver)) {
            continue;
        }

        Driver driver = (Driver) employee;

        if (driver.getLicenseType() == null ||
                !driver.getLicenseType().equals(licenseType)) {
            continue;
        }

        if (!driver.canWork(day, isMorning, isEvening)) {
            continue;
        }

        availableDrivers.add(employeeId);
    }

    return availableDrivers;
}
public boolean updateEmployee(int id,
                              String name,
                              String bankName,
                              int accountNumber,
                              EmployeeTerms employeeTerms,
                              StoreBranch branch) {
    Employee employee = employees.get(id);

    if (employee == null) {
        return false;
    }

    if (branch == null) {
        throw new IllegalArgumentException("Branch is required");
    }

    BankAccount bankDetails = new BankAccount(bankName, accountNumber, name);
    employee.updateDetails(name, bankDetails, employeeTerms);

StoreBranch oldBranch = employee.getBranch();
if (oldBranch != null && oldBranch.getBranchId() != branch.getBranchId()) {
    oldBranch.removeEmployee(employee);
}

employee.assignToBranch(branch);
branch.addEmployee(employee);
    return true;
}
}