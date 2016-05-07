package Employees.PL;

import Employees.BL.BL_IMPL;
import Employees.BL.IBL;
import Employees.BackEnd.Driver;
import Employees.BackEnd.Employee;
import Employees.BackEnd.Role;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by omer on 18/04/16.
 */
public class EmployeeMenu {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    private static IBL bl_impl = new BL_IMPL();
    private static HashMap<Integer,Role> rolesDictionary = new HashMap<Integer, Role>();
    private static Scanner sc = new Scanner(System.in);
    private static String[][] days = new String[2][7];

    public static void run(){
        Scanner sc = new Scanner(System.in);
        boolean switchCase = false;

        fillRoles();

        /*initialize days strings*/
        days[0][0]="Sunday Morning:"; days[1][0]="Sunday Evening:";
        days[0][1]="Monday Morning:"; days[1][1]="Monday Evening:";
        days[0][2]="Tuesday Morning:"; days[1][2]="Tuesday Evening:";
        days[0][3]="Wednesday Morning:"; days[1][3]="Wednesday Evening:";
        days[0][4]="Thursday Morning:"; days[1][4]="Thursday Evening:";
        days[0][5]="Friday Morning:"; days[1][5]="Friday Evening:";
        days[0][6]="Saturday Morning:"; days[1][6]="Saturday Evening:";
        System.out.println(MainMenu.ANSI_BOLD+"Welcome to Employee/Role menu"+MainMenu.ANSI_RESET);
        System.out.println(MainMenu.ANSI_BOLD+"1"+MainMenu.ANSI_RESET+". Add Employee");
        System.out.println(MainMenu.ANSI_BOLD+"2"+MainMenu.ANSI_RESET+". Edit/Delete Employee");
        System.out.println(MainMenu.ANSI_BOLD+"3"+MainMenu.ANSI_RESET+". Add Role");
        System.out.println(MainMenu.ANSI_BOLD+"4"+MainMenu.ANSI_RESET+". Edit/Delete Role");


        while(!switchCase) {
            int i = sc.nextInt();
            switch (i) {
                case 1:
                    addEmployee();
                    switchCase=true;
                    break;
                case 2:
                    editEmployee();
                    switchCase=true;
                    break;
                case 3:
                    addRole();
                    switchCase=true;
                    break;
                case 4:
                    editRole();
                    switchCase=true;
                    break;
                case 5:
                    //add return to MainMenu menu function
                    switchCase=true;
                    break;
                default:
                    System.out.println("Try again..");
                    break;
            }
        }
    }

    private static void addEmployee(){
        boolean switchCase=false, finishedRoles = false, driver = false;
        int[][] avail = new int[2][7];
        String licenseNum="", licenseType="";
        Vector<Role> roles = new Vector<Role>();
        String fName, lName, bankAcct, contract;
        int ID, i=0, x=0, y=0, z=1, roleChosen; //use x,y for the availabilty array.. use z for rolesDictionary movement

        System.out.println("Insert Employee's ID:");
        while(true) {
            ID = sc.nextInt();
            if (bl_impl.idExists(ID)){
                System.out.println(MainMenu.ANSI_BOLD+MainMenu.ANSI_RED+"ID EXISTS IN DB"+MainMenu.ANSI_RESET);
                System.out.println("Please try again");
            }
            else{
                break;
            }
        }
        System.out.println("Insert Employee's first name:");
        fName = sc.next();

        System.out.println("Insert Employee's last name:");
        lName = sc.next();

        System.out.println("Insert Employee's date of hire (dd/MM/yyyy):");
        LocalDate date;
        while(true) {
            try {
                date = LocalDate.parse(sc.next(), formatter);
                break;
            } catch (Exception e) {
                System.out.println(MainMenu.ANSI_BOLD+MainMenu.ANSI_RED+"BAD INPUT"+MainMenu.ANSI_RESET);
                System.out.println("Please try again.");
            }
        }
        System.out.println("Insert Employee's contract:");
        contract = sc.next();

        System.out.println("Insert Employee's bank account:");
        bankAcct= sc.next();

        //roles
        while(!finishedRoles){
            System.out.println("Choose a role to add, Press 0 if finished:");

            //print the roles available
            for(Role r: rolesDictionary.values()){
                System.out.println(r.getID()+ ": " + r.getName());
            }

            roleChosen = sc.nextInt();

            if (roleChosen==0){
                finishedRoles = true;
            }
            else if(rolesDictionary.containsKey(roleChosen)){ //make sure input is valid
                roles.add(rolesDictionary.get(roleChosen));
                if (rolesDictionary.get(roleChosen).getName().equals("Driver")) //check if driver role chosen
                    driver = true;
                System.out.println(rolesDictionary.get(roleChosen).getName() + " added." );
            }
            else{
                System.out.println("Try again..");
            }
        }

        //avail
        System.out.println("Now insert employee's availability for each shift (1=Available, 0=Not available):");
        for(x=0;x<7;x++) {
            for (y=0; y < 2; y++) {
                switchCase=false;
                System.out.println(days[y][x]);
                while (!switchCase) {
                    i = sc.nextInt();
                    switch(i){
                        case 0:
                            avail[y][x] = 0;
                            switchCase = true;
                            break;
                        case 1:
                            avail[y][x] = 1;
                            switchCase = true;
                            break;
                        default:
                            System.out.println("Try again..");
                            break;
                    }
                }
            }
        }

        if(driver){
            System.out.println("Please enter license number:");
            licenseNum = sc.next();

            System.out.println("Please enter license type:");
            licenseType = sc.next().toUpperCase();
        }

        boolean result = bl_impl.insertEmployee(fName, lName, ID, roles, date, contract, bankAcct, avail, licenseNum, licenseType);
        if(result){
            System.out.println("Employee added successfully!");
        }
        else{
            System.out.println("Employee failed to add");
        }
    }

