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

}
