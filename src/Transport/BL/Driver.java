package Transport.BL;
import java.sql.*;


public class Driver {
	private Connection db;
	
	public Driver(Connection db){
		this.db=db;
	}
	
	public boolean add(int id,String name,int licenceNum,String licenceType){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="INSERT INTO Driver(ID,LicenceNum,LicenceType)"+
						" VALUES("+id+","+
						licenceNum+",'"+licenceType+"');";
			if(st.executeUpdate(sql)==0) ans=false;
			
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);

		}

		return ans;
	}
	
	public boolean remove(int id){
		boolean ans=true;
		try {
			Statement st=db.createStatement();
			String sql="DELETE from Driver where ID="+id+";";
			if(st.executeUpdate(sql)==0) ans=false;
			st.close();
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return ans;
	}
	
	public void listOfDrivers(){
		try {
			Statement st=db.createStatement();
			String sql="SELECT * FROM Driver";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				System.out.println("ID = "+rs.getInt("ID"));
				System.out.println("Name = "+rs.getString("name"));
				System.out.println("LicenceNum = "+rs.getInt("LicenceNum"));
				System.out.println("LicenceType = "+rs.getString("LicenceType"));
				System.out.println();
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public String getDriverName (int ID){
		String ans=null;
		try {
			Statement st=db.createStatement();
			String sql="SELECT name FROM Driver WHERE ID = "+ID+";";
			ResultSet rs =st.executeQuery(sql);
			ans = rs.getString("name");
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return ans;		
	}
	
	public boolean contains (int driverID){
		boolean ans = true;
		try {
			Statement st=db.createStatement();
			String sql="SELECT ID FROM Driver WHERE ID = "+driverID+";";
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next()) ans=false;
			st.close();

		} catch (SQLException e) {
			ans = false;
		}
		return ans;
	}
	
	}
