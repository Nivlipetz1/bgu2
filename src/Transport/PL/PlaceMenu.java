package Transport.PL;

import java.util.Scanner;

public class PlaceMenu {
	private static Scanner in = new Scanner(System.in);

	private MainTransport mainTransport;

	public PlaceMenu(MainTransport mainTransport){
		this.mainTransport=mainTransport;
	}

	protected void display(){
		int choice;

		System.out.println("Place Options");
		System.out.println("Please enter your choice: ");
		System.out.println();
		System.out.println("1- Add a Place");
		System.out.println("2- Remove a Place");
		System.out.println("3- List of all Places");
		System.out.println("4- Back to Menu");
		System.out.println();

		choice = in.nextInt();

		switch (choice){
		case 1:
			addPlace();
			break;
		case 2:
			removePlace();
			break;
		case 3:
			listPlaces();
			break;
		case 4: 
			mainTransport.displayMenu();
			break;
		}
	}

	private void addPlace(){
		boolean ans;
		int addressID = Transport.BL.Run.place.getLastPlaceId(), phoneNum;
		String placeName, contactName;

		System.out.println("Please enter a Place Name: ");
		placeName = in.nextLine();
		placeName = in.nextLine();
		System.out.println("Please enter the Name of the "+placeName+"'s Contact: ");
		contactName = in.nextLine();
		System.out.println("Please enter the Phone Number of "+contactName+": ");
		phoneNum = in.nextInt();


		ans = Transport.BL.Run.place.add(addressID, placeName, phoneNum, contactName);

		if (ans){
			System.out.println("Place Added Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}


	}

	private void removePlace(){
		int choice;
		boolean ans;
		Transport.BL.Run.place.listOfPlaces();;
		System.out.println("Please Enter the ID of the Place you want to Remove: ");
		choice = in.nextInt();
		ans = Transport.BL.Run.place.remove(choice);

		if (ans){
			System.out.println("Place Removed Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}
	}

	private void listPlaces(){
		System.out.println("All the Places are: ");
		System.out.println();
		Transport.BL.Run.place.listOfPlaces();;

		mainTransport.optionDone();
	}








}
