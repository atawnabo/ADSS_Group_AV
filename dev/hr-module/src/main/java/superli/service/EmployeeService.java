package superli.service;

import superli.controller.EmployeeController;
import superli.domain.*;

import java.util.Date;
import java.util.List;

public class EmployeeService {

    private EmployeeController controller;

    public EmployeeService() {
        this.controller = new EmployeeController();
    }

   
    public Employee addEmployee(int id, String name, String bankName, int accountNumber,
                                List<Role> rolesList, EmployeeTerms employeeTerms, StoreBranch branch) {
        return controller.addEmployee(id, name, bankName, accountNumber, rolesList, employeeTerms, branch);
    }

    public Employee addEmployee(int id, String name, String bankName, int accountNumber,
                                List<Role> rolesList, Date startDate, String employmentType,
                                double globalSalary, double hourlySalary, int vacationDays, StoreBranch branch) {

        return controller.addEmployee(id, name, bankName, accountNumber, rolesList,
                startDate, employmentType, globalSalary, hourlySalary, vacationDays, branch);
    }

    public Employee getEmployee(int id) {
        return controller.getEmployee(id);
    }

    public List<Employee> getAllEmployees() {
        return controller.getAllEmployees();
    }

    public boolean removeEmployee(int id) {
        return controller.removeEmployee(id);
    }

   

    public boolean enterAvailability(int employeeId, int day, boolean morning, boolean evening) {
        return controller.enterAvailability(employeeId, day, morning, evening);
    }


    public List<ShiftAssignment> viewScheduledShifts(int employeeId) {
        return controller.viewScheduledShifts(employeeId);
    }

    public void resetForNewWeek() {
        controller.resetForNewWeek();
    }


    public boolean addRoleToEmployee(int employeeId, Role role) {
        return controller.addRoleToEmployee(employeeId, role);
    }

    public boolean removeRoleFromEmployee(int employeeId, Role role) {
        return controller.removeRoleFromEmployee(employeeId, role);
    }

    public List<Role> getEmployeeRoles(int employeeId) {
        return controller.getEmployeeRoles(employeeId);
    }


    public List<Integer> getAvailableEmployees(Role role, int day, boolean morning, boolean evening, StoreBranch branch) {
       return controller.getAvailableEmployees(role, day, morning, evening, branch);
    }

    public List<Integer> getEmployeesByRole(Role role) {
        return controller.getEmployeesByRole(role);
    }

    public boolean hasAvailableShiftManager(int day, boolean morning, boolean evening, StoreBranch branch) {
        return controller.hasAvailableShiftManager(day, morning, evening, branch);
    }
    
    public boolean hasAssignedStockKeeper(Shift shift){
        return controller.hasAssignedStockKeeper(shift);
    }

    public EmployeeTerms getEmployeeTerms(int employeeId) {
        return controller.getEmployeeTerms(employeeId);
    }
    public boolean register(int id, String password) {
        return controller.register(id, password);
    }

    public boolean login(int id, String password) {
        return controller.login(id, password);
    }

    public boolean logout(int id) {
        return controller.logout(id);
    }
}