    private static void editEmployee(){
        Vector<Employee> employees = bl_impl.getEmployees();
        boolean switchCase=false, finishedRoles = false, result, driver = false;
        String licenseNum="", licenseType="";
        int[][] avail = new int[2][7];
        Vector<Role> roles = new Vector<Role>();
        String fName, lName, bankAcct, contract;
        int ID, i=0, x=0, y=0, z=1, roleChosen, empID; //use x,y for the availabilty array.. use z for rolesDictionary movement
        for(Employee emp : employees){
            System.out.println(emp.getId()+" "+emp.getFirstName()+" "+emp.getLastName());
        }

        System.out.println("Please insert employee's ID:");
        empID = sc.nextInt();

        Employee emp = bl_impl.getEmployee(empID);


        //check to make sure emp is not null
        while(emp==null){
            System.out.println("No employee exists with that ID, try again:");
            empID = sc.nextInt();
            emp = bl_impl.getEmployee(empID);
        }

        System.out.println("What would you like to update?");
        System.out.println("1: Personal Information");
        System.out.println("2: Employee availability");
        System.out.println("3: Remove employee's roles");
        System.out.println("4: Add to employee's roles");
        System.out.println("5: Delete employee");


        while(!switchCase) {
            i = sc.nextInt();
            switch (i) {
                case 1:
                    System.out.println("Editing Personal Info. Press -1 to keep current value");
                    System.out.println("Insert Employee's first name: (Current: "+emp.getFirstName()+" )");
                    fName = sc.next();
                    if(fName.equals("-1")){
                        fName = emp.getFirstName();
                    }

                    System.out.println("Insert Employee's last name: (Current: "+emp.getLastName()+" )");
                    lName = sc.next();
                    if(lName.equals("-1")){
                        lName = emp.getLastName();
                    }

                    System.out.println("Insert Employee's date of hire (dd/MM/yyyy): (Current: "+emp.getDateOfHire()+" )");

                    LocalDate date;
                    while(true) {
                        String inputDate = sc.next();
                    if(inputDate.equals("-1")){
                        date = LocalDate.parse(emp.getDateOfHire(), formatter);
                        break;
                    }
                    else{
                            try {
                                date = LocalDate.parse(inputDate, formatter);
                                break;
                            } catch (Exception e) {
                                System.out.println(MainMenu.ANSI_BOLD+MainMenu.ANSI_RED+"BAD INPUT"+MainMenu.ANSI_RESET);
                                System.out.println("Please try again.");
                            }
                        }
                    }

                    System.out.println("Insert Employee's contract: (Current: "+emp.getContract()+" )");
                    contract = sc.next();
                    if(contract.equals("-1")){
                        contract = emp.getContract();
                    }

                    System.out.println("Insert Employee's bank account: (Current: "+emp.getBankAcct()+" )");
                    bankAcct= sc.next();
                    if(bankAcct.equals("-1")){
                        bankAcct = emp.getBankAcct();
                    }
                    for(Role r : emp.getRoles()){
                        if(r.getName().equals("Driver"))
                            driver = true;
                    }

                    //update driver fields
                    if(driver){
                        System.out.println("Insert Employee's license number: (Current: "+((Driver)emp).getLicenseNumber()+" )");
                        ((Driver)emp).setLicenseNumber(sc.next());

                        System.out.println("Insert Employee's license type: (Current: "+((Driver)emp).getLicenseType()+" )");
                        ((Driver)emp).setLicenseType(sc.next().toUpperCase());

                        bl_impl.updateEmployee(fName, lName, emp.getId(), emp.getRoles(), date, contract, bankAcct, emp.getAvailability(), ((Driver)emp).getLicenseNumber(), ((Driver)emp).getLicenseType());
                    }
                    else{
                        bl_impl.updateEmployee(fName, lName, emp.getId(), emp.getRoles(), date, contract, bankAcct, emp.getAvailability(), licenseNum, licenseType);
                    }
                    switchCase = true;
                    break;
                case 2:
                    System.out.println("Now insert employee's availability for each shift (1=Available, 0=Not available):");
                    for(x=0;x<7;x++) {
                        for (y=0; y < 2; y++) {
                            switchCase=false;
                            System.out.println(days[y][x]+" (Current: "+emp.getAvailability()[y][x]+")");
                            while (!switchCase) {
                                i = sc.nextInt();
                                switch(i){
                                    case 0:
                                        avail[y][x] = 0;
                                        switchCase = true;
                                        break;
                                    case 1:
                                        avail[y][x] = 1;
                                        switchCase = true;
                                        break;
                                    case -1:
                                        avail[y][x] = emp.getAvailability()[y][x];
                                        switchCase = true;
                                        break;
                                    default:
                                        System.out.println("Try again..");
                                        break;
                                }
                            }
                        }
                    }

                    for(Role r : emp.getRoles()){
                        if(r.getName().equals("Driver"))
                            driver = true;
                    }
                    if(driver){
                        bl_impl.updateEmployee(emp.getFirstName(), emp.getLastName(), emp.getId(), emp.getRoles(), LocalDate.parse(emp.getDateOfHire(), formatter), emp.getContract(), emp.getBankAcct(), avail, ((Driver)emp).getLicenseNumber(), ((Driver)emp).getLicenseType());
                    }
                    else{
                        bl_impl.updateEmployee(emp.getFirstName(), emp.getLastName(), emp.getId(), emp.getRoles(), LocalDate.parse(emp.getDateOfHire(), formatter), emp.getContract(), emp.getBankAcct(), avail, licenseNum, licenseType);
                    }

                    switchCase = true;
                    break;
                case 3:
                    while(!finishedRoles){
                        System.out.println("Choose a role to remove, Press 0 if finished:");

                        //print the roles in the employee's roles vector
                        for(z=0;z<emp.getRoles().size();z++){
                            System.out.println((z+1)+ ": " + emp.getRoles().get(z).getName());
                        }
                        roleChosen = sc.nextInt();

                        if (roleChosen==0){
                            finishedRoles = true;
                        }
                        else if(rolesDictionary.containsKey(roleChosen)){ //make sure choice is valid
                            //remove from the employee's roles vector
                            System.out.println(emp.getRoles().get(roleChosen-1).getName() + " removed." );

                            //delete driver
                            if(emp.getRoles().get(roleChosen-1).getName().equals("Driver")){
                                bl_impl.deleteDriver(emp.getId());
                            }

                            emp.getRoles().remove(roleChosen-1);
                        }
                        else{
                            System.out.println("Try again..");
                        }
                    }

                    for(Role r : emp.getRoles()){
                        if(r.getName().equals("Driver"))
                            driver = true;
                    }
                    if(driver){
                        bl_impl.updateEmployee(emp.getFirstName(), emp.getLastName(), emp.getId(), emp.getRoles(), LocalDate.parse(emp.getDateOfHire(), formatter), emp.getContract(), emp.getBankAcct(), emp.getAvailability(), ((Driver)emp).getLicenseNumber(), ((Driver)emp).getLicenseType());
                    }
                    else{
                        bl_impl.updateEmployee(emp.getFirstName(), emp.getLastName(), emp.getId(), emp.getRoles(), LocalDate.parse(emp.getDateOfHire(), formatter), emp.getContract(), emp.getBankAcct(), emp.getAvailability(), licenseNum, licenseType);
                    }

                    switchCase = true;
                    break;
                case 4:
                    while(!finishedRoles){
                        System.out.println("Choose a role to add, Press 0 if finished:");

                        //print the roles available
                        for(Role r: rolesDictionary.values()){
                            System.out.println(r.getID()+ ": " + r.getName());
                        }
                        roleChosen = sc.nextInt();

                        if (roleChosen==0){
                            finishedRoles = true;
                        }
                        else if(rolesDictionary.containsKey(roleChosen)){ //make sure choice is valid
                            //if emp doesn't have the chosen role add to his roles vector
                            if(!emp.getRoles().contains(rolesDictionary.get(roleChosen))){
                                emp.getRoles().add(rolesDictionary.get(roleChosen));
                                System.out.println(rolesDictionary.get(roleChosen).getName() + " added." );
                            }
                            else{
                                System.out.println("Employee already has the chosen role.");
                            }
                        }
                        else{
                            System.out.println("Try again..");
                        }
                    }

                    for(Role r : emp.getRoles()){
                        if(r.getName().equals("Driver"))
                            driver = true;
                    }
                    if(driver){
                        result = bl_impl.updateEmployee(emp.getFirstName(), emp.getLastName(), emp.getId(), emp.getRoles(), LocalDate.parse(emp.getDateOfHire(), formatter), emp.getContract(), emp.getBankAcct(), emp.getAvailability(), ((Driver)emp).getLicenseNumber(), ((Driver)emp).getLicenseType());
                    }
                    else{
                        result = bl_impl.updateEmployee(emp.getFirstName(), emp.getLastName(), emp.getId(), emp.getRoles(), LocalDate.parse(emp.getDateOfHire(), formatter), emp.getContract(), emp.getBankAcct(), emp.getAvailability(), licenseNum, licenseType);
                    }

                    if(result){
                        System.out.println("Employee successfully edited");
                    }
                    else{
                        System.out.println("Employee failed to edit");
                    }
                    switchCase = true;
                    break;
                case 5:
                    result = bl_impl.deleteEmployee(emp);
                    if(result){
                        System.out.println("Employee successfully delete");
                    }
                    else{
                        System.out.println("Employee failed to delete");
                    }
                    switchCase = true;
                    break;
                default:
                    System.out.println("Try again..");
                    break;
            }
        }

    }

