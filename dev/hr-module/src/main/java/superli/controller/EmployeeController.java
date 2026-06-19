package superli.controller;

import superli.domain.*;

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
        if (employees.containsKey(id)) {
            System.out.println("Employee already exists");
            return null;
        }

        BankAccount bankDetails = new BankAccount(bankName, accountNumber, name);
        Employee employee = new Employee(id, name, bankDetails, employeeTerms, rolesList, branch);
        employees.put(id, employee);
        return employee;
    }


    public Employee addEmployee(int id, String name,String bankName,int accountNumber,List<Role> rolesList,Date startDate,String employmentType,double globalSalary,double hourlySalary,int vacationDays,StoreBranch branch) {

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

            if (employee.isActive() && employee.hasRole(selectedRole)&& employee.canWork(dayOfWeek, isMorning, isEvening) && employee.getBranch().equals(branch)) 
                {
                availableEmployees.add(employeeId);
            }
        }

        return availableEmployees;
    }

    public boolean hasAvailableShiftManager(int day, boolean isMorning, boolean isEvening, StoreBranch branch) {
        return !getAvailableEmployees(Role.SHIFT_MANAGER, day, isMorning, isEvening, branch).isEmpty();
    }

    public boolean hasAssignedStockKeeper(Shift shift) {
        for (ShiftAssignment assignment : shift.getAssignments()) {
            Employee employee = assignment.getEmployee();
            if (employee.isActive() && employee.hasRole(Role.STOCK_KEEPER) && employee.getBranch().getBranchId() == shift.getBranch().getBranchId()) {
               return true;
            }
        }
        return false;
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
}