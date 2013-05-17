/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
/**
 * 
 * @author A-Ho
 */
public class SqlSelector {
	
	final static Logger log = Logger.getLogger(SqlSelector.class);

	/** DB  Connection*/
	private Connection connetion;

	/**
	 * Constructor
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
			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					System.out.println(rs.getString(i));
				}
			}
			stmt.close();
 
		} catch (SQLException ex) {
			log.error("SQLException: " + ex.getMessage());
		}
		return result;
	}
	
}
