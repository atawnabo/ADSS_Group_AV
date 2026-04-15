package superli.domain;
import java.time.LocalDate;
public class ShiftAssignment {
    private Employee employee ;
    private Role role ;
    private LocalDate date;
    private  ShiftType shiftType;
    public ShiftAssignment(Employee e , Role r, LocalDate date, ShiftType shiftType){
    this.employee = e;
    this.role = r;
    this.date = date;
    this.shiftType = shiftType;
}
    
    public Employee getEmployee(){
        return employee;
    }
    public Role getRole(){
        return role;
    }
public LocalDate getDate() {
    return date;
}

public ShiftType getShiftType() {
    return shiftType;
}
}
