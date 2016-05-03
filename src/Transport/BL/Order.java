package Transport.BL;
import java.sql.*;


public class Order {
	private Connection db;
	
	
	public Order(Connection db){
		this.db=db;
	}
	
	public int getLastOrderId(){
		int ans=-2;
		try {
			Statement st=db.createStatement();
			String sql="SELECT orderID FROM Segment ";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				ans = rs.getInt("orderID");
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			ans = 555;
		}
		return ans+1;
	}
	
	
	public  boolean add(int orderID,int source,int dest){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Segment(orderID,sourceID,destID)"+
						" VALUES("+orderID+","+source+","+
						dest+");";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}
	
	public boolean remove(int orderID){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from Segment where orderID="+orderID+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}
		return ans;
	}
	
	
	public  void listOfOrders(){
		try {
			Statement st=db.createStatement();
			String sql="SELECT * FROM Segment";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				System.out.println("OrderID = "+rs.getInt("orderID"));
				System.out.println("SourceID = "+rs.getInt("sourceID"));
				System.out.println("DestinationID = "+rs.getInt("destID"));
				
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
