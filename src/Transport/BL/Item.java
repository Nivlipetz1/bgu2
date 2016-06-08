package Transport.BL;
import java.sql.*;


public class Item {
	private Connection db;
	
	public Item (Connection db){
		this.db=db;
	}


	public int getLastItemId(){
		int ans=-2;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ID FROM Product ";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				ans = rs.getInt("ID");
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			ans = 555;
		}
		return ans+1;
	}

	public boolean add(int itemID,String itemName, int weight){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Product(ID,Name,Weight)"+
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
			String sql="DELETE from Product where ID="+itemID+";";
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
			String sql="SELECT * FROM Product";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				System.out.println("ItemID = "+rs.getInt("ID"));
				System.out.println("Item Name = "+rs.getString("name"));
				System.out.println("Item Weight = "+rs.getInt("Weight"));

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
			String sql="SELECT Weight FROM Product WHERE ID = "+itemID+";";
			ResultSet rs =st.executeQuery(sql);
			weight = rs.getInt("Weight");
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
			String sql="SELECT Name FROM Product WHERE ID = "+itemID+";";
			ResultSet rs =st.executeQuery(sql);
			ans = rs.getString("Name");
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
			String sql="SELECT ID FROM Product WHERE ID = "+itemID+";";
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next()) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}

}
