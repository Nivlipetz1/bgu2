package Employees.BL;

import Employees.BackEnd.*;
import Employees.DAL.IDAL;
import Employees.DAL.SQLiteDAL;
import Program.DriverInformations;



import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by matan on 4/19/2016.
 */
public class BL_IMPL implements IBL, DriverInformations {
    private static IDAL SQLDAL = new SQLiteDAL();

    @Override
    public boolean insertEmployee(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava) {
        /*check validity*/
        if(SQLDAL.getEmployee(id)!=null) {

            //create emp
            Employee emp = new Employee(firstName, lastName, id, roles, dateOfHire, contract, bankAcct, ava);

            //insert into database
            return SQLDAL.insert(emp);
        }
        else{
            System.out.println("Employee already exists with that ID!");
            return false;
        }
    }

    @Override
    public boolean updateEmployee(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava) {
        Employee emp = new Employee(firstName, lastName, id, roles, dateOfHire, contract, bankAcct, ava);
        return SQLDAL.update(emp);
    }

    @Override
    public boolean deleteEmployee(Employee emp) {
        return SQLDAL.delete(emp);
    }

    @Override
    public boolean insertShift(LocalTime startTime, LocalTime endTime, int duration, LocalDate date, Employee manager, Vector<Pair> roles, HashMap<Integer,Integer> amountOfRoles){
        /*check that there is no shift in this date!*/
        Day d = SQLDAL.getDay(date);
        if(d!=null) {
            //check shifts of the day
            if ((d.getMorningShift() != null && startTime.getHour() < 12) || (d.getEveningShift() != null && startTime.getHour() >= 12)) {
                if (startTime.getHour() < 12) {
                    System.out.println("Morning shift already exists for this day!");
                } else {
                    System.out.println("Evening shift already exists for this day!");
                }
                return false;
            } else {
                Shift newShift = new Shift(SQLDAL.shiftID(), startTime, endTime, duration, date, manager, roles, amountOfRoles);
                return SQLDAL.insert(newShift);
            }
        }
        else{
            Shift newShift = new Shift(SQLDAL.shiftID(), startTime, endTime, duration, date, manager, roles, amountOfRoles);
            return SQLDAL.insert(newShift);
        }
    }

    @Override
    public boolean updateShift(int ID, LocalTime startTime, LocalTime endTime, int duration, LocalDate date, Employee manager, Vector<Pair> roles, HashMap<Integer, Integer> amountOfRoles) {
        Shift newShift = new Shift(ID, startTime, endTime, duration, date, manager, roles, amountOfRoles);
        return SQLDAL.update(newShift);
    }

    @Override
    public boolean deleteShift(Shift s) {
        return SQLDAL.delete(s);
    }

    @Override
    public boolean insertDay(Day d) {
        return SQLDAL.insert(d);
    }

    @Override
    public boolean updateDay(Day d) {
        return SQLDAL.update(d);
    }

    @Override
    public boolean deleteDay(Day d) {
        return SQLDAL.delete(d);
    }

    @Override
    public boolean insertRole(String name) {
        //check valid name
        //use the func SQLDAL.roleID() to get the id of the next role and send
        // it to SQLDAL as a ROLE and not a STRING
        Role role = new Role(SQLDAL.roleID(),name);
        System.out.println(role.getID());
        return SQLDAL.insertRole(role);
    }

    @Override
    public boolean updateRole(int id, String name) {
        Role updateRole = new Role(id, name);
        return SQLDAL.updateRole(updateRole, name);
    }

    @Override
    public boolean deleteRole(Role r) {
        return SQLDAL.deleteRole(r);
    }

    @Override
    public Day getDay(LocalDate d) {
        return SQLDAL.getDay(d);
    }

    @Override
    public Shift getShift(LocalDate d, LocalTime startTime) {
        return SQLDAL.getShift(d,startTime);
    }

    @Override
    public Shift getShift(int id) {
        return SQLDAL.getShift(id);
    }

    @Override
    public Employee getEmployee(int id) {
        return SQLDAL.getEmployee(id);
    }

    @Override
    public Role getRole(int id) {
        return SQLDAL.getRole(id);
    }

    @Override
    public Vector<Role> getRoles() {
        return SQLDAL.getRoles();
    }

    @Override
    public Vector<Employee> getEmployees() {
        return SQLDAL.getEmployees();
    }

    @Override
    public boolean idExists(int id) {
        return SQLDAL.idExists(id);
    }

    @Override
    public Vector<Employee> getAvailableEmployees(int[][] avail) {
        return SQLDAL.getAvailableEmployees(avail);
    }


    /*Driver Information functions*/

