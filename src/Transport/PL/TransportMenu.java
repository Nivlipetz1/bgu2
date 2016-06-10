package Transport.PL;

import Employees.BL.BL_IMPL;
import Employees.BL.IBL;
import Employees.BackEnd.Driver;
import Employees.BackEnd.Employee;
import Program.DriverInformations;
import Program.OrderToTransport;
import Transport.BL.Run;
import Transport.BL.Truck;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransportMenu {
	private static Scanner in = new Scanner(System.in);
	private MainTransport mainTransport;
	private static int lastDest;
	private static int lastSource;
	private static int day;
	private static int driverID, truckPlateNum;
	private static int amountToTakeInNextOrder;
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
	private static DriverInformations driverInformations = new BL_IMPL();


	public TransportMenu(MainTransport mainTransport){
		this.mainTransport=mainTransport;
	}
	
	protected void display(){
	int choice;
		
		System.out.println("Transport Options");
		System.out.println("Please enter your choice: ");
		System.out.println();
		//System.out.println("1- Add an Outcoming Transport");
		//System.out.println("2- Add an Incoming Transport");
		System.out.println("3- List of all Transports");
		System.out.println("4- Back to Menu");
		System.out.println();
		
		choice = in.nextInt();
		
		switch (choice){
		case 1:
			checkConditions();
			break;
		case 2:
			addIncomingTransport();
			break;
		case 3:
			listTransports();
			break;
		case 4:
			mainTransport.displayMenu();
			break;
	}
	}

	private void addIncomingTransport(){
		LocalDate date;
		LocalTime startTime;

		System.out.println("Step 1/2 - Please enter the Date of the Incoming Transport: (dd/MM/yyyy)");
		while(true) {
			try {
				date = LocalDate.parse(in.next(), dateFormatter);
				break;
			} catch (Exception e) {
				System.out.println("Bad Input! Please try again: ");
				System.out.println();
			}
		}

		System.out.println("Step 2/2 - Please enter the Hour of the Incoming Transport: (HH:mm)");
		while(true) {
			try {
				startTime = LocalTime.parse(in.next(), timeFormatter);
				break;
			} catch (Exception e) {
				System.out.println("Bad Input! Please try again: ");
				System.out.println();
			}
		}

		System.out.println("Just a moment, we are checking with the Department Employee if a Store Keeper is Available to get the Transport");
		boolean canGetTransport = driverInformations.isStoreKeeperAvailable(startTime, date);
		if (canGetTransport){
			System.out.println("Ok, we have a Store Keeper to Receive the Transport!");
			System.out.println();
			System.out.println("Do the Truck is Super-Lee's Truck?");
			System.out.println("1-Yes, 2-No");
			int choice = in.nextInt();
			if (choice ==1){
				System.out.println("Please enter the licence Number of the Truck");
				int truckPlateNum = in.nextInt();
				if (Run.truck.contains(truckPlateNum)){
					Run.truck.setAvailability(truckPlateNum, 0);
					System.out.println("Ok, we Successfully register the Truck Back");
				}
				else{
					System.out.println("Sorry, the Licence Num is not a part of our DataBase");
				}
			}
			else{
				System.out.println("Thank you!!");
			}
		}
		else {
			System.out.println("Sorry, none of our Store Keeper is Available to get the Transport!");
		}
		mainTransport.optionDone();
	}

	private void checkConditions() {
				int confirm = -1;

				while (day < 1 || day > 7) {
					System.out.println("Please enter The Actual Day: ");
					System.out.println("1- Sunday, 2- Monday, 3- Tuesday, 4-Wednesday, 5-Thursday, 6-Friday, 7-Saturday");
					day = in.nextInt();
				}

				if (day != 1 && day != 4) {
					while (confirm < 1 || confirm > 2) {
						System.out.println("Did the Stock is Out? ");
						System.out.println("1- Yes, 2- No");
						confirm = in.nextInt();
					}
					if (confirm == 2) {
						System.out.println("Sorry, you can not make a Transport When the Stock is not Out");
						mainTransport.optionDone();
					} else {
						addTransport(day);
					}
				} else {
					addTransport(day);
				}

		}


	private void addTransport(int day){
		Vector <Integer> vectorDest = new Vector<Integer>();
		HashMap <Integer, Integer> itemsHashMap = new HashMap <Integer, Integer>();
		LocalDate date;
		LocalTime startTime;

		int source, tmpDest, itemID, counter=0;
		boolean ans = true, order=true, transOrder=true;

		System.out.println("Step 1/7 - Please enter the Date: (dd/mm/yy)");
		while(true) {
			try {
				date = LocalDate.parse(in.next(), dateFormatter);
				break;
			} catch (Exception e) {
				System.out.println("Bad Input! Please try again: ");
				System.out.println();
			}
		}

		System.out.println("Step 2/7 - Please enter the leaving time: (hh:mm)");
		while(true) {
			try {
				startTime = LocalTime.parse(in.next(), timeFormatter);
				break;
			} catch (Exception e) {
				System.out.println("Bad Input! Please try again: ");
				System.out.println();
			}
		}

		System.out.println("Step 3/7 - Please enter a Truck Plate Number: ");
		truckPlateNum = truckExist();

		adjustDriver(truckPlateNum, startTime, date);

		System.out.println("Step 5/7 - Please enter the Source Address ID: ");
		source = placeExist();
		lastSource = source;

		System.out.println("Step 6/7 - Please enter the Destination Address ID: ");
		tmpDest=placeExist();

		if (vectorDest.contains(tmpDest) || tmpDest == source){
			System.out.println("Error, this address is already on the Transport Travel");
		}
		else {
			lastDest = tmpDest;
			vectorDest.add(tmpDest);
		}

		otherDestinations (vectorDest);

		System.out.println("Step 7/7 - Please enter an Item ID: ");
		itemID=itemExist();
		itemsNumber(itemID, itemsHashMap, truckPlateNum);
		otherItems(itemsHashMap, truckPlateNum);


		@SuppressWarnings("rawtypes")
		Enumeration en = vectorDest.elements();

		int transportID = Transport.BL.Run.transport.getLastTransportId();

		Run.truck.setAvailability(truckPlateNum, 1); // the Truck is not Available Anymore

		ans = Transport.BL.Run.transport.add(transportID, truckPlateNum, driverID, source, lastDest, date.toString(), startTime.toString());
		if (ans){
			while (en.hasMoreElements() && order && transOrder){
				int dest = (int)en.nextElement();
				int orderID = Transport.BL.Run.order.getLastOrderId();
				order = Transport.BL.Run.order.add(orderID, source, dest);

				Set set = itemsHashMap.entrySet();
			    Iterator iterator = set.iterator();

			    while(iterator.hasNext()) {
			        Map.Entry mentry = (Map.Entry)iterator.next();
					transOrder = Transport.BL.Run.transOrder.add(transportID, orderID, (int)mentry.getKey(), (int)mentry.getValue(), 0);
			      }
				counter++;
			}

			if (order && transOrder){
				System.out.println(counter + " Orders Added Successfully!");
				System.out.println("Transport Added Successfully!");
				mainTransport.optionDone();
			}
			else{
				System.out.println("A Problem occured!");
				mainTransport.optionDone();
			}
		}

	}
	
	private void adjustDriver (int truckPlateNum, LocalTime time, LocalDate date){
		IBL bl_impl = new BL_IMPL();
		int driverID = -1;
		System.out.println("One moment, we are Checking if we have Some Driver to Drive this Truck... ");
		String licenceType = Run.truck.getLicenceType(truckPlateNum);
		boolean isDriverAvailable = driverInformations.isDriverAvailable(licenceType, time, date);
		if (isDriverAvailable){
			Vector<Employee> vectorEmployee = driverInformations.getDriverList(licenceType, time, date);
			int driversAvailablesLength = vectorEmployee.size();
			System.out.println("Nice, "+driversAvailablesLength+" Drivers Can Drive Your Selected Truck!");
			if (driversAvailablesLength > 1) {
				System.out.println("Do you want to Choose a Driver, or Do You Prefer That We Will Choose One For You?");
				System.out.println("1- I Want to Choose, 2-Choose Automatically");
				int choice = in.nextInt();
				driverID=-1;
				if (choice ==1){
					Enumeration en = vectorEmployee.elements();
					System.out.println("Please enter the Id of the Driver, you Want to Drive");
					System.out.println();
					while(en.hasMoreElements()){
						Employee tempDriver = (Driver)en.nextElement();
						System.out.println("ID = "+tempDriver.getId()+" FirstName:"+ tempDriver.getFirstName()+" LastName:"+tempDriver.getLastName());
					}
					driverID = in.nextInt();
					if (bl_impl.idExists(driverID)){
						driverInformations.setDriverBusy(driverID,time,date);
						System.out.println("The Driver "+bl_impl.getEmployee(driverID).getFirstName()+" "+bl_impl.getEmployee(driverID).getLastName()+" was Successfully Choosen");
					}
					else{
						System.out.println("Error, The ID you Entered is Wrong");
						driverID = -1;
					}
				}
				if (driverID== -1){
					Employee employeeToDrive = vectorEmployee.get(0);
					driverID = employeeToDrive.getId();
					driverInformations.setDriverBusy(driverID,time,date);
					System.out.println();
					System.out.println("The Driver "+employeeToDrive.getFirstName()+" "+employeeToDrive.getLastName()+" was Successfully Choosen");
				}
			}
			else if (driversAvailablesLength == 1){
				Employee employeeToDrive = vectorEmployee.get(0);
				driverID = employeeToDrive.getId();
				driverInformations.setDriverBusy(driverID,time,date);
				System.out.println();
				System.out.println("The Driver "+employeeToDrive.getFirstName()+" "+employeeToDrive.getLastName()+" was Successfully Choosen");
			}
		}
		else{
			System.out.println();
			System.out.println("Sorry We don't have any Available Driver with the Type Licence Of the Truck You Choose");
			System.out.println("Press 1 - If You want to Choose an Auther Truck");
			System.out.println("Press 2 - If You want to Return to the Menu");
			int choice = in.nextInt();
			if (choice == 1){
				chooseAnAutherTruck (time, date);
			}
			else {
				System.out.println();
				mainTransport.displayMenu();
			}

		}
	}

	public void chooseAnAutherTruck (LocalTime time, LocalDate date){
		System.out.println("Step 3/7 - Please enter a Truck Plate Number: ");
		truckPlateNum = truckExist();
		adjustDriver(truckPlateNum, time, date);
	}
	
	
	private void otherDestinations (Vector <Integer> vectorDest){
		boolean otherDest = true;
		while (otherDest){
			int choice, tmpDest;
			System.out.println("Do you want to enter other Destinations?: ");
			System.out.println("1- Yes, 2- No");
			choice = in.nextInt();
			switch (choice){
			case 1:
				System.out.println("Step 7/7 - Please enter the Destination Address ID: ");
				
				tmpDest=placeExist();
				
				if (vectorDest.contains(tmpDest) || lastSource == tmpDest){
					System.out.println("Error, this address is already on the Transport Travel");
				}
				else {
					lastDest = tmpDest;
					vectorDest.add(tmpDest);
				}
				break;
			case 2:
				otherDest=false;
				break;
			default:
				System.out.println("Invalid input");
				System.out.println();
				break;
			}

		}//while
	}
	
	private void canDrive (int driverID, int truckPlateNum){
		boolean ans;
		ans = Transport.BL.Run.truck.canDrive(driverID, truckPlateNum);
		if (!ans){
			System.out.println();
			System.out.println("Error the Driver have not the licence for driving this truck");
			mainTransport.optionDone();
		}
		else{
			System.out.println("Fine, the Driver have the licence for driving this truck!");
			System.out.println();
		}
	}
	
	private int placeExist (){
		int addressID = in.nextInt();
		
		while (!Transport.BL.Run.place.contains(addressID)){
			System.out.println("Error the Address ID don't exist in the DataBase");
			int choice;
			System.out.println();
			System.out.println("Press 1 to enter a new Address ID");
			System.out.println("Press 2 to Display the Address List");
			System.out.println("Press 3 to Return to the Menu");
			System.out.println("Press 4 to Exit");

			choice = in.nextInt();
			switch (choice){
			case 1:
				addressID = in.nextInt();
				break;
			case 2:
				System.out.println();
				Transport.BL.Run.place.listOfPlaces();
				System.out.println();
				System.out.println("Please enter an Address ID: ");
				addressID = in.nextInt();
				break;
			case 3:
				System.out.println();
				mainTransport.displayMenu();
				break;
			case 4:
				mainTransport.exit();
			default:
				System.out.println("Invalid input");
				System.out.println();
				break;
			}
		}
		System.out.println("The address "+ Transport.BL.Run.place.getAddressName(addressID)+" was selected");
		System.out.println();
		return addressID;
	}
	
	private void otherItems (HashMap <Integer, Integer> itemsHashMap, int truckPlateNum){
		boolean otherDest = true;
		while (otherDest){
			int choice, tmpItem;
			System.out.println("Do you want to enter other Items?: ");
			System.out.println("1- Yes, 2- No");
			choice = in.nextInt();
			switch (choice){
			case 1:
				System.out.println("Step 5/7 - Please enter the Item ID: ");
				
				tmpItem =itemExist();
				
				if (itemsHashMap.containsKey(tmpItem) ){
					System.out.println("Error, this item is already on the Transport Items");
				}
				else {
					lastDest = tmpItem;
					itemsNumber (tmpItem, itemsHashMap, truckPlateNum);
				}
				break;
			case 2:
				otherDest=false;
				break;
			default:
				System.out.println("Invalid input");
				System.out.println();
				break;
			}

		}//while
	}

	
	private int itemExist (){
		int itemID = in.nextInt();
		
		while (!Transport.BL.Run.item.contains(itemID)){
			System.out.println("Error the Item ID don't exist in the DataBase");
			int choice;
			System.out.println();
			System.out.println("Press 1 to enter a new Item ID");
			System.out.println("Press 2 to Display the Address List");
			System.out.println("Press 3 to Return to the Menu");
			System.out.println("Press 4 to Exit");

			choice = in.nextInt();
			switch (choice){
			case 1:
				itemID = in.nextInt();
				break;
			case 2:
				System.out.println();
				Transport.BL.Run.item.listOfItems();
				System.out.println();
				System.out.println("Please enter an Item ID: ");
				itemID = in.nextInt();
				break;
			case 3:
				System.out.println();
				mainTransport.displayMenu();
				break;
			case 4:
				mainTransport.exit();
			default:
				System.out.println("Invalid input");
				System.out.println();
				break;
			}
		}
		System.out.println("The item "+ Transport.BL.Run.item.getItemName(itemID)+" was selected");
		System.out.println();
		return itemID;
	}
	
	private static void itemsNumber (int itemID, HashMap <Integer, Integer> itemsHashMap, int truckPlateNum){
		boolean canAdd=false, add=false;
		int amount = 0, choice;
		String itemName = Transport.BL.Run.item.getItemName(itemID);
		System.out.println();
		System.out.println("Do you want to Add more than 1 "+itemName+"?");
		System.out.println("1- Yes, 2- No");
		choice = in.nextInt();
		switch (choice){
		case 1:
			System.out.println("How much "+itemName+" do you Want to Add?");
			amount= in.nextInt();
			canAdd = Transport.BL.Run.truck.canAddWeight(truckPlateNum, itemID, amount);
			if (canAdd) {
				add = Transport.BL.Run.truck.addWeight(truckPlateNum, itemID, amount);
				if(add){
					itemsHashMap.put(itemID, amount);
					System.out.println("The truck has enough weight to take "+ amount +" "+itemName);
					System.out.println(amount +" "+itemName+" were successfully added to the Order!");
					System.out.println();
				}
				else{
					System.out.println("The truck has enough weight to take "+ amount +" "+itemName);
					System.out.println("A problem occured with the DB in order to take "+ amount +" "+itemName);
					System.out.println();
				}
			}
			else{
				System.out.println("The truck has not enough weight to take "+ amount +" "+itemName);
				Transport.BL.Run.truck.weightInformations(truckPlateNum);
				System.out.println();
				itemsNumber(itemID, itemsHashMap, truckPlateNum);
			}

			break;
		case 2:
			canAdd = Transport.BL.Run.truck.canAddWeight(truckPlateNum, itemID, 1);
			if (canAdd) {
				add = Transport.BL.Run.truck.addWeight(truckPlateNum, itemID, 1);
				if(add){
					itemsHashMap.put(itemID, 1);
					System.out.println("The truck has enough weight to take 1 "+itemName);
					System.out.println("1 "+itemName+" was successfully added to the Order!");
					System.out.println();
				}
				else{
					System.out.println("A problem occured with the DB in order to take 1 "+itemName);
					System.out.println();
				}
			}
			else{
				System.out.println("The truck has not enough weight to take 1 "+itemName);
				Transport.BL.Run.truck.weightInformations(truckPlateNum);
				System.out.println();
				itemsNumber(itemID, itemsHashMap, truckPlateNum);
			}

			break;
		default:
			System.out.println("Invalid input");
			System.out.println();
			break;
		}
	}



	private int truckExist (){
		int truckPlateNum = in.nextInt();
		
		while (!Transport.BL.Run.truck.contains(truckPlateNum)){
			System.out.println("Error the Plate Number don't exist in the DataBase");
			int choice;
			System.out.println();
			System.out.println("Press 1 to enter a new Plate Number");
			System.out.println("Press 2 to Display the Truck List");
			System.out.println("Press 3 to Display the Licence Types You Can Choose (According to Availables Drivers)");
			System.out.println("Press 4 to Return to the Menu");
			System.out.println("Press 5 to Exit");

			choice = in.nextInt();
			switch (choice){
			case 1:
				truckPlateNum = in.nextInt();
				break;
			case 2:
				System.out.println();
				Transport.BL.Run.truck.listOfTrucks();
				System.out.println();
				System.out.println("Please enter a Truck Plate Number: ");
				truckPlateNum = in.nextInt();
				break;
			case 3:
				Vector<String> trucksLicencesAvailables = Run.truck.vectorLicenceTypeAvailablesToTransport();
				Enumeration en = trucksLicencesAvailables.elements();
				while(en.hasMoreElements()) {
					String tempType = (String) en.nextElement();
					System.out.println("Type: "+tempType);
				}
				break;
			case 4:
				System.out.println();
				mainTransport.displayMenu();
				break;
			case 5:
				mainTransport.exit();
			default:
				System.out.println("Invalid input");
				System.out.println();
				break;
			}
		}
		return truckPlateNum;
		
	}
	
	
	private void listTransports(){
		System.out.println("All the Transports are: ");
		System.out.println();
		Transport.BL.Run.transport.listOfTransport();
		
		mainTransport.optionDone();
	}
	
	
	
}
