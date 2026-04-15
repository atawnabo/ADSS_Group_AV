package superli.domain;

import java.util.Date;

public class EmployeeTerms {

    private Date startDate;
    private String employmentType;
    private double globalSalary;
    private double hourlySalary;
    private int vacationDays;

    public EmployeeTerms(Date startDate, String employmentType,
                         double globalSalary, double hourlySalary, int vacationDays) {
        this.startDate = startDate;
        this.employmentType = employmentType;
        this.globalSalary = globalSalary;
        this.hourlySalary = hourlySalary;
        this.vacationDays = vacationDays;
    }

    public Date getStartDate() {
         return startDate;
         }
 public String getEmploymentType() {
    return employmentType;
}

public double getGlobalSalary() {
    return globalSalary;
}

public double getHourlySalary() {
    return hourlySalary;
}

public int getVacationDays() {
    return vacationDays;
}
}