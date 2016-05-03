package Employees.BackEnd;

/**
 * Created by omer on 18/04/16.
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import Employees.PL.MainMenu;

public class Day {

    private Shift morningShift;
    private Shift eveningShift;
    private LocalDate date;
    private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);


    public Day(Shift morningShift, Shift eveningShift, LocalDate date) {
        this.morningShift = morningShift;
        this.eveningShift = eveningShift;
        this.date = date;
    }

    public Shift getMorningShift() {
        return morningShift;
    }

    public Shift getEveningShift() {
        return eveningShift;
    }

    public String getDate() {
        return date.format(formatterDate);
    }

    public void setMorningShift(Shift morningShift) {
        this.morningShift = morningShift;
    }

    public void setEveningShift(Shift eveningShift) {
        this.eveningShift = eveningShift;
    }

    public void print(){
        System.out.print(MainMenu.ANSI_REDBG+"\t\t\t\t"+MainMenu.ANSI_RESET);
        System.out.print(MainMenu.ANSI_BOLD+" "+date.getDayOfWeek()+" "+MainMenu.ANSI_RESET);
        System.out.println(MainMenu.ANSI_REDBG+"\t\t\t\t  "+MainMenu.ANSI_RESET);
        System.out.println("Date: "+date.format(formatterDate));
        System.out.println(MainMenu.ANSI_BOLD+"\t\t\tMorning Shift: "+MainMenu.ANSI_RESET);
        if(morningShift!=null){
            morningShift.print();
        }
        else{

            System.out.println("");
            System.out.println("\t\t\tNo Morning Shift Set");
            System.out.println("");
            System.out.println("");
        }
        System.out.println(MainMenu.ANSI_BOLD+"\t\t\tEvening Shift: "+MainMenu.ANSI_RESET);
        if(eveningShift!=null){
            eveningShift.print();
        }
        else{
            System.out.println("");
            System.out.println("\t\t\tNo Evening Shift Set");

            System.out.println("");
            System.out.println("");
        }
        System.out.println(MainMenu.ANSI_REDBG+"\t\t\t\t\t\t\t\t\t\t  "+MainMenu.ANSI_RESET);
    }
}
