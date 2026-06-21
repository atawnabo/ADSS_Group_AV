package superli.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import superli.data.EmployeeDAO;
import superli.data.ShiftDAO;
import superli.domain.*;

public class ShiftController {
    private List<Shift> shifts ;
    private ShiftDAO shiftDAO;
    private EmployeeDAO employeeDAO;

    public ShiftController(){
        this.shifts = new ArrayList<>();
        this.shiftDAO = new ShiftDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    public void createShift(LocalDate date , ShiftType shiftType, StoreBranch branch){
        
        if(getShift(date,shiftType,branch) != null){
            throw new IllegalArgumentException("Shift already exists");
        }
        Shift newShift = new Shift(date, shiftType, branch);
        shiftDAO.save(newShift);
        shifts.add(newShift);

        if (branch != null) {
            branch.addShift(newShift);
        }
    }

    public Shift getShift(LocalDate date , ShiftType shiftType, StoreBranch branch){
        if (date == null || shiftType == null || branch == null) {
            return null;
        }
        for (Shift shift : shifts) {
            if (shift.getDate().isEqual(date)
                && shift.getShiftType() == shiftType
                && shift.getBranch().getBranchId() == branch.getBranchId()) {
                    return shift;
                }
        }

        Shift shiftFromDatabase = shiftDAO.findByDetails(date, shiftType, branch);
        if (shiftFromDatabase != null) {
            shifts.add(shiftFromDatabase);
        }
        return shiftFromDatabase;
    }

    public void addRequiredRole(LocalDate date ,ShiftType shiftType ,Role role ,int amount, StoreBranch branch){
        Shift shift = getShift(date, shiftType, branch) ;
        if(shift == null){
            throw new IllegalArgumentException("Shift does not exists");
        }
        shift.addRequiredRole(role,amount);
        shiftDAO.save(shift);
    }

   public void addAssignment(LocalDate date, ShiftType shiftType, ShiftAssignment assignment, StoreBranch branch) {
    if (assignment == null) {
        throw new IllegalArgumentException("Assignment is required");
    }

    assignEmployeeToShift(
            assignment.getEmployee(),
            date,
            shiftType,
            assignment.getRole(),
            false,
            branch
    );
}

    public List<Shift> getShifts() {
        shifts.clear();
        shifts.addAll(shiftDAO.findAll());
        return new ArrayList<>(shifts);
    }

    public void assignEmployeeToShift(Employee employee ,LocalDate date ,ShiftType shiftType ,Role role,boolean specialApproval, StoreBranch branch){
         if (employee == null) {
         throw new IllegalArgumentException("Employee not found");
       }
        Shift shift = getShift(date, shiftType, branch);
        if (shift == null) {
            throw new IllegalArgumentException("Shift does not exist");
        }
        if (!employee.isActive()) {
            throw new IllegalArgumentException("Employee is not active");
        }
        if(employee.getBranch().getBranchId() != shift.getBranch().getBranchId()){
            throw new IllegalArgumentException("Employee and shift must belong to the same branch");
        }
        if (!employee.hasRole(role)) {
            throw new IllegalArgumentException("Employee does not have this role");
        }
        boolean isMorning = shiftType == ShiftType.MORNING;
        boolean isEvening = shiftType == ShiftType.EVENING;
        int day = date.getDayOfWeek().getValue();
        if (!employee.canWork(day, isMorning, isEvening)&&!specialApproval){
            throw new IllegalArgumentException("Employee is not available for this shift");
        }
        
        Integer requiredAmountOfRole = shift.getRequiredRoles().get(role);
        if(requiredAmountOfRole == null){
            throw new IllegalArgumentException("This role is not required for this shift");
        }

        int currentAmountOfRole = 0 ;
        for(ShiftAssignment shiftAssignment : shift.getAssignments()){
            if(shiftAssignment.getEmployee().getId() == employee.getId()){
                throw new IllegalArgumentException("Employee is already assigned to this shift");
            }
            if(shiftAssignment.getRole() == role){
                currentAmountOfRole++ ;
            }
        }
        if(currentAmountOfRole >= requiredAmountOfRole){
            throw new IllegalArgumentException("The amount of employees for this role is full !");
        }
        employeeDAO.save(employee);
        ShiftAssignment assignment = new ShiftAssignment(employee, role, date, shiftType);
        shift.addAssignment(assignment);
        employee.addShift(assignment);
        shiftDAO.save(shift);
    }
    
  public boolean hasStockKeeper(LocalDate date, ShiftType shiftType, StoreBranch branch) {
    if (date == null) {
        throw new IllegalArgumentException("Date is required");
    }

    if (shiftType == null) {
        throw new IllegalArgumentException("Shift type is required");
    }

    if (branch == null) {
        throw new IllegalArgumentException("Branch is required");
    }

    Shift shift = getShift(date, shiftType, branch);

    if (shift == null) {
        return false;
    }

    for (ShiftAssignment assignment : shift.getAssignments()) {
        Employee employee = assignment.getEmployee();

        if (assignment.getRole() == Role.STOCK_KEEPER
                && employee != null
                && employee.isActive()
                && employee.getBranch() != null
                && employee.getBranch().getBranchId() == shift.getBranch().getBranchId()) {
            return true;
        }
    }

    return false;
}
}
