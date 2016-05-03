package Employees.BackEnd;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by omer on 18/04/16.
 */
public class Shift {
    private int ID;
    private LocalTime startTime;
    private LocalTime endTime;
    private int duration;
    private LocalDate date;
    private Employee manager;
    private Vector<Pair>roles;
    private HashMap<Integer,Integer> amountOfRoles;
    private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    private static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    public Shift(int ID, LocalTime startTime, LocalTime endTime, int duration,
                 LocalDate date, Employee manager, Vector<Pair> roles, HashMap<Integer,Integer> amountOfRoles) {
        this.ID = ID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.date = date;
        this.manager = manager;
        this.roles = roles;
        this.amountOfRoles = amountOfRoles;
    }

    public int getID(){
        return ID;
    }

    public String getStartTime() {
        return startTime.format(formatterTime);
    }

    public String getEndTime() {
        return endTime.format(formatterTime);
    }

    public int getDuration() {
        return duration;
    }

    public String getDate() {
        return date.format(formatterDate);
    }

    public Employee getManager() {
        return manager;
    }

    public Vector<Pair> getRoles() {
        return roles;
    }

    public HashMap<Integer,Integer> getAmountOfRoles(){
        return amountOfRoles;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setRoles(Vector<Pair> roles) {
        this.roles = roles;
    }

    public void setAmountOfRoles(HashMap<Integer, Integer> amountOfRoles) {
        this.amountOfRoles = amountOfRoles;
    }


    public void addRole(Role role){
        roles.add(new Pair(role,null));
    }

    public boolean enroll(String role,Employee emp){
        for(Pair pi : roles){
            if (pi.getRole().equals(role) && pi.getEmployee()==null){
                pi.setEmployee(emp);
                return true;
            }
        }
        return false;
    }

    public void print(){
        System.out.print("ID: ");
        System.out.println(ID);
        System.out.print("Start Time: ");
        System.out.println(startTime);
        System.out.print("End Time: ");
        System.out.println(endTime);
        System.out.print("Duration: ");
        System.out.println(duration);
        System.out.print("Manager: ");
        System.out.println(manager.getFirstName()+" "+manager.getLastName());
        System.out.println("Employees On Shift: ");
        for(Pair pair : roles){
            System.out.print("\t\tPosition: ");
            System.out.print(pair.getRole().getName());
            System.out.print("\t\tEmployee: ");
            System.out.println(pair.getEmployee().getFirstName()+" "+pair.getEmployee().getLastName());
        }
    }
}