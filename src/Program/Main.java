package Program;

import java.util.Calendar;
import java.util.Scanner;
import Stock.data.DBHandler;

public class Main
{
    private static Scanner reader = new Scanner(System.in);
    public static Calendar today = Calendar.getInstance();

    public static void main(String[] args)
    {

        System.out.println("Welcome!");
        int clearance = Login();
        int choice = 0;
        while(choice != 5)
        {
            System.out.println("Choose your thing...");
            System.out.println("1. Warehouse module.");
            System.out.println("2. Suppliers module.");
            System.out.println("3. Employees module.");
            System.out.println("4. Transport module.");
            System.out.println("5. Exit.");

            boolean DEMO = false;
            if(DEMO)
                System.out.println("6. Next Day (For Demonstration only!).");
            try
            {
                choice = reader.nextInt();
            } catch (Exception e)
            {
                choice=-1;
                reader.next();
            }
            if(!DEMO)
            {
                Calendar tmp = today;
                today = Calendar.getInstance();
                if((today.get(Calendar.DATE) != tmp.get(Calendar.DATE)))
                    dailyTasks();
            }
            switch(choice)
            {
                case(1):
                    Warehouse.run(clearance);
                    clearance = Login();
                    break;
                case(2):
                    Suppliers.run(clearance);
                    clearance = Login();
                    break;
                case 3:
                    Employees.PL.MainMenu.run(clearance);
                    clearance = Login();
                    break;
                case 4:
                    Transport.BL.Main.Run(clearance);
                    clearance = Login();
                    break;
                case(5):
                    System.out.println("Quitting! bye...  :(");
                    break;
                case(6):
                    if(DEMO)
                    {
                        today.roll(Calendar.DAY_OF_MONTH, true);
                        dailyTasks();
                        break;
                    }
                default:
                    System.out.println("Please choose an option from the menu above...");
                    break;
            }
        }
        reader.close();

    }
    private static void dailyTasks()
    {
        Warehouse.faultyItems();
        Warehouse.retryOrders();
        Suppliers.datedOrder();
    }
    private static int Login()
    {
        System.out.println("Hello! Please log in to the system.");
        DBHandler dataHandler = new DBHandler();
        int clearance = -1;
        String Username,Password;
        while(clearance == -1)
        {
            System.out.println("Username: ");
            Username = reader.next();
            if(Username.equals("EXIT"))
                System.exit(0);
            System.out.println("Password: ");
            Password = reader.next();
            clearance = dataHandler.logIn(Username, Password);
            if(clearance == -1)
                System.out.println("Something is not right... Please try again.");
        }
        return clearance;
    }

}
