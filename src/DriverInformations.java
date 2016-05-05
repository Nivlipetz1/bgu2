import Employees.BackEnd.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by ariel on 4/5/2016.
 */
public interface DriverInformations {
    boolean isEmployeeAvailable (String licenceType, LocalTime time, LocalDate date);
    Vector<Employee> getDriverList (String licenceType, LocalTime time, LocalDate date);
    void setDriverBusy (int employeeID);
    boolean isStoreKeeperAvailable (LocalTime time, LocalDate date);
    void setStoreKeeperBusy (int employeeID);
}
