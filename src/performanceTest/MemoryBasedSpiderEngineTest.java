/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package performanceTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cobweb.ICobweb;

import model.WebPage;


import utils.HttpProxySetter;

/**
 * 多線程 Spider Engine, 暫存資料由記憶體處理 
 * @author 960122
 */
public class MemoryBasedSpiderEngineTest implements ICobweb {

	private List<String> searchedSites = new ArrayList<String>();// 已經被搜索站點列表

	private Queue<String> unsearchList = new LinkedList<String>();// 需解析的鏈結列表
	
	/**
	 * @return the searchedSites
	 */
	public List<String> getSearchedSites() {
		return searchedSites;
	}
	
	public synchronized void addSearchedSites(String url){
		this.searchedSites.add(url);
	}
	
	public Queue<String> getUnsearchList(){
		return this.unsearchList;
	}
	
	public synchronized String peekUnsearchList(){
		final String url = this.unsearchList.peek().toString();
		this.unsearchList.remove();
		return url;
	}
	
	public synchronized void addUnsearchList(String url){
		this.unsearchList.add(url);
	}

	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public void checkLink(String link, String startUrl) {
		if (link != null && !link.equals("") && link.indexOf("#") == -1) {
			if (!link.startsWith("http://") && !link.startsWith("ftp://")
					&& !link.startsWith("www.")) {
				link = "file:///" + link;
			} else if (link.startsWith("www.")) {
				link = "http://" + link;
			}
			//過濾非此區段開頭的網址
//			if (!link.startsWith(startUrl)) {
//				link = null;
//				return;
//			}
			startUrl = null;

			if (this.unsearchList.isEmpty()) {
				addUnsearchList(link);
				link = null;
			} else {
				String link_end_ = link.endsWith("/") ? link.substring(0, link.lastIndexOf("/")) : (link + "/");
				if (!this.unsearchList.contains(link) && !this.unsearchList.contains(link_end_)) {
					link_end_ = null;
					addUnsearchList(link);
				}
				link = null;
			}
		}
	}

	/**
	 * 檢查該鏈結是否已經被掃描
	 */
	public boolean isSearched(String url) {
		String url_end_ = "";
		if (url.endsWith("/")) {
			url_end_ = url.substring(0, url.lastIndexOf("/"));
		} else {
			url_end_ = url + "/";
		}
		if (this.searchedSites.size() > 0) {
			if (this.searchedSites.indexOf(url) != -1 || this.searchedSites.indexOf(url_end_) != -1) {
				url_end_ = null;
				return true;
			}
		}
		url_end_ = null;
		url = null;
		return false;
	}
	
	
	public static void main(String[] args) {
		HttpProxySetter.CHTTLSetting();
		
		final String goalName = "ez";
//		final String wikiUrl = "http://zh.wikipedia.org/zh-tw/%E7%B6%AD%E5%9F%BA%E7%99%BE%E7%A7%91";
		final String wikiUrl = "http://photo.bitauto.com/";
		final String filePath = "E://WDMRDB//photo";
		MemoryBasedSpiderEngineTest multiThreadSpider = new MemoryBasedSpiderEngineTest();
		multiThreadSpider.addUnsearchList(wikiUrl);
		
		int threadCount = 1;
		ThreadSpiderTest[] phs = new ThreadSpiderTest[threadCount];
		for(int i=0;i<phs.length;i++){
			phs[i] = new ThreadSpiderTest(goalName, wikiUrl, filePath, multiThreadSpider);
		}
		Thread[] searchs = new Thread[threadCount];
		for(int i=0;i<searchs.length;i++){
			searchs[i] = new Thread(phs[i]);
			searchs[i].start();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	/* (non-Javadoc)
	 * @see engine.ISpiderEngine#addUnsearchQueue(model.SearchResultBean)
	 */
	@Override
	public void addUnsearchQueue(WebPage unsearchQueue) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see engine.ISpiderEngine#checkLink(model.SearchResultBean)
	 */
	@Override
	public void checkLink(WebPage srb) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see engine.ISpiderEngine#getUnsearchQueue()
	 */
	@Override
	public Queue<WebPage> getUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see engine.ISpiderEngine#peekUnsearchQueue()
	 */
	@Override
	public WebPage peekUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cobweb.ICobweb#addDownloadRangeList(java.lang.String)
	 */
	@Override
	public void addDownloadRangeList(String url) {
		// TODO Auto-generated method stub
		
	}
}
