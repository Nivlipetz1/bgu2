package Transport.DL;

import java.sql.Connection;
import java.sql.SQLException;

public class CloseDB {
	private static Connection db;

	public static void closeConnection(){
		ConnectDB cdb=ConnectDB.getInstance();
		db =cdb.getConnection();
		try {
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
