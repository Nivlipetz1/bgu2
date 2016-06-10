package Transport.BL;
import Employees.BL.BL_IMPL;
import Employees.BackEnd.Employee;
import Program.DriverInformations;

import java.sql.*;
import java.util.*;


public class Truck {
	private Connection db;
	private static DriverInformations driverInformations = new BL_IMPL();
	public Vector<Integer> availableTrucks;



	public Truck(Connection db){
		this.db=db;
		availableTrucks= new Vector<Integer>();
	}

	//available 0=Available, 1=Busy
	public boolean add(int truckPlateNum,String licenceType,String model,String color,double weightNeto,double maxWeight, double actualWeight, int available){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Truck(TruckPlateNum,LicenceType,Model,Color,WeightNeto,MaxWeight,ActualWeight,Available)"+
					" VALUES("+truckPlateNum+",'"+licenceType+"','"+
					model+"','"+color+"',"+weightNeto+","+maxWeight+","+weightNeto+", 0);";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}

	public boolean remove(int truckPlateNum){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from Truck where TruckPlateNum="+truckPlateNum+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}
	
	public boolean contains (int truckPlateNum){
		boolean ans = true;
		try {
			Statement st=db.createStatement();
			String sql="SELECT TruckPlateNum FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next()) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}
	
	public boolean canDrive (int driverID, int truckPlateNum){
		boolean ans = false;
		String licenceDriver;
		try {
			Statement st=db.createStatement();
			String sql="SELECT LicenceType FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			Statement st2=db.createStatement();
			String sql2="SELECT LicenceType FROM Driver WHERE driverID = "+driverID+";";
			ResultSet rs2 = st2.executeQuery(sql2);
			licenceDriver = rs2.getString("LicenceType");
			while (rs.next() && !ans){
				ans = (licenceDriver.equals(rs.getString("LicenceType")));
			}
			rs.close();
			st.close();
			rs2.close();
			st2.close();


		} catch (SQLException e) {
			ans = false;
		}
		return ans;

	}
	
	public void listOfTrucks(){
		try {
			Statement st=db.createStatement();
			String sql="SELECT * FROM Truck";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				boolean isAvailable = (rs.getInt("Available")==0);
				System.out.println("Plate Number = "+rs.getInt("TruckPlateNum"));
				System.out.println("Licence Type = "+rs.getString("LicenceType"));
				System.out.println("Model = "+rs.getString("Model"));
				System.out.println("Color = "+rs.getString("Color"));
				System.out.println("Net Weight = "+rs.getInt("WeightNeto"));
				System.out.println("Actual Weight = "+rs.getInt("ActualWeight"));
				System.out.println("Max Weight = "+rs.getInt("MaxWeight"));
				System.out.println("Availablity = "+isAvailable);
				System.out.println();
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public boolean canAddWeight (int truckPlateNum, int itemID, int amount){
		boolean ans = false;
		int actualTruckWeight;
		int maxTruckWeight;
		int itemWeight=0;
		int allWeight = 0;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ActualWeight,MaxWeight FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			itemWeight = Run.item.getItemWeight(itemID);
			allWeight = itemWeight * amount;
			actualTruckWeight = rs.getInt("ActualWeight");
			maxTruckWeight = rs.getInt("MaxWeight");

			ans = (maxTruckWeight >= (actualTruckWeight + allWeight) );
			rs.close();
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}

	public int amountMaxToTrans (int truckPlateNum, int itemID, int weight){
		int actualTruckWeight;
		int maxTruckWeight;
		int weightAllowed=1;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ActualWeight,MaxWeight FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);

			actualTruckWeight = rs.getInt("ActualWeight");
			maxTruckWeight = rs.getInt("MaxWeight");
			weightAllowed = maxTruckWeight - actualTruckWeight;

			rs.close();
			st.close();

		} catch (SQLException e) {
		}
		return (weight / weightAllowed);
	}
	
	public void weightInformations (int truckPlateNum){
		int actualTruckWeight;
		int maxTruckWeight;
		int weightAllowed;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ActualWeight,MaxWeight FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			actualTruckWeight = rs.getInt("ActualWeight");
			maxTruckWeight = rs.getInt("MaxWeight");
			weightAllowed = maxTruckWeight - actualTruckWeight;
			
			System.out.println("The truck "+truckPlateNum+" have "+weightAllowed+"kg free");
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
	}

	public boolean addWeight (int truckPlateNum, int itemID, int amount){
		boolean ans = true;
		int actualTruckWeight;
		int itemWeight=0;
		int allWeight = 0;
		int newWeight=0;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ActualWeight FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			itemWeight = Run.item.getItemWeight(itemID);
			allWeight = itemWeight * amount;
			actualTruckWeight = rs.getInt("ActualWeight");
			newWeight = actualTruckWeight + allWeight;
			
			//System.out.println("Actual Weight = "+actualTruckWeight);

			//System.out.println("New weight= "+newWeight);


			Statement st2=db.createStatement();
			String sql2="UPDATE Truck SET ActualWeight = "+newWeight +" WHERE TruckPlateNum = "+truckPlateNum+";";
			PreparedStatement pstmt = db.prepareStatement(sql2);
			pstmt.executeUpdate();

			ans = true;
			
			rs.close();
			st.close();
			st2.close();

		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			ans = false;
		}
		return ans;
	}

	public String getLicenceType (int truckPlateNum){
		String ans="";
		try {
			Statement st=db.createStatement();
			String sql="SELECT LicenceType FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);

			ans = rs.getString("licenceType");
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
		return ans;
	}

	public Vector <String> getLicencesTypes (){
		Vector<String> ans= new Vector<String>();
		try {
			Statement st=db.createStatement();
			String sql="SELECT LicenceType FROM Truck;";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				ans.add(rs.getString("licenceType"));
			}
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
		return ans;
	}

	public Vector <Integer> getAvailablesTruck (){
		Vector<Integer> ans= new Vector<Integer>();
		try {
			Statement st=db.createStatement();
			String sql="SELECT TruckPlateNum FROM Truck WHERE Available=0;";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				ans.add(rs.getInt("TruckPlateNum"));
			}
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
		return ans;
	}

	public HashMap <Integer, String> getAvailableTruckByTruckPlateNumAndLicenceType(){
		HashMap <Integer, String> ans = new HashMap <Integer, String>();
		Vector <Integer> vectorAvailableTruck = getAvailablesTruck();
		Enumeration en = vectorAvailableTruck.elements();
		while (en.hasMoreElements()){
			int truckPlateNum = (int)en.nextElement();
			String licenceType = getLicenceType(truckPlateNum);
			ans.put(truckPlateNum, licenceType);
		}

		return ans;
	}

	public Vector <Integer> getBusyTruck (){
		Vector<Integer> ans= new Vector<Integer>();
		try {
			Statement st=db.createStatement();
			String sql="SELECT TruckPlateNum FROM Truck WHERE Available=1;";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				ans.add(rs.getInt("TruckPlateNum"));
			}
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
		return ans;
	}

	public boolean isTruckAvailable (int truckPlateNum){
		boolean ans = false;
		try {
			Statement st=db.createStatement();
			String sql="SELECT Available FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);

			int isAvailable = rs.getInt("Available");

			ans = (isAvailable == 0);
			rs.close();
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}

	public  void setAvailability (int truckPlateNum, int available){ // 0 available, 1 not available
		try {
			Statement st2=db.createStatement();
			String sql2="UPDATE Truck SET Available = "+available +" WHERE TruckPlateNum = "+truckPlateNum+";";
			PreparedStatement pstmt = db.prepareStatement(sql2);
			pstmt.executeUpdate();
			st2.close();

		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}


	public boolean isTransportPossible(){
		return (vectorLicenceTypeAvailablesToTransport()!=null);
	}

	public Vector<String> vectorLicenceTypeAvailablesToTransport(){
		Vector<String> ans = new Vector<String>();
		Vector<String> driverLicenceTypesAvailables = driverInformations.getDriversTypesLicencesAvailables();

		Enumeration en = driverLicenceTypesAvailables.elements();

		//TODO NEED TO CHECK THAT ENUMERATION IS NOT NULL (there might not be a shift in the time now ->which will return null)
		while(en.hasMoreElements()){
			boolean areEqual = false;
			Vector<String> truckLicenceTypesAvailables = getLicencesTypes();
			Enumeration en2 = truckLicenceTypesAvailables.elements();
			while (!areEqual && en2.hasMoreElements()){
				if (en.nextElement().equals(en2.nextElement())){
					areEqual = true;
					if (!ans.contains(en.nextElement())){
						ans.addElement((String) en.nextElement());
					}
				}
			}
		}

		return ans;
	}

	public boolean returnTruck (int truckPlateNum, int itemID, int amount){
		boolean ans = removeWeight(truckPlateNum, itemID, amount);
		if (ans) setAvailability (truckPlateNum, 0);
		return ans;
	}


	
	public boolean removeWeight (int truckPlateNum, int itemID, int amount){
		boolean ans = false;
		int actualTruckWeight;
		int itemWeight=0;
		int allWeight = 0;
		int newWeight=0;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ActualWeight FROM Truck WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			itemWeight = Run.item.getItemWeight(itemID);
			allWeight = itemWeight * amount;
			actualTruckWeight = rs.getInt("ActualWeight");
			newWeight = actualTruckWeight - allWeight;

			Statement st2=db.createStatement();
			String sql2="UPDATE Truck SET ActualWeight ="+newWeight+" WHERE TruckPlateNum = "+truckPlateNum+";";
			ResultSet rs2 = st.executeQuery(sql2);
			
			rs.close();
			st.close();
			rs2.close();
			st2.close();
			ans = true;

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}

	
	

}
