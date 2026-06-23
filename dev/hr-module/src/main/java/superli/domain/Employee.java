package superli.domain;

import java.util.*;

public class Employee extends User {

    private int id;
    private String name;
    private BankAccount bankDetails;
    private EmployeeTerms employeeTerms;
    private List<Role> roles;
    private List<ShiftAssignment> shiftScheduled;
    private List<Availability> availability;
    private boolean active = true;
    private StoreBranch branch;

    public Employee(int id, String name, BankAccount bankDetails,EmployeeTerms employeeTerms, List<Role> roles ,StoreBranch branch){
        this.id = id;
        this.name = name;
        this.bankDetails = bankDetails;
        this.employeeTerms = employeeTerms;

        if (roles != null) {
          this.roles = new ArrayList<>(roles);
        }
        else{
            this.roles = new ArrayList<>();
        }

        this.branch = branch;
        this.shiftScheduled = new ArrayList<>();
        this.availability = new ArrayList<>();
    }


    public int getId() { return id; }

    public String getName() { return name; }

    public BankAccount getBankDetails() { return bankDetails; }

    public EmployeeTerms getEmployeeTerms() { return employeeTerms; }

    public List<Role> getRoles() { return roles; }

    public List<ShiftAssignment> getShiftScheduled() { return shiftScheduled; }

    public List<Availability> getAvailability() { return availability; }

   public StoreBranch getBranch() {
    return branch;
}

public void assignToBranch(StoreBranch branch) {
    this.branch = branch;
}

public boolean belongsToBranch(StoreBranch branch) {
    if (this.branch == null || branch == null) {
        return false;
    }

    return this.branch.getBranchId() == branch.getBranchId();
}

public boolean belongsToBranch(int branchId) {
    return this.branch != null && this.branch.getBranchId() == branchId;
}
    public boolean isActive() { return active; }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            roles.add(role);
        }
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public void addShift(ShiftAssignment assignment) {
    if (assignment != null) {
        shiftScheduled.add(assignment);
    }
}
    public boolean removeShift(ShiftAssignment assignment) {

    if (assignment == null) 
    {
        return false;
   }
    if (shiftScheduled.remove(assignment)) {
        return true;
    }

    return false;
}

    public boolean addAvailability(Availability availability) {
        if (availability == null) 
            return false;
        this.availability.add(availability);
        return true;
    }

    
   public boolean canWork(int day, boolean isMorning, boolean isEvening) {

    if (availability == null || availability.isEmpty()) {
        return true;
    }

    for (Availability av : availability) {
        if (av.getDay() == day) {
            return (av.isMorningShift() && isMorning) ||
                   (av.isEveningShift() && isEvening);
        }
    }

    return true; 
}

    public void resetForNewWeek() {
        shiftScheduled.clear();
        availability.clear();
    }

public void updateDetails(String name, BankAccount bankDetails, EmployeeTerms employeeTerms) {
    this.name = name;
    this.bankDetails = bankDetails;
    this.employeeTerms = employeeTerms;
}
 }