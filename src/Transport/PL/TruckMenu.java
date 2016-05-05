package Transport.PL;

import java.util.Scanner;

public class TruckMenu {
	private static Scanner in = new Scanner(System.in);

	private MainTransport mainTransport;

	public TruckMenu(MainTransport mainTransport){
		this.mainTransport=mainTransport;
	}

	protected void display(){
		int choice;

		System.out.println("Truck Options");
		System.out.println("Please enter your choice: ");
		System.out.println();
		System.out.println("1- Add Truck");
		System.out.println("2- Remove Truck");
		System.out.println("3- List of all Trucks");
		System.out.println("4- Back to Menu");
		System.out.println();

		choice = in.nextInt();

		switch (choice){
		case 1:
			addTruck();
			break;
		case 2:
			removeTruck();
			break;
		case 3:
			listTrucks();
			break;
		case 4: 
			mainTransport.displayMenu();
			break;

		}	
	}

	private void addTruck(){
		int truckPlateNum, weightNeto, maxWeight;
		String licenceType, model, color;
		boolean ans;

		System.out.println("Please enter a Plate Number: ");
		truckPlateNum = in.nextInt();
		System.out.println("Please enter a Model: ");
		model = in.nextLine();
		model = in.nextLine();
		System.out.println("Please enter a Licence Type: ");
		licenceType = in.nextLine();
		System.out.println("Please enter a Color: ");
		color = in.nextLine();
		System.out.println("Please enter an Initial Weight: ");
		weightNeto = in.nextInt();
		System.out.println("Please enter a Maximum Weight: ");
		maxWeight = in.nextInt();

		while (maxWeight < weightNeto){
			System.out.println("Error, the maximum weight must be smaller than the initial weight who is "+ weightNeto);
			System.out.println("Please enter a Maximum Weight: ");
			maxWeight = in.nextInt();
		}

		ans = Transport.BL.Run.truck.add(truckPlateNum, licenceType, model, color, weightNeto, maxWeight, weightNeto, 0);

		if (ans){
			System.out.println("Truck Added Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}
	}



	private void removeTruck(){
		int choice;
		boolean ans;
		Transport.BL.Run.truck.listOfTrucks();
		System.out.println("Please Enter the Plate Number of the Truck you want to Remove: ");
		choice = in.nextInt();
		ans = Transport.BL.Run.truck.remove(choice);

		if (ans){
			System.out.println("Truck Removed Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}

	}

	private void listTrucks(){
		System.out.println("All the Trucks are: ");
		System.out.println();
		Transport.BL.Run.truck.listOfTrucks();

		mainTransport.optionDone();
	}




}
