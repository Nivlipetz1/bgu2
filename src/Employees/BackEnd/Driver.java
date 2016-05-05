package Employees.BackEnd;

import java.time.LocalDate;
import java.util.Vector;

/**
 * Created by matan on 5/5/2016.
 */
public class Driver extends Employee {

    private String lisenceType;
    private String lisenceNumber;


    public Driver(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava, String lisenceType, String lisenceNumber) {
        super(firstName, lastName, id, roles, dateOfHire, contract, bankAcct, ava);
        this.lisenceType = lisenceType;
        this.lisenceNumber = lisenceNumber;
    }


    public String getLisenceNumber() {
        return lisenceNumber;
    }

    public String getLisenceType() {
        return lisenceType;
    }

    public void setLisenceNumber(String lisenceNumber) {
        this.lisenceNumber = lisenceNumber;
    }

    public void setLisenceType(String lisenceType) {
        this.lisenceType = lisenceType;
    }
}
