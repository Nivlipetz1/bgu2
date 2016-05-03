package Transport.BL;
import java.sql.*;


public class TransOrder {
	private Connection db;
	
	public TransOrder(Connection db){
		this.db=db;
	}
	
	public boolean add(int transportID,int orderID, int itemID, int numOfItems){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO TransOrder(transportID,orderID,itemID,numOfItems)"+
						" VALUES("+transportID+","+orderID+","+itemID+","+numOfItems+");";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}
	
	public  boolean remove(int transportID,int orderID){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from TransOrder where transportID="+transportID+" And orderID="+orderID+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
		
	}
	

}
