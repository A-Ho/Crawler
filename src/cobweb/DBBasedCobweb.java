/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package cobweb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.WebPage;

import database.SqlParameter;
import database.SqlUpdater;

/**
 * Multi-thread spider engine, tmp data are stored in DB
 * @author A-Ho
 */
public class DBBasedCobweb implements ICobweb {

	private List<String> searchedSites = new ArrayList<String>();// 已經被搜索站點列表

	private Queue<String> unsearchList = new LinkedList<String>();// 需解析的鏈結列表
	
	private Connection connection;

	private SqlUpdater sqlUpdater;
	
	private Queue<String> staticList = new LinkedList<String>();
	
	public DBBasedCobweb(){
		// 先隨便回傳  有值即可
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
	 * @return the searchedSites
	 */
	public List<String> getSearchedSites() {
		return searchedSites;
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
					System.out.println("WAIT_SEARCH_URL_WIKI 資料用檠");
					return null;
				}
			}
			// Remove searched URI
			stmt.executeUpdate("Delete From WAIT_SEARCH_URL_WIKI Where Sn = " + sn);
			stmt.close();
		} catch (SQLException ex) {
			System.err.print("SQLException: ");
			System.err.println(ex.getMessage());
		}
		
		//GC
		sn = null;
		
		return url;
	}
	
	public void addUnsearchList(String url){
		if(url.length() > 999){
			System.out.println("URL過長: "+url);
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

	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public void checkLink(String link, String startUrl) {
		// 過濾非此區段開頭的網址
		if (!link.startsWith(startUrl)) {
			return;
		}
		//不存在才加入列隊
		//1. 檢查存不存在
		//2. 加入列隊
		addUnsearchList(link);
		return;
	}

	/**
	 * 檢查該鏈結是否已經被掃描
	 */
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
	
	/*
	public static void main(String[] args) {
		final String wikiUrl = "http://zh.wikipedia.org/wiki/Wiki";
		final String filePath = "E://WDMRDB//Wiki//";
		
		DBBasedCobweb engine = new DBBasedCobweb();
		
		Connection wdmrConnetion = JDBCConnector.getWDMRConnetion("localhost:3306", "wdmr_base", "wdmr", "wdmr135");
		engine.setConnection(wdmrConnetion);
		engine.sqlUpdater = new SqlUpdater(wdmrConnetion);
		engine.initWiki();

	}
	*/

	@Override
	public void checkLink(WebPage srb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Queue<WebPage> getUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebPage peekUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUnsearchQueue(WebPage unsearchQueue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDownloadRangeList(String url) {
		// TODO Auto-generated method stub
		
	}
}
