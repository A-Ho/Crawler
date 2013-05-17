/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.cobweb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import aho.crawler.model.FileType;
import aho.crawler.model.WebPage;


/**
 * The container that is saved whole web links.
 * Single inteface for 3 kinds of JVM Memory, File, DB implementations.
 * 
 * @author A-Ho
 */
public abstract class Cobweb {

	protected List<String> downloadRangeList = new LinkedList<String>();
	
	public synchronized void addDownloadRangeList(String url){
		this.downloadRangeList.add(url);
	}


	
	/**
	 * @return the unsearchQueue
	 */
	public synchronized WebPage peekUnsearchQueue() {
		final WebPage result = (WebPage)getUnsearchQueue().poll();
		return result;
	}
	
	/**
	 * Check the link if neccesarily join to waiting-crawl queue
	 */
	public synchronized void checkLink(String link) {
		checkLink(new WebPage(0, link));
	}

	/**
	 * Check the WebPage if neccesarily join to waiting-crawl queue
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
			
			//Filter download range links
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
				// TODO Lookup key is URL, consider other properties?
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
	 * Check status of link-fetching status. Fetch this when searchedSitesList do not contain this URL
	 * 
	 * Save the searchedSitesList<String> but not searchedSitesList<WebPage> for saving memory space.
	 */
	public boolean isSearched(String url) {
		String url_end_ = "";
		if (url.endsWith("/")) {
			url_end_ = url.substring(0, url.lastIndexOf("/"));
		} else {
			url_end_ = url + "/";
		}
		if (getSearchedSites().size() > 0) {
			if (getSearchedSites().indexOf(url) != -1 || getSearchedSites().indexOf(url_end_) != -1) {
				url_end_ = null;
				return true;
			}
		}
		url_end_ = null;
		url = null;
		return false;
	}
	/*******************************************************************
	 * Download files
	 *******************************************************************/
	Set<String> downloadFileTypes = new HashSet<String>();
	
	public void addDownloadFileType(FileType fileType){
		this.downloadFileTypes.add(fileType.getType());
	}
	
	public Set<String> getDownloadFileTypes(){
		return this.downloadFileTypes;
	}
	
	
	/*******************************************************************
	 * interfaces
	 *******************************************************************/
	
	public abstract List<String> getSearchedSites();
	
	public abstract void addSearchedSites(String url);
	
	public abstract Queue<String> getUnsearchList();
	
	public abstract Queue<WebPage> getUnsearchQueue();
	
	public abstract void addUnsearchQueue(WebPage unsearchQueue);

}
