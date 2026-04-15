package superli.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import superli.domain.*;

public class ShiftController {
    private List<Shift> shifts ;

    public ShiftController(){
        this.shifts = new ArrayList<>();
    }

    public void createShift(LocalDate date , ShiftType shiftType){
        if(getShift(date,shiftType) != null){
            throw new IllegalArgumentException("Shift already exists");
        }
        Shift newsShift = new Shift(date, shiftType);
        shifts.add(newsShift);
    }

    public Shift getShift(LocalDate date , ShiftType shiftType){
        for(Shift shift : shifts){
            if(shift.getDate().isEqual(date) && shift.getShiftType()==shiftType){
                return shift ;
            }
        }
        return null ;
    }
    public void addRequiredRole(LocalDate date ,ShiftType shiftType ,Role role ,int amount){
        Shift shift = getShift(date, shiftType) ;
        if(shift == null){
            throw new IllegalArgumentException("Shift does not exists");
        }
        shift.addRequiredRole(role,amount);
    }

    public void addAssignment(LocalDate date ,ShiftType shiftType ,ShiftAssignment assignment){
        Shift shift = getShift(date, shiftType) ;
        if(shift == null){
            throw new IllegalArgumentException("Shift does not exists");
        }
        shift.addAssignment(assignment);
     
    }

    public List<Shift> getShifts(){
        return new ArrayList<>(shifts) ;
    }

    public void assignEmployeeToShift(Employee employee ,LocalDate date ,ShiftType shiftType ,Role role){
         if (employee == null) {
         throw new IllegalArgumentException("Employee not found");
       }
        Shift shift = getShift(date, shiftType);
        if (shift == null) {
            throw new IllegalArgumentException("Shift does not exist");
        }
        if (!employee.isActive()) {
            throw new IllegalArgumentException("Employee is not active");
        }
        if (!employee.hasRole(role)) {
            throw new IllegalArgumentException("Employee does not have this role");
        }
        boolean isMorning = shiftType == ShiftType.MORNING;
        boolean isEvening = shiftType == ShiftType.EVENING;
        int day = date.getDayOfWeek().getValue();
        if (!employee.canWork(day, isMorning, isEvening)){
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
        ShiftAssignment assignment = new ShiftAssignment(employee, role, date, shiftType);
        shift.addAssignment(assignment);
        employee.addShift(assignment);
    }



}
