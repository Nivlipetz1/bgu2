package Program;

import Employees.BackEnd.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by ariel on 4/5/2016.
 */
public interface DriverInformations {
    // return boolean in order to know if there is an Available Driver do Drive truck with Licence type : licenceType
    boolean isDriverAvailable (String licenceType, LocalTime time, LocalDate date);
    // return a list with all free Drivers with licenceType
    Vector<Employee> getDriverList (String licenceType, LocalTime time, LocalDate date);
    void setDriverBusy (int employeeID, LocalTime time, LocalDate date);
    boolean isStoreKeeperAvailable (LocalTime time, LocalDate date);
    Vector<String> getDriversTypesLicencesAvailables();
    HashMap<Employee, Integer> getAllAvailablesDriversAccordingToAvailablesTrucks(HashMap <Integer, String> availableTruckByTruckPlateNumAndLicenceType); // The first element is the Available Driver, and the Second is the TruckID

}
