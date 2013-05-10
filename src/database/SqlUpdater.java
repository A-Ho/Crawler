/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * SQL Updater
 * 
 * @author A-Ho
 */
public class SqlUpdater {

	private Connection connetion;

	public SqlUpdater(Connection connetion) {
		this.connetion = connetion;
	}

	public int executeSql(final String sql) {
		try {
			Statement stmt = connetion.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Error sql statement:" + sql);
			e.printStackTrace();
		}
		return 0;
	}

	public void executeSql(final String sql, List<SqlParameter> sqlParams) {
		PreparedStatement stmt;
		try {
			stmt = this.connetion.prepareStatement(sql);
			int i = 1;
			for (SqlParameter tmpSqlParam : sqlParams) {
				tmpSqlParam.helpSetPreparedStatement(i++, stmt);
			}
			stmt.executeUpdate();
			stmt.clearParameters();
		} catch (SQLException e) {
			System.out.println("Error sql statement:" + sql);
			e.printStackTrace();
		}

	}


}
