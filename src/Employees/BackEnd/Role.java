package Employees.BackEnd;

/**
 * Created by omer on 18/04/16.
 */
public class Role {
    private int ID;
    private String name;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }
}
