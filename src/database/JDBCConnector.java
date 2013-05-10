/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnector {

	final static String JDBC_DRIVER_MYSQL= "com.mysql.jdbc.Driver";
	final static String JDBC_URI_MYSQL= "jdbc:mysql://";
	
	/**
	 * Get default system JDBC connection
	 */
	public static Connection getWDMRConnetion(final String url, final String database//
			, final String user, final String password//
	) {
		
		final String connetUrl = JDBCConnector.JDBC_URI_MYSQL + url + "/" + database;
		try {
			Class.forName(JDBCConnector.JDBC_DRIVER_MYSQL);
			Connection conn = DriverManager.getConnection(connetUrl, user, password);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("DB driver classes were not found.");
			e.printStackTrace();
		}
		
		System.out.println("Connect DB failed.");
		return null; //TODO sould return error messge object
	}

}