    /**
     * @param licenceType
     * @param time
     * @param date
     * @return need to check if drvier with lisenceType is available based on shift availibility.
     *          if driver is availible in the shift avail and he is not scheduled for a shift then return true.
     */
    @Override
    public boolean isDriverAvailable(String licenceType, LocalTime time, LocalDate date) {
        Shift curShift = SQLDAL.getShift(date, time);
        String lType;
        boolean employeeInShift = false, employeeIsDriver = false, driverAvailable=false;

        //get list of drivers available for this shift
        for(Employee e : getAvailableEmployees(getShiftDay(date, time))) {
            if(!driverAvailable) {
                //check if employee is driver
                for (Role r : e.getRoles()) {
                    if (r.getName() == "Driver")
                        employeeIsDriver = true;
                }

                if (employeeIsDriver) {
                    //make sure employee not in shift
                    for (Pair p : curShift.getRoles()) {
                        if (p.getEmployee().getId() == e.getId()) {
                            employeeInShift = true;
                        }
                    }

                    //didn't find the employee in the shift, he is available
                    if (!employeeInShift) {
                        lType = ((Driver) e).getLicenseType();
                        //add type to list
                        if (lType == licenceType)
                            driverAvailable = true;
                    }
                    employeeInShift = false;
                }
                employeeIsDriver = false;
            }
        }

        return driverAvailable;
    }

    @Override
    public Vector<Employee> getDriverList(String licenceType, LocalTime time, LocalDate date) {
        Shift curShift = SQLDAL.getShift(date, time);
        String lType;
        boolean employeeInShift = false, employeeIsDriver = false;
        Vector<Employee> driversList = new Vector<Employee>();

        //get list of drivers available for this shift
        for(Employee e : getAvailableEmployees(getShiftDay(date, time))) {
            //check if employee is driver
            for (Role r : e.getRoles()) {
                if (r.getName() == "Driver")
                    employeeIsDriver = true;
            }

            if (employeeIsDriver) {
                //make sure employee not in shift
                for (Pair p : curShift.getRoles()) {
                    if (p.getEmployee().getId() == e.getId()) {
                        employeeInShift = true;
                    }
                }

                //didn't find the employee in the shift, he is available
                if (!employeeInShift) {
                    lType = ((Driver) e).getLicenseType();
                    //add type to list
                    if (lType == licenceType)
                        driversList.add(e);
                }
                employeeInShift = false;
            }
            employeeIsDriver = false;
        }

        return driversList;
    }

    @Override
    /**
     * Adds driver to the shift
     */
    public void setDriverBusy(int employeeID, LocalTime time, LocalDate date) {
        Shift curShift = SQLDAL.getShift(date, time);
        //add new pair <Driver, Employee> into the roles of the shift
        //id of driver role is 99
        curShift.getRoles().add(new Pair(getRole(99), getEmployee(employeeID)));
        if(curShift.getAmountOfRoles().containsKey(99)){
            //increase amount of Drivers by 1
            curShift.getAmountOfRoles().replace(99, curShift.getAmountOfRoles().get(99)+1);
        }
        else{
            //add 1 driver to amount of roles
            curShift.getAmountOfRoles().put(99, 1);
        }

        boolean result = SQLDAL.update(curShift);
        if(result){
            System.out.println("Driver added successfuly!");
        }
        else{
            System.out.println("Failed to add driver...");
        }
    }

    @Override
    public boolean isStoreKeeperAvailable(LocalTime time, LocalDate date) {
        Shift curShift = SQLDAL.getShift(date, time);
        boolean storeKeeperAvailable=false;

        for(Pair p: curShift.getRoles()){
            if(p.getRole().getName()=="StoreKeeper")
                storeKeeperAvailable=true;
        }

        return storeKeeperAvailable;
    }

    @Override
    public Vector<String> getDriversTypesLicencesAvailables() {
        Shift curShift = SQLDAL.getShift(LocalDate.now(), LocalTime.now());
        Vector<String> driversList = new Vector<String>();
        String lType;
        boolean employeeInShift = false, employeeIsDriver = false;

        //get list of drivers available for this shift
        for(Employee e : getAvailableEmployees(getShiftDay(LocalDate.now(), LocalTime.now()))) {

            //check if employee is driver
            for (Role r : e.getRoles()) {
                if (r.getName() == "Driver")
                    employeeIsDriver = true;
            }

            if (employeeIsDriver) {
                //make sure employee not in shift
                for (Pair p : curShift.getRoles()) {
                    if (p.getEmployee().getId() == e.getId()) {
                        employeeInShift = true;
                    }
                }

                //didn't find employee in the shift, he is available
                if (!employeeInShift) {
                    lType = ((Driver) e).getLicenseType();
                    //add type to list
                    if (!driversList.contains(lType))
                        driversList.add(lType);
                }

                employeeInShift = false;
            }

            employeeIsDriver = false;
        }

        return driversList;
    }


    private int[][] getShiftDay(LocalDate d, LocalTime t){
        int[][] shift = new int[1][2];

        //get day according to calendar date
        int day = d.getDayOfWeek().getValue();

        /*get specific shift in the 2-D array*/
        //1: check if morning shift/evening shift
        if(t.getHour()<12){
            //morning shift
            shift[0][0]=0;
        }
        else{
            //evening shift
            shift[0][0]=1;
        }

        //2: get day in the week: (enum: 1 (Monday) to 7 (Sunday))
        if(day==1){
            //fix monday=1
            shift[0][1] = day;
        }
        else if(day==7){
            //fix sunday=0
            shift[0][1] = 0;
        }
        else{
            //no need to fix rest of days
            shift[0][1] = day;
        }

        return shift;
    }
}
