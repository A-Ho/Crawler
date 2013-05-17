/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.cobweb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import aho.crawler.model.WebPage;



/**
 * Multi-thread spider engine, tmp data are stored on memory
 * @author A-Ho
 */
public class MemoryBasedCobweb extends Cobweb {

	final static Logger log = Logger.getLogger(MemoryBasedCobweb.class);
	
	/**
	 * Data in memoried
	 */
	private List<String> searchedSitesList = new ArrayList<String>();

	private Queue<String> unsearchSitesList = new LinkedList<String>();

	private Queue<WebPage> unsearchQueue = new LinkedList<WebPage>();
	
	/**
	 * Implements interface
	 */
	public synchronized List<String> getSearchedSites() {
		return searchedSitesList;
	}
	
	public synchronized void addSearchedSites(String url){
		this.searchedSitesList.add(url);
	}
	
	public synchronized void addUnsearchList(String url){
		this.unsearchSitesList.add(url);
	}
	
	
	public synchronized Queue<String> getUnsearchList(){
		return this.unsearchSitesList;
	}
	
	public synchronized String peekUnsearchList(){
		final String url = this.unsearchSitesList.poll().toString();
		return url;
	}
	
	public synchronized void addUnsearchQueue(WebPage wp) {
		getUnsearchQueue().add(wp);
	}
	
	public synchronized Queue<WebPage> getUnsearchQueue() {
		return unsearchQueue;
	}
	

}
