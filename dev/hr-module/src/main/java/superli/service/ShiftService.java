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

    public void createShift(LocalDate date , ShiftType shiftType, StoreBranch branch){
        shiftController.createShift(date, shiftType, branch);
    }

    public Shift getShift(LocalDate date , ShiftType shiftType, StoreBranch branch){
       return shiftController.getShift(date, shiftType, branch);
    }

    public void addRequiredRole(LocalDate date ,ShiftType shiftType ,Role role ,int amount, StoreBranch branch){
        shiftController.addRequiredRole(date, shiftType, role, amount, branch);
    }

    public void addAssignment(LocalDate date ,ShiftType shiftType ,ShiftAssignment assignment, StoreBranch branch){
        shiftController.addAssignment(date, shiftType, assignment, branch);
    }

    public List<Shift> getShifts(){
        return shiftController.getShifts();
    }

    public void assignEmployeeToShift(Employee employee ,LocalDate date ,ShiftType shiftType ,Role role,boolean specialApproval, StoreBranch branch){
        shiftController.assignEmployeeToShift(employee, date, shiftType, role,specialApproval, branch);
    }
}
