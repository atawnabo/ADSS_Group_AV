package superli.domain;

import java.util.List;

public class Driver extends Employee {
    private String licenseType;

    public Driver(int id,
                  String name,
                  BankAccount bankDetails,
                  EmployeeTerms employeeTerms,
                  List<Role> roles,
                  StoreBranch branch,
                  String licenseType) {

        super(id, name, bankDetails, employeeTerms, roles, branch);
        this.licenseType = licenseType;

        if (!hasRole(Role.DRIVER)) {
            addRole(Role.DRIVER);
        }
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
}