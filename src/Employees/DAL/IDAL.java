package Employees.DAL;

import Employees.BackEnd.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;


/**
 * Created by omer on 18/04/16.
 */
public interface IDAL {
    boolean insert(Employee emp);
    boolean delete(Employee emp);
    boolean update(Employee emp);
    boolean insert(Shift shft);
    boolean delete(Shift shft);
    boolean update(Shift sft);
    boolean insert(Day day);
    boolean delete(Day day);
    boolean update(Day day);
    boolean addRole(int roleID, int empID);
    boolean insertRole(Role role);
    boolean updateRole(Role role, String name);
    boolean deleteRole(Role role);
    Day getDay(LocalDate d);
    Shift getShift(int id);
    Employee getEmployee(int id);
    Role getRole(int id);
    Vector<Role> getRoles();
    Vector<Employee> getEmployees();
    boolean idExists(int id);
    int shiftID();
    int roleID();
    Vector<Employee> getAvailableEmployees(int[][] avail);
    Shift getShift(LocalDate d, LocalTime startTime);
    Vector<Driver> getDriversList();
    boolean isDriver(int id);




}
