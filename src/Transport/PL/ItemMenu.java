package Transport.PL;

import Transport.BL.Run;

import java.util.Scanner;

public class ItemMenu {
	private static Scanner in = new Scanner(System.in);

	private MainTransport mainTransport;

	public ItemMenu(MainTransport mainTransport){
		this.mainTransport=mainTransport;
	}

	protected void display(){
		int choice;

		System.out.println("Item Options");
		System.out.println("Please enter your choice: ");
		System.out.println();
		System.out.println("1- Add an Item");
		System.out.println("2- Remove an Item");
		System.out.println("3- List of all Items");
		System.out.println("4- Back to Menu");
		System.out.println();

		choice = in.nextInt();

		switch (choice){
		case 1:
			addItem();
			break;
		case 2:
			removeItem();
			break;
		case 3:
			listItems();
			break;
		case 4: 
			mainTransport.displayMenu();
			break;
		}
	}

	private void addItem(){
		boolean ans;
		int itemID;
		int weight;
		String itemName;

		itemID = Run.item.getLastItemId();
		System.out.println("Please enter an Item Name: ");
		itemName = in.nextLine();
		itemName = in.nextLine();
		System.out.println("Please enter the Weight of a Single "+itemName+": ");
		weight = in.nextInt();


		ans = Transport.BL.Run.item.add(itemID, itemName, weight);

		if (ans){
			System.out.println("Item Added Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}


	}

	private void removeItem(){
		int choice;
		boolean ans;
		Transport.BL.Run.item.listOfItems();;
		System.out.println("Please Enter the ID of the Item you want to Remove: ");
		choice = in.nextInt();
		ans = Transport.BL.Run.item.remove(choice);

		if (ans){
			System.out.println("Item Removed Successfully!");
			mainTransport.optionDone();
		}
		else{
			System.out.println("A Problem occured!");
			mainTransport.optionDone();
		}
	}

	private void listItems(){
		System.out.println("All the Items are: ");
		System.out.println();
		Transport.BL.Run.item.listOfItems();;

		mainTransport.optionDone();
	}








}
