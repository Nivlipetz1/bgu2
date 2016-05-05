package Employees.BackEnd;

import java.time.LocalDate;
import java.util.Vector;

/**
 * Created by matan on 5/5/2016.
 */
public class Driver extends Employee {

    private String licenseType;
    private String licenseNumber;


    public Driver(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava, String licenseType, String licenseNumber) {
        super(firstName, lastName, id, roles, dateOfHire, contract, bankAcct, ava);
        this.licenseType  = licenseType;
        this.licenseNumber = licenseNumber;
    }


    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
