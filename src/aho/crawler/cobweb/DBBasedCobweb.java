/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.cobweb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import aho.crawler.database.SqlParameter;
import aho.crawler.database.SqlUpdater;
import aho.crawler.model.WebPage;



/**
 * Multi-thread spider engine, tmp data are stored in DB
 * @author A-Ho
 */
public class DBBasedCobweb extends Cobweb {
	
	final static Logger log = Logger.getLogger(DBBasedCobweb.class);
	
	final static private int DB_URL_CHAR_SIZE = 999;

	private Connection connection;

	private SqlUpdater sqlUpdater;
	
	private Queue<String> staticList = new LinkedList<String>();
	
	public DBBasedCobweb(){
		// init data
		for(int i=0;i<2;i++){
			this.staticList.add("a");
		}
	}
	
	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * TODO
	 */
	public List<String> getSearchedSites() {
		return null;
	}
	
	public void addSearchedSites(String url){
		/*
		List<SqlParameter> sqlParamList = new LinkedList<SqlParameter>();
		sqlParamList.add(new SqlParameter(Types.VARCHAR, url));
		sqlParamList.add(new SqlParameter(Types.TIMESTAMP, new java.sql.Timestamp(new Date().getTime())));
		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO SEACHED_URL_WIKI (URL, UPDATE_TIME) VALUES (?, ?)");
		this.sqlUpdater.executeSql(sql.toString(), sqlParamList);
        */
		this.sqlUpdater.executeSql("INSERT INTO SEACHED_URL_WIKI (URL) VALUES ('"+url+"')");
	}
	
	public Queue<String> getUnsearchList(){
		
		return this.staticList;
	}
	
	public String peekUnsearchList(){
		final String sql = "SELECT min(sn), url FROM WAIT_SEARCH_URL_WIKI";
		String sn = "";
		String url = "";
		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				sn = rs.getString(1);
				url = rs.getString(2);
				if(sn == null || url == null){
					log.info("WAIT_SEARCH_URL_WIKI 資料用檠");
					return null;
				}
			}
			// Remove searched URI
			stmt.executeUpdate("Delete From WAIT_SEARCH_URL_WIKI Where Sn = " + sn);
			stmt.close();
		} catch (SQLException ex) {
			log.error("SQLException: " + ex.getMessage());
		}
		
		//GC
		sn = null;
		
		return url;
	}
	
	public void addUnsearchList(String url){
		if(url.length() > DB_URL_CHAR_SIZE){
			log.warn("URL size is too long: "+url);
			return;
		}
//		List<SqlParameter> sqlParamList = new LinkedList<SqlParameter>();
//		sqlParamList.add(new SqlParameter(Types.VARCHAR, url));
//		sqlParamList.add(new SqlParameter(Types.VARCHAR, "1"));
//		sqlParamList.add(new SqlParameter(Types.TIMESTAMP, new java.sql.Timestamp(new Date().getTime())));
//		final StringBuilder sql = new StringBuilder();
//		sql.append("INSERT INTO WAIT_SEARCH_URL_WIKI (URL, WEIGHT, UPDATE_TIME) VALUES (?, ?, ?)");
//		this.sqlUpdater.executeSql(sql.toString(), sqlParamList);

		this.sqlUpdater.executeSql("INSERT INTO WAIT_SEARCH_URL_WIKI (URL) VALUES ('"+ url +"')");
	}

	@Override
	public void checkLink(String link) {
		addUnsearchList(link);
	}

	@Override
	public boolean isSearched(String url) {
		return false;
	}
	
	public void initWiki(){
		List<SqlParameter> sqlParamList = new LinkedList<SqlParameter>();
		sqlParamList.add(new SqlParameter(Types.VARCHAR, "1"));
		sqlParamList.add(new SqlParameter(Types.VARCHAR, "http://zh.wikipedia.org/wiki/Wiki"));
		sqlParamList.add(new SqlParameter(Types.VARCHAR, "1"));
		sqlParamList.add(new SqlParameter(Types.TIMESTAMP, new java.sql.Timestamp(new Date().getTime())));
		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO WAIT_SEARCH_URL_WIKI (SN, URL, WEIGHT, UPDATE_TIME) VALUES (?, ?, ?, ?)");
		
		this.sqlUpdater.executeSql(sql.toString(), sqlParamList);
	}

	@Override
	public Queue<WebPage> getUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUnsearchQueue(WebPage unsearchQueue) {
		// TODO Auto-generated method stub
		
	}


}
