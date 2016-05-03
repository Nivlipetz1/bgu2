package Transport.PL;

import java.util.Scanner;

public class DriverMenu {
	private static Scanner in = new Scanner(System.in);
	private MainTransport mainTransport;
	
	public DriverMenu(MainTransport mainTransport){
		this.mainTransport=mainTransport;
	}
	
	protected void display(){
	int choice;
		
		System.out.println("Driver Options");
		System.out.println("Please enter your choice: ");
		System.out.println();
		System.out.println("1- Add a Driver");
		System.out.println("2- Remove a Driver");
		System.out.println("3- List of all Drivers");
		System.out.println("4- Back to Menu");
		System.out.println();
		
		choice = in.nextInt();
		
		switch (choice){
		case 1:
			addDriver();
			break;
		case 2:
			removeDriver();
			break;
		case 3:
			listDrivers();
			break;
		case 4: 
			mainTransport.displayMenu();
			break;
	}
	}	
	
	private void addDriver(){
		boolean ans;
		int id, licenceNum;
		String name, licenceType;
		
		System.out.println("Please enter a Driver ID: ");
		id = in.nextInt();
		System.out.println("Please enter a Driver Name: ");
		name = in.nextLine();
		name = in.nextLine();
		System.out.println("Please enter a Licence Number: ");
		licenceNum = in.nextInt();
		System.out.println("Please enter a Licence Type: ");
		licenceType = in.nextLine();
		licenceType = in.nextLine();
		
		ans = Transport.BL.Run.driver.add(id, name, licenceNum, licenceType);
		
		if (ans){
			System.out.println("Driver Added Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}

	}
	
	private void removeDriver(){
		int choice;
		boolean ans;
		Transport.BL.Run.driver.listOfDrivers();
		System.out.println("Please Enter the ID of the Driver you want to Remove: ");
		choice = in.nextInt();
		ans = Transport.BL.Run.driver.remove(choice);
		
		if (ans){
			System.out.println("Driver Removed Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}
	}
	
	private void listDrivers(){
		System.out.println("All the Drivers are: ");
		System.out.println();
		Transport.BL.Run.driver.listOfDrivers();;
		
		mainTransport.optionDone();
	}
	
	
	
	
	
	
	
	
	
	
}
