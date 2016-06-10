package Transport.PL;


import Employees.BL.BL_IMPL;
import Employees.BackEnd.Employee;
import Program.OrderToTransport;
import Transport.BL.Run;
import Program.DriverInformations;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class NewTransport {

    private static DriverInformations driverInformations = new BL_IMPL();
    private static int amountToTakeInNextOrder;
    private static int driverID ;
    private static int truckPlateNum;
    private static int numOfTrucks;
    private static OrderToTransport nextOrder;
    private static int orderID ;
    private static int source ;
    private static int dest ;
    private static LocalDate date ;
    private static LocalTime startTime ;
    private static boolean splitted ; // false is not splited, true is splitted
    private static int itemID ;
    private static int amount ;
    private static HashMap<Integer, Integer> itemsHashMap;
    private static Iterator iteratorItems;
    private static boolean first;
    private static Enumeration en;



    public NewTransport(){
        splitted = false;
        first=true;
        numOfTrucks=0;
    }

    public static  boolean addOutcomingTransport (Vector<OrderToTransport> vectorOrderToTransport){
        int transportID = Transport.BL.Run.transport.getLastTransportId();
        return addOutcomingTransport (vectorOrderToTransport, transportID);
    }

    public static boolean addOutcomingTransport(Vector<OrderToTransport> vectorOrderToTransport, int transportID) {
        boolean ans = false;
        int counterOrder = 0;
        int manageItemsTransport;

        boolean driverAndTruckAvailables = fillDriverAndTruckVariables();
        if (driverAndTruckAvailables){ // we have a driver and a truck to take the transport

            numOfTrucks++;
            Run.truck.setAvailability(truckPlateNum, 1); // the Truck is not Available Anymore

            if (splitted){ // in case we are BACK in the function due to splitted order
                manageItemsTransport = manageItemsTransport(transportID, orderID, truckPlateNum, itemID, amountToTakeInNextOrder); // fill with the remaining amount!

                if (manageItemsTransport == -1) { // mean that a problem occured
                    System.out.println("Error");
                } else if (splitted) { // means that we split the order AGAIN
                    return addOutcomingTransport (vectorOrderToTransport, transportID);
                }
                else { // we succeed to put the next order to a transport, and we have now to finish to transport the remaining items
                    while (iteratorItems.hasNext()) { // while do we have items to send in this order
                        Map.Entry mentry2 = (Map.Entry) iteratorItems.next();
                        itemID = (int) mentry2.getKey();
                        amount = (int) mentry2.getValue();


                        manageItemsTransport = manageItemsTransport(transportID, orderID, truckPlateNum, itemID, amount);
                        if (manageItemsTransport == -1) { // mean that a problem occured
                            System.out.println("Error");
                            break;
                        } else if (splitted) { // means that we split the order
                            return addOutcomingTransport (vectorOrderToTransport, transportID);
                        }
                        // else everything is OK and we already added the order to the transport in manageItemsTransport
                    }
                    counterOrder++;
                }

            }

            if (first) en = vectorOrderToTransport.elements();

            while (en.hasMoreElements()) { // while do we have some orders to transport
                nextOrder = (OrderToTransport) en.nextElement();

                fillItemsVariables(nextOrder);

                while (iteratorItems.hasNext()) { // while do we have items to send in this order
                    Map.Entry mentry2 = (Map.Entry) iteratorItems.next();
                    itemID = (int) mentry2.getKey();
                    amount = (int) mentry2.getValue();

                    manageItemsTransport = manageItemsTransport(transportID, orderID, truckPlateNum, itemID, amount);
                    if (manageItemsTransport == -1) { // mean that a problem occured
                        System.out.println("Error");
                        break;
                    } else if (splitted) { // means that we split the order
                        first = false;
                        return addOutcomingTransport (vectorOrderToTransport, transportID);
                    }
                    // else everything is OK and we already added the order to the transport in manageItemsTransport
                }
                counterOrder++;

            }
            // we add the transport to the DB
            ans = Transport.BL.Run.transport.add(transportID, truckPlateNum, driverID, source, dest, date.toString(), startTime.toString());
            if (ans) {
                if (counterOrder>1 && numOfTrucks >1)
                    System.out.println("The Transport Module Successfully added "+counterOrder+" Orders to "+numOfTrucks+" Trucks with TransportID: "+transportID);
                else if (counterOrder>1 && numOfTrucks ==1)
                    System.out.println("The Transport Module Successfully added "+counterOrder+" Orders to "+numOfTrucks+" Truck with TransportID: "+transportID);
                else if (counterOrder==1 && numOfTrucks ==1)
                    System.out.println("The Transport Module Successfully added "+counterOrder+" Order to "+numOfTrucks+" Truck with TransportID: "+transportID);
            }
        }

        return ans;
    }

    private static void fillItemsVariables(OrderToTransport nextOrder){
        orderID = nextOrder.getOrderID();
        source = nextOrder.getSourceId();
        dest = nextOrder.getDestId();
        date = nextOrder.getDate();
        startTime = nextOrder.getStartTime();

        HashMap<Integer, Integer> itemsHashMap = nextOrder.getItemsHashMap();
        Set set2 = itemsHashMap.entrySet();
        Iterator iteratorItems = set2.iterator(); // all the items in the same order
    }

    private static boolean fillDriverAndTruckVariables(){

        HashMap<Integer, String> availableTruckByTruckPlateNumAndLicenceType = Run.truck.getAvailableTruckByTruckPlateNumAndLicenceType();
        HashMap<Employee, Integer> hashMapAvailablesDrivers = driverInformations.getAllAvailablesDriversAccordingToAvailablesTrucks(availableTruckByTruckPlateNumAndLicenceType);

        boolean ans = (hashMapAvailablesDrivers!= null);
        if (ans) {
            Set set = hashMapAvailablesDrivers.entrySet();
            Iterator iteratorDriver = set.iterator();

            Map.Entry mentry = (Map.Entry) iteratorDriver.next();

            Employee employee = (Employee) mentry.getKey();
            int driverID = employee.getId();
            int truckPlateNum = (int) mentry.getValue();
        }
        return ans;

    }

    private static int manageItemsTransport(int transportID, int orderID, int truckPlateNum, int itemID, int amount){ // 0 successfull, 1 split, -1 problem
        boolean split = false, transOrder=true;
        int ans= -1;
        if (Run.truck.canAddWeight(truckPlateNum, itemID, amount)){ // if we can add all the items in the truck
            Run.truck.addWeight(truckPlateNum, itemID, amount);
            amountToTakeInNextOrder=0;
            splitted = false;
            transOrder =  Transport.BL.Run.transOrder.add(transportID, orderID, itemID, amount, 0);
            if (transOrder) ans = 0;
        }
        else { // in the case we don't have enough weight
            int weightOfASingleProduct = 5; //TODO !!!!
            int amountAllowed = Run.truck.amountMaxToTrans(truckPlateNum, itemID, weightOfASingleProduct); // we check how many product we can take on this transport
            Run.truck.addWeight(truckPlateNum, itemID, amountAllowed);
            amountToTakeInNextOrder = amount - amountAllowed;
            splitted = true;
            transOrder =  Transport.BL.Run.transOrder.add(transportID, orderID, itemID, amountAllowed, 1);
            if (transOrder) ans = 1;
        }
        return ans;
    }





}
