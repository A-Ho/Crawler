/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
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
 * @author 960122
 */
public class SqlUpdater {

	/** 資料庫連線 */
	private Connection connetion;

	/**
	 * 建構子
	 */
	public SqlUpdater(Connection connetion) {
		this.connetion = connetion;
	}

	public int executeSql(final String sql) {
		try {
			Statement stmt = connetion.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("SQL錯誤:" + sql);
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
			System.out.println("資料庫更新語法錯誤:" + sql);
			e.printStackTrace();
		}

	}


}
