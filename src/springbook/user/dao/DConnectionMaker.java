package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker{
	
	private static final String URL = "jdbc:mysql://localhost/tobi";
	private static final String USERID = "study";
	private static final String PASSWORD = "study";
	
	@Override
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection con = DriverManager.getConnection(URL, USERID, PASSWORD);
		return con;
	}

}
