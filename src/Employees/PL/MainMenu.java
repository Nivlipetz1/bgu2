package Employees.PL;
import java.util.Scanner;

/**
 * Created by omer on 18/04/16.
 */
public class MainMenu {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37;1m";
    public static final String ANSI_REDBG = "\u001B[41m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUEBG = "\u001B[44m";


    public static void run(int clearance){
        Scanner sc = new Scanner(System.in);
        header();
        int i= 1;
        while(i!=0) {
            System.out.println(ANSI_BOLD+"Please choose desired menu:"+ANSI_RESET);
            System.out.println(ANSI_BOLD+"1"+ANSI_RESET+". Employee Menu");
            System.out.println(ANSI_BOLD+"2"+ANSI_RESET+". Shifts Menu");
            System.out.println(ANSI_BOLD+"0 To Exit"+ANSI_RESET);
            i = sc.nextInt();
            switch (i) {
                case 0:
                    System.out.println("Exiting..");
                    break;
                case 1:
                    EmployeeMenu.run();
                    break;
                case 2:
                    ShiftMenu.run();
                    break;
                default:
                    System.out.println("Try again..");
                    break;
            }
        }
    }
    public static void header(){
        try {
            Runtime.getRuntime().exec("clear");
        }catch (Exception e){

        }
        System.out.println(ANSI_BOLD+ANSI_YELLOW+ANSI_REDBG+"Super-Lee Management Application"+ANSI_RESET);
        System.out.println("");
    }
}
