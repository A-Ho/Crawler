/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Calendar;


import org.apache.log4j.Logger;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.*;
import org.htmlparser.tags.*;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import aho.crawler.cobweb.Cobweb;
import aho.crawler.model.EncodingSet;
import aho.crawler.model.FileType;
import aho.crawler.model.WebPage;
import aho.crawler.model.EncodingSet.ENCODE;
import aho.crawler.utils.FileUtils;
import aho.crawler.utils.MailUtils;


/**
 * Thread of crawler
 * 
 * @author A-Ho
 */
public class ThreadSpider implements Runnable {
	
	Logger log = Logger.getLogger(ThreadSpider.class);
	
	public static String ISO_8895_1= "ISO-8859-1";

	/**  */
	protected int count = 0;

	/**  */
	protected long lastTime = 0;

	/**  */
	protected String dirPath = "";

	/**  */
	private int miningDepth = -1;
	
	/**  */
	protected ENCODE encode = ENCODE.UTF8; // Default

	/**  */
	protected Cobweb cobweb;
	
	/**  */
	private String resultFilePath;
	
	/**  */
	private String description = "";
	
	/** Notify email address */
	private String email;

	/** Sleep time after fetching one page for avoiding some web limitation */
	private int sleepTime = 0;
	
	/** Max fetching url in one file */
	private int fileSize = 1000;

	public ThreadSpider(String startSite, String dirPath, Cobweb spiderEngine) {
		this.cobweb = spiderEngine;
		this.dirPath = dirPath;
		// Create folder
		File indexDirectory = new File(dirPath);  
		if (!indexDirectory.exists()) {   
            indexDirectory.mkdirs();   
		}
	}
	
	public ThreadSpider(String startSite, String dirPath, Cobweb spiderEngine, int miningDepth) throws Exception {
		this.cobweb = spiderEngine;
		this.dirPath = dirPath;
		// Create folder
		File indexDirectory = new File(dirPath);  
		if (!indexDirectory.exists()) {   
            indexDirectory.mkdirs();   
		}
		if(miningDepth < 0){
			log.fatal("Min mining depth must > 0");
			throw new Exception("Min mining depth must > 0");
		}
		this.miningDepth = miningDepth;
	}
	
	protected String createFileName(){
		return this.dirPath + getRightNowTimeStr("-", "-", "_");
	}

	public void run() {
		if(this.resultFilePath == null || this.resultFilePath == ""){
			this.resultFilePath = createFileName();
		}
		search(this.resultFilePath);
	}

	private File leavesNodeFile;
	
	private File leavesContentFile;
	
	private FileWriter leavesNodeFileWriter;
	
	private FileWriter leavesContentFileWriter;
	
