
package Transport.DL;

import java.sql.*;

public class ConnectDB {

	private static ConnectDB instance=new ConnectDB();
	private Connection connection;
	
	
	private ConnectDB(){
		
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:mine.db");
			if (c!= null) System.out.println("Connection accepted!");
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		//System.out.println("Opened database successfully");
		//System.out.println("***************************");
		connection=c;
		
	}
	
	public static ConnectDB getInstance(){
		
		return instance;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
}
