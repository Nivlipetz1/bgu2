package Transport.PL;


import Employees.BL.BL_IMPL;
import Employees.BackEnd.Employee;
import Program.OrderToTransport;
import Transport.BL.Run;
import Program.DriverInformations;
import Transport.BL.Transport;
import Transport.BL.Truck;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class NewTransport {


    public static boolean addOutcomingTransport(OrderToTransport ott)
    {
        Run r = new Run();
        Vector<String> noDriversForTheseLicenseTypes = new Vector<>();
        while(ott.getItemAmount() > 0)
        {
            int transportID = Run.transport.getLastTransportId();
            int truckPlateNumber = findMaxCapacityTruck(Run.truck.getAvailablesTruck(), noDriversForTheseLicenseTypes);
            if(truckPlateNumber == -1)
                return false;
            DriverInformations DI = new Employees.BL.BL_IMPL();
            int driverID = DI.getDriverInShift(Run.truck.getLicenceType(truckPlateNumber),ott.getStartTime(),ott.getDate());
            if(driverID == -1)
            {
                noDriversForTheseLicenseTypes.add(Run.truck.getLicenceType(truckPlateNumber));
                continue;
            }
            int handled = manageItemsTransport(truckPlateNumber, ott.getItemID(), ott.getItemAmount());
            ott.subtractAmount(handled);
            Run.transport.add(transportID,truckPlateNumber,driverID, ott.getSourceId(),ott.getDestId(),ott.getDate().toString(),ott.getStartTime().toString());
            Run.transOrder.add(transportID,ott.getOrderID(),ott.getItemID(),handled,0);
            DI.setDriverBusy(driverID,ott.getStartTime(),ott.getDate());
        }

        return true;

    }
    public static boolean returnTruck(int truckPlateNum)
    {
        return Run.truck.returnTruck(truckPlateNum);
    }

    private static int findMaxCapacityTruck(Vector<Integer> availableTrucks, Vector<String> noDriversForTheseLicenseTypes)
    {
        int maxWeight=0,maxWeightTruckPlateNumber=-1;
        int tmp;
        for(Integer i : availableTrucks)
        {
            if(noDriversForTheseLicenseTypes.contains(Run.truck.getLicenceType(i)))
                continue;
            else
            {
                maxWeightTruckPlateNumber=i;
                maxWeight = Run.truck.truckCapacity(maxWeightTruckPlateNumber);
                break;
            }
        }

        for(Integer i : availableTrucks)
        {
            if(noDriversForTheseLicenseTypes.contains(Run.truck.getLicenceType(i)))
                continue;
            if((tmp = Run.truck.truckCapacity(i)) > maxWeight)
            {
                maxWeight = tmp;
                maxWeightTruckPlateNumber = i;
            }
        }
        return maxWeightTruckPlateNumber;
    }


    private static int manageItemsTransport(int truckPlateNum, int itemID, int amount)
    {
       int handled = 0;
        while(handled != amount && Run.truck.canAddWeight(truckPlateNum,itemID,amount))
        {
            handled++;
            Run.truck.addWeight(truckPlateNum, itemID, 1);
        }
        Run.truck.setAvailability(truckPlateNum,1);

        return handled;
    }






}