	private void startOutput(String fileName){
		leavesNodeFile = new java.io.File(fileName + ".leavesNode.txt");
		leavesContentFile = new java.io.File(fileName + ".leavesContent.txt");
		try {
			leavesNodeFileWriter = new FileWriter(leavesNodeFile);
			leavesContentFileWriter = new FileWriter(leavesContentFile);
			leavesNodeFileWriter.write("Start: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.write("Stop: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			leavesNodeFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesNodeFileWriter.close();
			leavesContentFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void search(String fileName){
		startOutput(fileName);
		
		while(!cobweb.getUnsearchQueue().isEmpty()){
			final WebPage wp = cobweb.peekUnsearchQueue();
			// Write some info at the first URL 
			if(wp.getDepth() == 0){
				try {
					leavesNodeFileWriter.write(wp.getUrl() + " ,level=" + wp.getDepth() + CrawlResultFormat.ln);
				} catch (IOException e) {
					log.error("leavesNodeFileWriter.txt write failed: " + e.getMessage());
				}
			}
			
			if (!cobweb.isSearched(wp.getUrl())) {
				//
				try {
					if(wp.getDepth() <= getMaxSpyDepth()){
						processHtml(wp.getUrl(), wp.getDepth());
					}
					if(getSleepTime() > 0){
						Thread.sleep(getSleepTime());
					}
					WebPage tmpWp = new WebPage();
					tmpWp.setDepth(wp.getDepth());
					tmpWp.setUrl(wp.getUrl());
					tmpWp.setCharSet(wp.getCharSet());
					tmpWp.setHtmlCode(aho.crawler.engine.WebPageParser.toPureHtml(tmpWp));
					tmpWp.patternHtmlCode();
					leavesContentFileWriter.write(tmpWp.getHtmlCode());
					leavesNodeFileWriter.write(tmpWp.getUrl() + CrawlResultFormat.ln);
					tmpWp = null;
				} catch (ParserException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		closeOutput();
		
		// Jobs finished, send mail.
		if(this.email != null && this.email != ""){
			MailUtils.mailTo(this.email, "Crawling jobs were done!", "Hi，<BR> " + this.description + "Crawling jobs were done!");
			log.info(" Jobs finished, send mail.");
		}
	}
	
	/**
	 * Parse HTML
	 * @param url
	 * @param depth
	 * @throws ParserException
	 * @throws Exception
	 */
	protected synchronized WebPage processHtml(final String url, final int depth) //
	throws ParserException, Exception {
		this.cobweb.addSearchedSites(url);
		//
		Parser parser = new Parser();
		parser.setURL(url);
		URLConnection uc = parser.getConnection();
		uc.connect();
		//
		WebPage wp = new WebPage();
		wp.setDepth(depth);
		wp.setUrl(url);
		wp.setCharSet(parser.getEncoding());
		NodeIterator nit = parser.elements();
		while (nit.hasMoreNodes()) {
			parserNode((Node)nit.nextNode(), wp);
		}

		parser = null;
		uc = null;
		nit = null;
		
		return wp;
	}
	
	/**
	 * Parse HTML tag node
	 */
	protected synchronized void parserNode(Node node, WebPage wp) throws Exception {
		if (node instanceof TextNode) {
			// srb.addText(((TextNode) node).getText());
		} else if (node instanceof Tag) {
			Tag atag = (Tag) node;
			
			if(wp.getCharSet().equalsIgnoreCase(ISO_8895_1)){
				//Set charset of this page
				if(atag instanceof MetaTag){
					final MetaTag metaTag = (MetaTag) atag;
					if(metaTag.getMetaContent().contains("charset=")){
						final String charSet = metaTag.getMetaContent().split("charset=")[1];
						wp.setCharSet(EncodingSet.vauleOf(charSet).getValue());
					}
				}
			}
			
			if (atag instanceof LinkTag) { 
				final LinkTag linkatag = (LinkTag) atag;
				String newUrl = linkatag.getLink();
				WebPage newWp = new WebPage();
				newWp.setDepth(wp.getDepth() + 1);				
				newWp.setUrl(newUrl);
				cobweb.checkLink(newWp);
			}
			
			// Process downloading images
			if (cobweb.getDownloadFileTypes().contains(FileType.jpg.getType()) //
			||	cobweb.getDownloadFileTypes().contains(FileType.png.getType())) {
				if (atag instanceof org.htmlparser.tags.ImageTag) {
					final ImageTag imageTag = (ImageTag) atag;
					String imageURL = imageTag.getImageURL();
					String fileExt = FileType.getUrlFileType(imageURL);
					if (null != fileExt) {
						FileUtils.loadFile(imageURL, this.dirPath, fileExt);
					}
				}
			}	
			
			dealTag(atag, wp);
			atag = null;
		}
	}
	
	
	
	/**
	 * Deal html tag
	 * 
	 * @param tag
	 * @throws Exception
	 */
	protected void dealTag(Tag tag, WebPage srb) throws Exception {
		NodeList list = tag.getChildren();
		if (list != null) {
			NodeIterator it = list.elements();
			while (it.hasMoreNodes()) {
				parserNode(it.nextNode(), srb);
			}
			it = null;
		}
		list = null;
	}

	/**
	 * Get formatting time sting at right now
	 */
	protected String getRightNowTimeStr(final String dateDep, final String secDep, final String dep) {
		final String month = (Calendar.getInstance().get(Calendar.MONTH) + 1) > 9 //
		? String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1) //
				: "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1);

		return 
		Calendar.getInstance().get(Calendar.YEAR) //
		+ dateDep + month //
		+ dateDep + (Calendar.getInstance().get(Calendar.DATE)) //
		+ dep + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) //
		+ secDep + (Calendar.getInstance().get(Calendar.MINUTE)) //
		+ secDep + (Calendar.getInstance().get(Calendar.SECOND));
	}
	
	
	/**
	 * @return the maxSpyDepth
	 */
	public int getMaxSpyDepth() {
		return miningDepth;
	}

	/**
	 * @param maxSpyDepth the maxSpyDepth to set
	 */
	public void setMaxSpyDepth(int maxSpyDepth) {
		this.miningDepth = maxSpyDepth;
	}
	
	/**
	 * @return the encode
	 */
	public ENCODE getEncode() {
		return encode;
	}

	/**
	 * @param encode the encode to set
	 */
	public void setEncode(ENCODE encode) {
		this.encode = encode;
	}
	
	/**
	 * @return the resultFilePath
	 */
	public String getResultFilePath() {
		return resultFilePath;
	}

	/**
	 * @param resultFilePath the resultFilePath to set
	 */
	public void setResultFilePath(String resultFilePath) {
		this.resultFilePath = resultFilePath;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the sleepTime
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * @param sleepTime the sleepTime to set
	 */
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
