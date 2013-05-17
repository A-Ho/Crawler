/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;

/**
 * SQL parameter
 * @author A-Ho
 */
public class SqlParameter {
	
	final static Logger log = Logger.getLogger(SqlParameter.class);

	private int type;

	private Object value;

	public SqlParameter(int type, Object value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Set parameters of PreparedStatement 
	 */
	public void helpSetPreparedStatement(int index, PreparedStatement ps) {
		try {
			switch (this.type) {
			case Types.CHAR:
			case Types.VARCHAR:
				ps.setString(index, (String)this.getValue());
				break;

			case Types.TIMESTAMP:
				ps.setTimestamp(index, (java.sql.Timestamp)this.getValue());
				break;
				
			case Types.DATE:
				ps.setDate(index, (java.sql.Date)this.getValue());
				
				
			//TODO Keep increasing data type...
			
			
			default:
				log.debug("No such DB data type: " + this.type);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**************************************************************
	 *  Accessor Block Start 
	 **************************************************************/
	/**
	 */
	public int getType() {
		return type;
	}

	/**
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**************************************************************
	 *  Accessor Block End 
	 **************************************************************/
}
