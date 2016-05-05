package Transport.BL;
import java.sql.*;


public class Transport {
	private Connection db;
	
	public Transport (Connection db){
		this.db=db;
	}

	public int getLastTransportId(){
		int ans=-2;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ID FROM Transport";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				ans = rs.getInt("ID");
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			ans=0;
		}
		return ans+1;
	}
	
	
	
	
	public boolean add(int transportID,int truckPlateNum,int driverID,int source,int dest,String date,String leavingTime){
		boolean ans=true;
			try {
				Statement st=db.createStatement();
				String sql="INSERT INTO Transport (ID,TruckPlateNum,DriverID,SourceID,DestID,Date,LeavingTime)"+
						" VALUES("+transportID+","+truckPlateNum+","+
						driverID+","+source+","+dest+",'"+date+"','"+leavingTime+"');";
				if(st.executeUpdate(sql)==0) ans=false;
				st.close();
			} catch (SQLException e) {
				System.out.println("Error with the "+dest+" Destination ");

				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);	
				ans=false;
			}
		
		return ans;
	}

	public boolean remove (int transportID){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from Transport where ID="+transportID+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();

		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}
	
	public boolean contains (int transportID){
		boolean ans = true;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ID FROM Transport WHERE ID = "+transportID+";";
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next()) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}

	public boolean checkTruckDriver(int truckPlateNum,int driverID){
		boolean ans=false;

		try {
			Statement st=db.createStatement();
			String sql1="Select licenceType From Driver"
					+ " Where driverID="+driverID+";";
			String sql2="Select licenceType From Truck"
					+ " Where truckPlateNum="+truckPlateNum+";";
			ResultSet rs1=st.executeQuery(sql1);
			ResultSet rs2=st.executeQuery(sql2);
			String s1=rs1.getString("licenceType");
			String s2=rs2.getString("licenceType");
			if(s1.compareTo(s2)==0) ans=true;
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		return ans;
	}
	
	public void numOfStations(int transportID){
		int counter = 0;

		try {
			Statement st=db.createStatement();
			String sql1="Select orderID From TransOrder"
					+ " Where transportID="+transportID+";";
			ResultSet rs1=st.executeQuery(sql1);
			while (rs1.next()){
				counter++;
			}
			System.out.println("Transport "+transportID+" stop "+counter+" times.");

			rs1.close();
			st.close();

		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public void listOfTransport(){
		try {
			Statement st=db.createStatement();
			String sql="SELECT * FROM Transport";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				System.out.println("TransportID = "+rs.getInt("ID"));
				System.out.println("Truck Plate Number = "+rs.getInt("TruckPlateNum"));
				System.out.println("DriverID = "+rs.getInt("DriverID"));
				
				System.out.println("Driver Name = "+Run.driver.getDriverName(rs.getInt("DriverID")));

				System.out.println("SourceID = "+Run.place.getAddressName(rs.getInt("SourceID")));
			
				System.out.println("DestinationID = "+Run.place.getAddressName(rs.getInt("DestID")));

				System.out.println("Date = "+rs.getInt("Date"));
				System.out.println("LeavingTime = "+rs.getString("LeavingTime"));
				System.out.println();
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}



}
