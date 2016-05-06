package Employees.BackEnd;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Vector;

public class Employee {



    private String firstName;
    private String lastName;
    private int id;
    //ADDED BANKACCT
    private String bankAcct;
    private Vector<Role> roles;
    //CHANGED TYPE FROM DATE TO LOCALDATE
    private LocalDate dateOfHire;
    private String contract;
    private int[][]  availability = new int[7][2];
    private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    private static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);


    public Employee(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava){

        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.roles = roles;
        this.dateOfHire = dateOfHire;
        this.contract = contract;
        this.bankAcct = bankAcct;
        availability = ava;
    }

    public Employee(Employee e){
        firstName = e.getFirstName();
        lastName = e.getLastName();
        id = e.getId();
        roles = e.getRoles();
        dateOfHire = LocalDate.parse(e.getDateOfHire(), formatterDate);
        contract = e.getContract();
        bankAcct = e.getBankAcct();
        availability = e.getAvailability();
    }

    public int[][] getAvailability() {
        return availability;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getId() {
        return id;
    }

    public Vector<Role> getRoles() {
        return roles;
    }

    public String getDateOfHire() {
        return dateOfHire.format(formatterDate);
    }

    public LocalDate getDateOfHireAsLocalDate() {
        return dateOfHire;
    }

    public String getContract() {
        return contract;
    }

    public String getBankAcct() {
        return bankAcct;
    }

    public void addRole(Role role){
        if(roles==null){
            roles = new Vector<Role>();
            roles.add(role);
        }
        else
            roles.add(role);
    }

}
