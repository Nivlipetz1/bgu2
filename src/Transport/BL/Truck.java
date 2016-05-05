package Transport.BL;
import java.sql.*;
import java.util.*;


public class Truck {
	private Connection db;

	public Truck(Connection db){
		this.db=db;
	}

	//available
	public boolean add(int truckPlateNum,String licenceType,String model,String color,double weightNeto,double maxWeight, double actualWeight){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Truck(truckPlateNum,licenceType,model,color,weightNeto,maxWeight,actualWeight)"+
					" VALUES("+truckPlateNum+",'"+licenceType+"','"+
					model+"','"+color+"',"+weightNeto+","+maxWeight+","+weightNeto+");";
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
			String sql="DELETE from Truck where truckPlateNum="+truckPlateNum+";";
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
			String sql="SELECT truckPlateNum FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
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
			String sql="SELECT licenceType FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			Statement st2=db.createStatement();
			String sql2="SELECT licenceType FROM Driver WHERE driverID = "+driverID+";";
			ResultSet rs2 = st2.executeQuery(sql2);
			licenceDriver = rs2.getString("licenceType");
			while (rs.next() && !ans){
				ans = (licenceDriver.equals(rs.getString("licenceType")));
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
				System.out.println("Plate Number = "+rs.getInt("truckPlateNum"));
				System.out.println("Licence Type = "+rs.getString("licenceType"));
				System.out.println("Model = "+rs.getString("model"));
				System.out.println("Color = "+rs.getString("color"));
				System.out.println("Net Weight = "+rs.getInt("weightNeto"));
				System.out.println("Actual Weight = "+rs.getInt("actualWeight"));
				System.out.println("Max Weight = "+rs.getInt("maxWeight"));
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
			String sql="SELECT actualWeight,maxWeight FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			itemWeight = Run.item.getItemWeight(itemID);
			allWeight = itemWeight * amount;
			actualTruckWeight = rs.getInt("actualWeight");
			maxTruckWeight = rs.getInt("maxWeight");

			ans = (maxTruckWeight >= (actualTruckWeight + allWeight) );
			rs.close();
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}
	
	public void weightInformations (int truckPlateNum){
		int actualTruckWeight;
		int maxTruckWeight;
		int weightAllowed;
		try {
			Statement st=db.createStatement();
			String sql="SELECT actualWeight,maxWeight FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			actualTruckWeight = rs.getInt("actualWeight");
			maxTruckWeight = rs.getInt("maxWeight");
			weightAllowed = maxTruckWeight - actualTruckWeight;
			
			System.out.println("The truck "+truckPlateNum+" have "+weightAllowed+"kg free");
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
	}

	public  boolean addWeight (int truckPlateNum, int itemID, int amount){
		boolean ans = true;
		int actualTruckWeight;
		int itemWeight=0;
		int allWeight = 0;
		int newWeight=0;
		try {
			Statement st=db.createStatement();
			String sql="SELECT actualWeight FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			itemWeight = Run.item.getItemWeight(itemID);
			allWeight = itemWeight * amount;
			actualTruckWeight = rs.getInt("actualWeight");
			newWeight = actualTruckWeight + allWeight;
			
			//System.out.println("Actual Weight = "+actualTruckWeight);

			//System.out.println("New weight= "+newWeight);


			Statement st2=db.createStatement();
			String sql2="UPDATE Truck SET actualWeight = "+newWeight +" WHERE truckPlateNum = "+truckPlateNum+";";
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
			String sql="SELECT licenceType FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);

			ans = rs.getString("licenceType");
			rs.close();
			st.close();

		} catch (SQLException e) {
		}
		return ans;
	}

	public Vector <String> getLicencesTypesAvailables (){
		Vector<String> ans= new Vector<String>();
		try {
			Statement st=db.createStatement();
			String sql="SELECT licenceType FROM Truck;";
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



	
	public boolean removeWeight (int truckPlateNum, int itemID, int amount){
		boolean ans = false;
		int actualTruckWeight;
		int itemWeight=0;
		int allWeight = 0;
		int newWeight=0;
		try {
			Statement st=db.createStatement();
			String sql="SELECT actualWeight FROM Truck WHERE truckPlateNum = "+truckPlateNum+";";
			ResultSet rs = st.executeQuery(sql);
			
			itemWeight = Run.item.getItemWeight(itemID);
			allWeight = itemWeight * amount;
			actualTruckWeight = rs.getInt("actualWeight");
			newWeight = actualTruckWeight - allWeight;

			Statement st2=db.createStatement();
			String sql2="UPDATE Truck SET actualWeight ="+newWeight+" WHERE truckPlateNum = "+truckPlateNum+";";
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
