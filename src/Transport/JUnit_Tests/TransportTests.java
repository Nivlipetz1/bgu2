package Transport.JUnit_Tests;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

import Transport.BL.Driver;
import Transport.BL.Item;
import Transport.BL.Order;
import Transport.BL.Place;
import Transport.BL.TransOrder;
import Transport.BL.Transport;
import Transport.BL.Truck;

public class TransportTests extends TestCase {
	public Connection db;
	public static boolean ans;

	public Transport transport;
	public Order order;
	public Place place;
	public Truck truck;
	public Driver driver;
	public Item item;
	public TransOrder transOrder;

	
	public TransportTests(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		try {
			Class.forName("org.sqlite.JDBC");
			db = DriverManager.getConnection("jdbc:sqlite:Transport.db");
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		
		place= new Place(db);
		truck=new Truck(db);
		driver=new Driver(db);
		item =new Item(db);
		transport= new Transport(db);
		order= new Order(db);
		transOrder=new TransOrder(db);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		ans = driver.contains(102939);
		if(ans) ans =driver.remove(102939);
		ans =item.contains(102939);
		if (ans) ans =item.remove(102939);
		ans =truck.contains(102939);
		if (ans) ans = truck.remove(102939);
		ans =item.contains(102938);
		if (ans) ans =item.remove(102938);
		ans =truck.contains(102938);
		if (ans) ans = truck.remove(102938);


		

		
		db.close();
	}	
	
	
	@Test
	public void testAddDriver (){
		ans = driver.add(102939, "Eddy", 1234567, "B");
		assertTrue(ans);
		ans = driver.contains(102939);
		assertTrue(ans);
		ans =driver.remove(102939);
		assertTrue(ans);
	}
	
	
	@Test
	public void testGetDriverNameByID(){
		String name;
		ans = driver.add(102939, "Eddy", 1234567, "B");
		assertTrue(ans);
		name = driver.getDriverName(102939);
		assertEquals(name, "Eddy");
		ans =driver.remove(102939);
		assertTrue(ans);
	}
	
	
	@Test
	public void testAddItem (){
		ans = item.add(102939, "Perfume", 10);
		assertTrue(ans);
		ans =item.contains(102939);
		assertTrue(ans);
		ans =item.remove(102939);
		assertTrue(ans);
	}
	
	
	@Test
	public void testGetItemNameByID (){
		String name;
		ans = item.add(102939, "Perfume", 10);
		assertTrue(ans);
		name = item.getItemName(102939);
		assertEquals(name, "Perfume");
		ans =item.remove(102939);
		assertTrue(ans);
	}
	
	
	public void testGetItemWeightByID (){
		int weight;
		ans = item.add(102939, "Perfume", 10);
		assertTrue(ans);
		weight = item.getItemWeight(102939);
		assertEquals(weight, 10);
		ans =item.remove(102939);
		assertTrue(ans);
	}
	
	
	@Test
	public void testAddTruck (){
		ans = truck.add(102939, "A", "mercedes", "blue", 200000.0, 300000.0,20000, 0);
		assertTrue(ans);
		ans =truck.contains(102939);
		assertTrue(ans);
		ans = truck.remove(102939);
		assertTrue(ans);
	}
	
	
	@Test
	public void testAddAddress (){
		ans =place.add(102939,"A", 39399404,"eddy");
		assertTrue(ans);
		ans = place.contains(102939);
		assertTrue(ans);
		ans = place.remove(102939);
		assertTrue(ans);
	}
	
	
	@Test
	public void testGetAddressNameByID (){
		String name;
		ans =place.add(102939,"A", 39399404,"eddy");
		assertTrue(ans);
		name = place.getAddressName(102939);
		assertEquals (name, "A");
		ans = place.remove(102939);
		assertTrue(ans);
	}
		
	
}
