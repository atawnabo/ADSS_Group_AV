package superli.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shift {
    private LocalDate date ;
    private ShiftType shiftType ;
    private List<ShiftAssignment> assignments ;
    private Map<Role,Integer> requiredRoles ;

    public Shift(LocalDate date , ShiftType shiftType){
        this.date = date ;
        this.shiftType = shiftType ;
        this.assignments = new ArrayList<>();
        this.requiredRoles = new HashMap<>();
    }

    public LocalDate getDate(){
        return date ;
    }
    public ShiftType getShiftType(){
        return shiftType ;
    }
    public List<ShiftAssignment> getAssignments(){
        return assignments ;
    }
    public Map<Role,Integer> getRequiredRoles(){
        return requiredRoles ;
    }

    public void addAssignment(ShiftAssignment assignment){
        assignments.add(assignment);
    }
    public void removeAssignment(ShiftAssignment assignment){
        assignments.remove(assignment);
    }
    public void addRequiredRole(Role role , int amount){
        requiredRoles.put(role, amount);
    }

    public boolean hasManager(){
        for(ShiftAssignment assignment : assignments){
            if(assignment.getRole() == Role.SHIFT_MANAGER){
                return true ;
            }
        }
        return false ;
    }

    public boolean isFullyStaffed(){
        for(Role role : requiredRoles.keySet()){
            int requiredAmount = requiredRoles.get(role);
            int currentAmount = 0 ;
            for(ShiftAssignment assignment : assignments){
                if(assignment.getRole() == role){
                    currentAmount++;
                }
            }
            if(currentAmount < requiredAmount) {
                return false ;
            }
        }
        return true ;
    }

}
