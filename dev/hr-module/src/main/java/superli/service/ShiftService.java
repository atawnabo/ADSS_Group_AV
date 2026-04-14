package superli.service;

import java.time.LocalDate;
import java.util.List;

import superli.controller.ShiftController;
import superli.domain.*;

public class ShiftService {
    
    private ShiftController shiftController;

    public ShiftService(){
        this.shiftController = new ShiftController();
    }

    public void createShift(LocalDate date , ShiftType shiftType){
        shiftController.createShift(date, shiftType);
    }

    public Shift getShift(LocalDate date , ShiftType shiftType){
       return shiftController.getShift(date, shiftType);
    }

    public void addRequiredRole(LocalDate date ,ShiftType shiftType ,Role role ,int amount){
        shiftController.addRequiredRole(date, shiftType, role, amount);
    }

    public void addAssignment(LocalDate date ,ShiftType shiftType ,ShiftAssignment assignment){
        shiftController.addAssignment(date, shiftType, assignment);
    }

    public List<Shift> getShifts(){
        return shiftController.getShifts();
    }

    public void assignEmployeeToShift(Employee employee ,LocalDate date ,ShiftType shiftType ,Role role){
        shiftController.assignEmployeeToShift(employee, date, shiftType, role);
    }


}
