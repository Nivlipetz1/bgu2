package Transport.BL;
import java.sql.*;

import Transport.DL.CloseDB;
import Transport.DL.ConnectDB;
import Transport.PL.MainTransport;
import Transport.JUnit_Tests.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class Run {
	private Connection db;
	public static Transport transport;
	public static Order order;
	public static Place place;
	public static Truck truck;
	public static Driver driver;
	public static Item item;
	public static TransOrder transOrder;

	public Run (){
		createConnection();
		place= new Place(db);
		truck=new Truck(db);
		driver=new Driver(db);
		item =new Item(db);
		transport= new Transport(db);
		order= new Order(db);
		transOrder=new TransOrder(db);
	}

	protected void run(){
		//launchTests();
		MainTransport mainTransport=new MainTransport();
		mainTransport.display();
		CloseDB.closeConnection();
	}

	private void createConnection(){
		ConnectDB cdb=ConnectDB.getInstance();
		db=cdb.getConnection();
	}
/*
	private void launchTests(){
		//Result result = JUnitCore.runClasses(Transport.JUnit_Tests.TransportTests.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
*/


}
