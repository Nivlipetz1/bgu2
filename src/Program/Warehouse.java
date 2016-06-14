package Program;

import Stock.modul.StockReport;
import Stock.modul.SuppliersModule;
import Stock.modul.WarehouseModule;
import Stock.common.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class Warehouse
{
	private static final int ENTIRE_STOCK = -1;
	public static boolean running = true;
	private static Scanner reader = new Scanner(System.in);
	private static WarehouseModule wh = new WarehouseModule();

	public static void retryOrders()
	{
		wh.retryOrders();
	}

	private enum MainMenu { Reports, AddItems, TakeItems, IncomingTransport, LogOut, Quit };
	private enum ReportsMenu { EntireStock, OneCategory, RecursiveOneCategory, PendingOrders,FaultyItems, MainMenu, Quit };
	private static int clearance =-1;
	
	public static void run(int cl)
	{
		clearance = cl;
		Menu();
	}
	
	public static void Menu()
	{
		running=true;
		int choice = 0;
		while(running)
		{
			PrintMainMenu();
			choice = getInt();
			switch(choice)
			{
			case -1:
				break;
			case 0:
				ReportsMenu();
				break;
			case 1:
				AddItems();
				break;
			case 2:
				TakeItems();
				break;
			case 3:
				incomingTransport();
				break;
			case 4:
				running = false;
				break;
			case 5:
				System.exit(0);
			default:
				System.out.println("Index is out of bounds.");
				break;
			}
		}
	}

	private static void incomingTransport()
	{
		System.out.println("Insert Transport ID: ");
		System.out.print(">> ");
		wh.insertTransportToWarehouse(reader.nextInt());
		System.out.println("Items Successfully Loaded To The Warehouse!");
	}

	private static void TakeItems()
	{
		wh.UpdateFaulty();
		System.out.println("How many items would you like to take?");
		System.out.print(">");
		int count = getInt();
		for (int i = 0; i < count; i++)
		{
			System.out.print("Name: ");
			String name = reader.next();
			System.out.println("How many "+name+" units?");
			System.out.print(">");
			int amount = getInt();
			Product p = new Product(wh.getIDfromName(name), null, 0);
			if(wh.TakeItems(p, amount))
			{
				System.out.println("Taken: "+p.getID()+"	|	"+name+"	|	"+amount+"	|");
			}
			else
				System.out.println("Not enough"+name+" in stock. Please try again.");
			
		}
		
		StockReport sr = wh.CheckStock();
		SuppliersModule Suppl = new SuppliersModule();
		if(sr!=null)
			Suppl.makeOrders(sr);
		
	}
	private static void AddItems()
	{
		LinkedList<Product> products = new LinkedList<Product>();
		System.out.println("How many items would you like to add?");
		System.out.print(">");
		int count = getInt();
		for (int i = 0; i < count; i++)
		{
			System.out.print("Name: ");
			String name = reader.next();
			System.out.print("Expiration date: (dd.MM.yy) ");
			SimpleDateFormat sdfmt = new SimpleDateFormat("dd.MM.yy");
			Date date = null;
			try
			{
				date = sdfmt.parse( reader.next() );
			} catch (ParseException e)
			{
				System.out.println("Invalid date! aborting...");
				return;
			}
			System.out.print("Amount: ");
			int amount = getInt();
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			products.add(new Product(wh.getIDfromName(name), c, amount));
		}
		wh.PutItems(products);
	}
	private static void ReportsMenu()
	{
		if(clearance !=1)
		{
			System.err.println("You are not authorized to produce reports.");
			System.err.println("Log in as a warehouse administrator to do so.");
			return;
		}
		int choice = 0;
		while(running)
		{
			PrintReportsMenu();
			choice = getInt();
			int catID;
			switch(choice)
			{
			case -1:
				break;
			case 0: //Entire Stock
				PrintReport(ENTIRE_STOCK, false);
				break;
			case 1: //One Category. hence the false value.
				System.out.println("Please select category:");
				catID = PrintAndReadCategory();
				PrintReport(catID, false);
				break;
			case 2: //Recursive, hence the true value.
				System.out.println("Please select category:");
				catID = PrintAndReadCategory();
				PrintReport(catID, true);
				break;
			case(3):
				printPendingOrders();
				break;
			case 4:
				faultyItems();
				break;
			case 5:
				//Return to the main menu method.
				return;
			case 6:
				System.exit(0);
				break;
			default:
				System.out.println("Index is out of bounds.");
				break;
			}
		}
	}

	private static void printPendingOrders()
	{
		System.out.println(wh.printPendingOrders());
	}

	public static void faultyItems()
	{
		wh.UpdateFaulty();
		System.out.println(wh.printFaulty());
		System.out.println("Would you like to clear the faulty items data? (y/n)");
		String ans;
		while(!(ans = reader.next()).equals("y") && !ans.equals("n"))
		{
			System.out.println("Invalid answer. please insert (y/n).");
		}
		if(ans.equals("y"))
			wh.clearFaulty();
	}
	private static int getInt()
	{
		int choice;
		try
		{
			System.out.print("> ");
			choice = reader.nextInt();
		}
		catch(Exception e)
		{
			System.out.println("Please insert the index number of the option you wish to use");
			choice = -1;
			reader.next();
		}
		return choice;
	}
	private static void PrintReport(int catID, boolean Recursive)
	{
		wh.UpdateFaulty();
		if(catID == ENTIRE_STOCK)
		{
			try
			{
				wh.printEntireStock();
			} catch (Exception e)
			{
				System.out.println("Sorry, Can't access database.");
			}
		}
		else
		{
			if(Recursive)
			{
				wh.printWithSubCategories(catID);
			}
			else
			{
				wh.printCategory(catID);
			}
		}
		
	}
	private static int PrintAndReadCategory()
	{
		wh.listCategories();
		int numberOfCategories = wh.countCategories(); // Get the number of categories.
		int choice = getInt();
		while(!(choice > 0 && choice <= numberOfCategories))
		{
			System.out.println("Invalid category ID. Please try again.");
			choice = getInt();
		}
		return choice;
	}
	private static void PrintMainMenu()
	{
		for(MainMenu item : MainMenu.values())
		{
			System.out.println(item.ordinal() + ". "+ item.name());		
		}
	}
	private static void PrintReportsMenu()
	{
		for(ReportsMenu item : ReportsMenu.values())
		{
			System.out.println(item.ordinal() + ". "+ item.name());		
		}
	}
}
