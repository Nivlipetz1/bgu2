package Employees.BL;

import Employees.BackEnd.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by matan on 4/19/2016.
 */
public interface IBL {
    boolean insertEmployee(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava, String licenseNum, String licenseType);
    boolean updateEmployee(String firstName, String lastName, int id, Vector<Role> roles, LocalDate dateOfHire, String contract, String bankAcct, int[][] ava, String licenseNum, String licenseType);
    boolean deleteEmployee(Employee emp);
    boolean deleteDriver(int id);
    boolean insertShift(LocalTime startTime, LocalTime endTime, int duration, LocalDate date, Employee manager, Vector<Pair> roles, HashMap<Integer,Integer> amountOfRoles);
    boolean updateShift(int ID, LocalTime startTime, LocalTime endTime, int duration, LocalDate date, Employee manager, Vector<Pair> roles, HashMap<Integer,Integer> amountOfRoles);
    boolean deleteShift(Shift s);
    boolean insertDay(Day d);
    boolean updateDay(Day d);
    boolean deleteDay(Day d);
    boolean insertRole(String name);
    boolean updateRole(int id, String name);
    boolean deleteRole(Role r);
    Day getDay(LocalDate d);
    Shift getShift(LocalDate d, LocalTime startTime);
    Employee getEmployee(int id);
    Role getRole(int id);
    Vector<Role> getRoles();
    Vector<Employee> getEmployees();
    boolean idExists(int id);
    public Shift getShift(int id);



    /**here i will send in the arguements a shift
    * it will be an array[1][2] where avail[0][0] is 0 or 1
    * and avail[0][1] is 0-6..
    * Meaning: that if avail[0][0] = 1 and avail[0][1] = 5
    * the shift being sent is [1][5] = Friday evening.
    * As a result i need you to return a vector of employees that are available on this shift,
    * not their IDs i need the entire instance of employee itself so i can list their names, etc..
    */
    Vector<Employee> getAvailableEmployees(int[][] avail);

}
