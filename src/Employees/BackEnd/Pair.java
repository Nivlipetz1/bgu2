package Employees.BackEnd;

/**
 * Created by omer on 18/04/16.
 */
public class Pair {
    Role role;
    Employee employee;

    public Pair(Role role, Employee employee) {
        this.role = role;
        this.employee = employee;
    }

    public Role getRole() {
        return role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
