package Transport.BL;
import java.sql.*;


public class Item {
	private Connection db;
	
	public Item (Connection db){
		this.db=db;
	}
	
	public boolean add(int itemID,String itemName, int weight){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Item(itemID,itemName,weight)"+
						" VALUES("+itemID+",'"+itemName+"',"+weight+");";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}
	
	public  boolean remove(int itemID){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from Item where itemID="+itemID+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return ans;
	}
	
	public  void listOfItems(){
		try {
			Statement st=db.createStatement();
			String sql="SELECT * FROM Item";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				System.out.println("ItemID = "+rs.getInt("itemID"));
				System.out.println("Item Name = "+rs.getString("itemName"));
				System.out.println("Item Weight = "+rs.getInt("weight"));

				System.out.println();
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public int getItemWeight (int itemID){
		int weight=0;
		try {
			Statement st=db.createStatement();
			String sql="SELECT weight FROM Item WHERE itemID = "+itemID+";";
			ResultSet rs =st.executeQuery(sql);
			weight = rs.getInt("weight");
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return weight;		
	}
	
	public String getItemName (int itemID){
		String ans=null;
		try {
			Statement st=db.createStatement();
			String sql="SELECT itemName FROM Item WHERE itemID = "+itemID+";";
			ResultSet rs =st.executeQuery(sql);
			ans = rs.getString("itemName");
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return ans;		
	}

	public  boolean contains (int itemID){
		boolean ans = true;
		try {
			Statement st=db.createStatement();
			String sql="SELECT itemID FROM Item WHERE itemID = "+itemID+";";
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next()) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}

}
