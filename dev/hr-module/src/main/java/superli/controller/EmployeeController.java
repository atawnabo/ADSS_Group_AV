package superli.controller;

import superli.data.EmployeeDAO;
import superli.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeController {

    private Map<Integer, Employee> employees;
    private EmployeeDAO employeeDAO;

    public EmployeeController() {
        employees = new HashMap<>();
        employeeDAO = new EmployeeDAO();
    }

    private void loadEmployeesFromDatabase() {
        List<Employee> databaseEmployees = employeeDAO.findAll();
        for (Employee employee : databaseEmployees) {
            employees.put(employee.getId(), employee);
        }
    }

    public Employee addEmployee(int id,String name,String bankName, int accountNumber,List<Role> rolesList,EmployeeTerms employeeTerms, StoreBranch branch) {
        if (branch == null) {
            throw new IllegalArgumentException("Branch is required");
        }
        if (getEmployee(id) != null) {
            System.out.println("Employee already exists");
            return null;
        }

        BankAccount bankDetails = new BankAccount(bankName, accountNumber, name);
        Employee employee = new Employee(id, name, bankDetails, employeeTerms, rolesList, branch);
        employeeDAO.save(employee);
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
        Employee employee = employees.get(id);

        if (employee != null) {
            return employee;
        }
        employee = employeeDAO.findById(id);
        if (employee != null) {
            employees.put(id, employee);
        }
        return employee;
    }

    public List<Employee> getAllEmployees() {
        loadEmployeesFromDatabase();
        return new ArrayList<>(employees.values());
    }

    public boolean removeEmployee(int id) {
        Employee employee = getEmployee(id);

        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        employee.setActive(false);
        employeeDAO.save(employee);
        return true;
    }

    public boolean enterAvailability(int employeeId, int day, boolean morning, boolean evening) {
        Employee employee = getEmployee(employeeId);
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
        boolean added = employee.addAvailability(availability);
        if(added){
            employeeDAO.save(employee);
        }
        return added;
    }

    public List<ShiftAssignment> viewScheduledShifts(int employeeId) {
     Employee employee = getEmployee(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return new ArrayList<>();
        }

        return new ArrayList<>(employee.getShiftScheduled());
    }

    public void resetForNewWeek() {
        for (Employee employee : getAllEmployees()) {
            employee.resetForNewWeek();
        }
    }

    public boolean addRoleToEmployee(int employeeId, Role role) {
        Employee employee = getEmployee(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        employee.addRole(role);
        employeeDAO.save(employee);
        return true;
    }

    public boolean removeRoleFromEmployee(int employeeId, Role role) {
        Employee employee = getEmployee(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        employee.removeRole(role);
        employeeDAO.save(employee);
        return true;
    }

    public List<Integer> getEmployeesByRole(Role role) {
        List<Integer> employeesByRole = new ArrayList<>();

        for (Employee employee : getAllEmployees()) {
            if (employee.isActive() && employee.hasRole(role)) {
                employeesByRole.add(employee.getId());
            }
        }

        return employeesByRole;
    }

    public List<Integer> getAvailableEmployees(Role selectedRole,int dayOfWeek,boolean isMorning,boolean isEvening, StoreBranch branch) {
        List<Integer> availableEmployees = new ArrayList<>();
        for (Employee employee : getAllEmployees()) {
           if (employee.isActive()
            && employee.hasRole(selectedRole)
            && employee.canWork(dayOfWeek, isMorning, isEvening)
            && employee.getBranch() != null
             && branch != null
             && employee.getBranch().getBranchId() == branch.getBranchId()) {
                
                availableEmployees.add(employee.getId());
            }
        }

        return availableEmployees;
    }

    public boolean hasAvailableShiftManager(int day, boolean isMorning, boolean isEvening, StoreBranch branch) {
        return !getAvailableEmployees(Role.SHIFT_MANAGER, day, isMorning, isEvening, branch).isEmpty();
    }

    public List<Role> getEmployeeRoles(int employeeId) {
       Employee employee = getEmployee(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return new ArrayList<>();
        }

      return new ArrayList<>(employee.getRoles());
    }

    public EmployeeTerms getEmployeeTerms(int employeeId) {
     Employee employee = getEmployee(employeeId);

        if (employee == null) {
            System.out.println("Employee not found");
            return null;
        }

        return employee.getEmployeeTerms();
    }

    public boolean register(int id, String password) {
         Employee employee = getEmployee(id);

        if (employee == null || password == null || password.isEmpty()) {
            return false;
        }

        employee.setPassword(password);
        return true;
    }

    public boolean login(int id, String password) {
        Employee employee = getEmployee(id);
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
            Employee employee = getEmployee(id);


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

    if (getEmployee(id)!= null) {
        System.out.println("Employee already exists");
        return null;
    }

    List<Role> driverRoles = rolesList == null ? new ArrayList<Role>() : new ArrayList<Role>(rolesList);

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
    employeeDAO.save(driver);
    employees.put(id, driver);
    branch.addEmployee(driver);

    return driver;
}


public List<Integer> getAvailableDrivers(LocalDate date, ShiftType shiftType,String licenseType,StoreBranch branch) {
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

    for (Employee employee : getAllEmployees()) {

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

        availableDrivers.add(employee.getId());
    }

    return availableDrivers;
}
public boolean updateEmployee(int id,String name,String bankName,int accountNumber,EmployeeTerms employeeTerms,StoreBranch branch) {
    Employee employee = getEmployee(id);

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

    employeeDAO.save(employee);
    employees.put(id, employee);

    return true;
}
public boolean updatePersonalDetails(int employeeId,  String name,  String bankName,  int accountNumber) {
    Employee employee = getEmployee(employeeId);

    if (employee == null) {
        return false;
    }

    return updateEmployee( employeeId, name, bankName, accountNumber, employee.getEmployeeTerms(), employee.getBranch() );
}

}