package Program;

import Stock.modul.NeededItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Vector;


public class OrderToTransport {

    private int orderID;
    private LocalDate date;
    private LocalTime startTime;
    private int sourceId; // the source adress of the transport
    private int destId;
    private NeededItem needed;

    public OrderToTransport(int orderID, LocalDate date, LocalTime startTime, int sourceId, int destID, NeededItem needed) {
        this.orderID = orderID;
        this.date = date;
        this.startTime = startTime;
        this.sourceId = sourceId;
        this.destId = destID;
        this.needed = needed;

    }

    public int getOrderID() {
        return orderID;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getDestId() {
        return destId;
    }

    public int getItemID()
    {
        return needed.getItem().getID();
    }

    public int getItemAmount()
    {
        return needed.getQuantity();
    }

    public void subtractAmount(int handled)
    {
        this.needed.setAmount(this.needed.getQuantity()-handled);
    }
}