    private static void addRole(){
        String name;
        System.out.println("Please insert role name:");
        name = sc.next();
        bl_impl.insertRole(name);
        fillRoles();
    }

    private static void editRole(){
        int roleChosen, delete, z;
        String newName;
        System.out.println("Choose role:");

        //print the roles available
        for(Role r: rolesDictionary.values()){
            System.out.println(r.getID()+ ": " + r.getName());
        }


        //chose which role from the list
        roleChosen = sc.nextInt();

        if(rolesDictionary.containsKey(roleChosen)){ //make sure choice is valid
            System.out.println("To delete role press 1, to edit press 0:");
            try {
                delete = sc.nextInt();
            }catch (InputMismatchException e){
                delete = -1;
            }

            switch(delete){
                case 0:
                    //edit role
                    System.out.println("Insert new name:");
                    newName = sc.next();
                    boolean result= bl_impl.updateRole(rolesDictionary.get(roleChosen).getID(), newName);
                    if(result){
                        System.out.println("Role successfully edited");
                    }
                    else{
                        System.out.println("Role failed to edit");
                    }
                    break;
                case 1:
                    //delete role
                    result = bl_impl.deleteRole(rolesDictionary.get(roleChosen));
                    if(result){
                        System.out.println("Role successfully deleted");
                    }
                    else{
                        System.out.println("Role failed to delete");
                    }
                    break;
                case -1:
                    System.out.println("Illegal Input:(");
            }
            //update roles dictionary
            fillRoles();
        }
        else {
            System.out.println("Try again..");
        }
    }

    private static void fillRoles(){
        //fill in the rolesDicationary
        rolesDictionary.clear();
        if(bl_impl.getRoles().size()>0) {
            for (Role r : bl_impl.getRoles()) {
                rolesDictionary.put(r.getID(), r);
                //lineIndex++;
            }
        }
    }
}