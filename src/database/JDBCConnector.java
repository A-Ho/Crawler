package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCConnector {

	/**
	 * 
	 */
	public static Connection getWDMRConnetion(final String url, final String database//
			, final String user, final String password//
	) {
		
		final String driver = "com.mysql.jdbc.Driver";
		final String connetUrl = "jdbc:mysql://" + url + "/" + database;
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(connetUrl, user, password);
//			conn.setAutoCommit(false);
//			System.out.println("資料庫連接成功");
			
//			Statement stmt = conn.createStatement();
//			stmt.executeUpdate("INSERT INTO ACCOUNT VALUES('test', '123')");
//			stmt.
//			conn.commit();
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驅動程式類別");
			e.printStackTrace();
		}
		
		System.out.println("連接資料庫失敗");
		return null;
	}

	public static void main(String[] args) {
		
		JDBCConnector.getWDMRConnetion("localhost:3306", "wdmr_base", "wdmr", "wdmr135");
		
//		String insertSql = "INSERT INTO ACCOUNT VALUES (";
		
	}
}
