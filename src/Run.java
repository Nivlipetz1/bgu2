import java.util.Scanner;

/**
 * Created by matan on 5/3/2016.
 */
public class Run {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37;1m";
    public static final String ANSI_REDBG = "\u001B[41m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUEBG = "\u001B[44m";

    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);
        int i= 1;
        while(i!=0) {
            System.out.println(ANSI_BOLD+"Please choose desired menu:"+ANSI_RESET);
            System.out.println(ANSI_BOLD+"1"+ANSI_RESET+". Employee Menu");
            System.out.println(ANSI_BOLD+"2"+ANSI_RESET+". Transports Menu");
            System.out.println(ANSI_BOLD+"0 To Exit"+ANSI_RESET);
            i = sc.nextInt();
            switch (i) {
                case 0:
                    System.out.println("Exiting..");
                    break;
                case 1:
                    Employees.PL.MainMenu.run();
                    break;
                case 2:
                    Transport.BL.Main.Run();
                    break;
                default:
                    System.out.println("Try again..");
                    break;
            }
        }
    }
}
