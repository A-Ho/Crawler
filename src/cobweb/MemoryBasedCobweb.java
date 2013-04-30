/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package cobweb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import model.WebPage;


/**
 * 多線程 Spider Engine, 暫存資料由磁碟處理 
 * @author 960122
 */
public class MemoryBasedCobweb implements ICobweb {

	private List<String> searchedSitesList = new ArrayList<String>();// 已經被搜索站點列表

	private Queue<String> unsearchSitesList = new LinkedList<String>();// 需解析的鏈結列表

	private Queue<WebPage> unsearchQueue = new LinkedList<WebPage>();// 需解析的鏈結列表
	
	protected List<String> downloadRangeList = new LinkedList<String>();

	public synchronized void addDownloadRangeList(String url){
		this.downloadRangeList.add(url);
	}
	
	@Override
	public synchronized void addUnsearchQueue(WebPage wp) {
//		System.out.println("增加: "+ wp.getUrl());
		getUnsearchQueue().add(wp);
	}
	
	/**
	 * @return the unsearchQueue
	 */
	public synchronized Queue<WebPage> getUnsearchQueue() {
		return unsearchQueue;
	}
	
	/**
	 * @return the unsearchQueue
	 */
	@Override
	public synchronized WebPage peekUnsearchQueue() {
		final WebPage result = (WebPage)getUnsearchQueue().poll();
//		this.unsearchQueue.iterator().remove();
//		System.out.println("消耗: "+result.getUrl());
		return result;
	}

//	/**
//	 * @param unsearchQueue the unsearchQueue to set
//	 */
//	public synchronized void addUnsearchQueue(Queue<WebPage> unsearchQueue) {
//		this.unsearchQueue = unsearchQueue;
//	}

	/**
	 * @return the searchedSites
	 */
	public synchronized List<String> getSearchedSites() {
		return searchedSitesList;
	}
	
	public synchronized void addSearchedSites(String url){
		this.searchedSitesList.add(url);
	}
	
	public synchronized Queue<String> getUnsearchList(){
		return this.unsearchSitesList;
	}
	
	public synchronized String peekUnsearchList(){
		final String url = this.unsearchSitesList.poll().toString();
//		this.unsearchSitesList.iterator().remove();
//		this.unsearchSitesList.remove();
		return url;
	}
	
	public synchronized void addUnsearchList(String url){
		this.unsearchSitesList.add(url);
	}

	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public synchronized void checkLink(String link, String startUrl) {
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

			if (this.unsearchSitesList.isEmpty()) {
				addUnsearchList(link);
				link = null;
			} else {
				String link_end_ = link.endsWith("/") ? link.substring(0, link.lastIndexOf("/")) : (link + "/");
				if (!this.unsearchSitesList.contains(link) && !this.unsearchSitesList.contains(link_end_)) {
					link_end_ = null;
					addUnsearchList(link);
				}
				link = null;
			}
		}
	}

	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public synchronized void checkLink(WebPage wp) {
		String link = wp.getUrl();
		if (link != null && !link.equals("") && link.indexOf("#") == -1) {
			if (!link.startsWith("http://") && !link.startsWith("ftp://")
					&& !link.startsWith("www.")) {
				link = "file:///" + link;
			} else if (link.startsWith("www.")) {
				link = "http://" + link;
			}
			
			//過濾非此區段開頭的網址
			final String newUrl = wp.getUrl();
			if(this.downloadRangeList.size() > 0){
				boolean isRange = false;
				for(String tmpUrl : this.downloadRangeList){
					if (newUrl.startsWith(tmpUrl)) {
						isRange = true;
						continue;
					}
				}
				if(!isRange){
					wp = null;
					return;
				}
			}
			
			if (getUnsearchQueue().isEmpty()) {
				addUnsearchQueue(wp);
				link = null;
			} else {
				// 是否已存在要搜尋的佇列中
				// 目前僅用URL作為key判斷, TODO 考慮Web Page的其他property?
				boolean isContains = false;
				for(Iterator<WebPage> iter=getUnsearchQueue().iterator();iter.hasNext();){
					WebPage tmpSrb = iter.next();
					if(tmpSrb.getUrl().equals(link)){
						isContains = true;
						continue;
					}
				}
				if(!isContains){
					addUnsearchQueue(wp);
					link = null;
				}
			}
		}
	}
	
	/**
	 * 檢查該鏈結是否已經被掃描, 如果searchedSitesList不存在此url, 代表該url需要搜尋, 回傳true; 反之則false.
	 * searchedSitesList<String>, 不用存成searchedSitesList<WebPage>以節省記憶體使用量
	 */
	public boolean isSearched(String url) {
		String url_end_ = "";
		if (url.endsWith("/")) {
			url_end_ = url.substring(0, url.lastIndexOf("/"));
		} else {
			url_end_ = url + "/";
		}
		if (this.searchedSitesList.size() > 0) {
			if (this.searchedSitesList.indexOf(url) != -1 || this.searchedSitesList.indexOf(url_end_) != -1) {
				url_end_ = null;
				return true;
			}
		}
		url_end_ = null;
		url = null;
		return false;
	}


}
