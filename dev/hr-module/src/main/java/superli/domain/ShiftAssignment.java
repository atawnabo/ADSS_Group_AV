package superli.domain;

public class ShiftAssignment {
    private Employee employee ;
    private Role role ;

    public ShiftAssignment(Employee e , Role r){
        this.employee = e ;
        this.role = r ;
    }
    
    public Employee getEmployee(){
        return employee;
    }
    public Role getRole(){
        return role;
    }

}
