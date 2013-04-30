/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author 960122
 */
public class SqlSelector {

	/** 資料庫連線 */
	private Connection connetion;

	/**
	 * 建構子
	 */
	public SqlSelector(Connection connetion) {
		this.connetion = connetion;
	}
	
	public ResultMap singleSelect(final String sql){
		ResultMap result = new ResultMap();
		Statement stmt;
		try {
			stmt = this.connetion.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			int rowCount = 1;
			while (rs.next()) {
				System.out.println("Row " + rowCount + ":  ");
				for (int i = 1; i <= numberOfColumns; i++) {
					System.out.print("   Column " + i + ":  ");
					System.out.println(rs.getString(i));
				}
				System.out.println("");
				rowCount++;
			}
			stmt.close();
 
		} catch (SQLException ex) {
			System.err.print("SQLException: ");
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	public static void main(String[] args) {
		Connection wdmrConnetion = JDBCConnector.getWDMRConnetion("localhost:3306", "wdmr_base", "wdmr", "wdmr135");
		SqlSelector selector = new SqlSelector(wdmrConnetion);
		
		final String sql = "SELECT sn, url FROM wait_search_url_wiki";
//		selector.select(sql);
	}
}
