package Transport.BL;
import java.sql.*;


public class Place {
	private Connection db;
	
	
	public Place(Connection db){
		this.db=db;
	}
	
	public int getLastPlaceId(){
		int ans=-2;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ID FROM Place ";
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
	
	public  boolean add(int addressID,String address,int phoneNum,String contactName){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Place(ID,Address,PhoneNum,ContactName)"+
						" VALUES("+addressID+",'"+address+"',"+phoneNum+",'"+contactName+"');";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}
	
	public  boolean remove(int addressID){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from Place where ID="+addressID+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return ans;
	}
	
	public  void listOfPlaces(){
		try {
			Statement st=db.createStatement();
			String sql="SELECT * FROM Place";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				System.out.println("AddressID = "+rs.getInt("ID"));
				System.out.println("Address = "+rs.getString("Address"));
				System.out.println("Contact Name = "+rs.getString("ContactName"));
				System.out.println("Contact Name Number = "+rs.getString("PhoneNum"));
				System.out.println();
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public boolean contains (int addressID){
		boolean ans = true;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ID FROM Place WHERE ID = "+addressID+";";
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next()) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}
	
	
	public  String getAddressName (int addressID){
		String ans=null;
		try {
			Statement st=db.createStatement();
			String sql="SELECT Address FROM Place WHERE ID = "+addressID+";";
			ResultSet rs =st.executeQuery(sql);
			ans = rs.getString("Address");
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return ans;		
	}
	
	
	

}
