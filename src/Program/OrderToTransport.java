package Program;

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
    private HashMap<Integer, Integer> itemsHashMap = new HashMap <Integer, Integer>(); // hashMap of items, when the first Integer is ItemID, and the second is the amount of it

    public OrderToTransport(int orderID, LocalDate date, LocalTime startTime, int sourceId, int destID, HashMap<Integer, Integer> itemsHashMap) {
        this.orderID = orderID;
        this.date = date;
        this.startTime = startTime;
        this.sourceId = sourceId;
        this.destId = destID;
        this.itemsHashMap = itemsHashMap;
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

    public HashMap<Integer, Integer> getItemsHashMap() {
        return itemsHashMap;
    }
}
