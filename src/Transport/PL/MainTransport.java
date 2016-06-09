package Transport.PL;

import java.util.Scanner;

public class MainTransport {
	private static Scanner in = new Scanner(System.in);
	private static ItemMenu itemMenu;
	private static TransportMenu transportMenu;
	private static TruckMenu truckMenu;
	private static PlaceMenu placeMenu;
	private static int clearance=-1;
	
	public MainTransport(){
		itemMenu = new ItemMenu(this);
		transportMenu = new TransportMenu(this);
		truckMenu = new TruckMenu(this);
		placeMenu = new PlaceMenu(this);
	}
	
	public void display(int clear){
		System.out.println();
		clearance=clear;
		displayMenu();
	}
	
	protected void displayMenu(){
		int choice;
		
		System.out.println("Transport Layer");
		System.out.println("Please enter your choice: ");
		System.out.println();
		System.out.println("1- Transport Options");
		System.out.println("2- Truck Options");
		System.out.println("3- Items Options");
		System.out.println("4- Places Options");
		System.out.println("5- Return to Super-LEE options");
		System.out.println("6- Exit");
		System.out.println();
		
		choice = in.nextInt();
		
		switch (choice){
		case 1:
			transportMenu.display();
			break;
		case 2:
			truckMenu.display();
			break;
		case 3:
			itemMenu.display();
			break;
		case 4:
			placeMenu.display();
		case 5:
			break;
		case 6:
			return;

		default:
			System.out.println("Invalid input");
			System.out.println();
			displayMenu();
		}

	}
	
	protected void exit(){
		Transport.DL.CloseDB.closeConnection();
		System.exit(1);
	}
	
	protected void optionDone(){
		int choice;
		System.out.println();
		System.out.println("Press 1 to Display the Menu");
		System.out.println("Press 2 to Exit");
		choice = in.nextInt();
		
		if (choice ==1){
			displayMenu();
		}
		else if (choice ==2){
			return;
		}
		else {
			System.out.println("The input you entered is Wrong");
			optionDone();
		}
	}
	
}
