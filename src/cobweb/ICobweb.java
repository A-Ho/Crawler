/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package cobweb;

import java.util.List;
import java.util.Queue;

import model.WebPage;

/**
 * Spider 會使用到的網頁連結來源集結處
 * Inteface: 目前使用JVM Memory、File、DB三種方式實作
 * @author A-Ho
 */
public abstract interface ICobweb {
	
	public List<String> getSearchedSites();
	
	public void addSearchedSites(String url);
	
	public Queue<String> getUnsearchList();
	
	public String peekUnsearchList();
	
	public void addUnsearchList(String url);

	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public void checkLink(String link, String startUrl) ;
	
	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public void checkLink(WebPage srb) ;

	/**
	 * 檢查該鏈結是否已經被掃描
	 */
	public boolean isSearched(String url);
	
	public Queue<WebPage> getUnsearchQueue();
	
	public WebPage peekUnsearchQueue();

	public void addUnsearchQueue(WebPage unsearchQueue);
	
	public void addDownloadRangeList(String url);
}
