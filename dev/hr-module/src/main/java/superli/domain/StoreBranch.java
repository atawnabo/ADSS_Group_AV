package superli.domain;

import java.util.ArrayList;
import java.util.List;

public class StoreBranch {
    private int id;
    private String name;
    private String address;
    private List<Employee> employees;
    private List<Shift> shifts;

    public StoreBranch(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.employees = new ArrayList<>();
        this.shifts = new ArrayList<>();
    }

    public int getBranchId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void addEmployee(Employee employee) {
    if (employee == null) {
        return;
    }

    for (Employee e : employees) {
        if (e.getId() == employee.getId()) {
            employee.assignToBranch(this);
            return;
        }
    }
    employees.add(employee);
    employee.assignToBranch(this);
}

   public void addShift(Shift shift) {
    if (shift == null) {
        return;
    }

    for (Shift s : shifts) {
        if (s.getDate().isEqual(shift.getDate())
                && s.getShiftType() == shift.getShiftType()) {
            shift.setBranch(this);
            return;
        }
    }

    shifts.add(shift);
    shift.setBranch(this);
}

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Shift> getShifts() {
        return new ArrayList<>(shifts);
    }
   public void removeEmployee(Employee employee) {
    if (employee == null) {
        return;
    }

    employees.removeIf(e -> e.getId() == employee.getId());
}
}