/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Calendar;

import model.EncodingSet;
import model.WebPage;
import model.EncodingSet.ENCODE;

import org.htmlparser.nodes.TextNode;
import org.htmlparser.*;
import org.htmlparser.tags.*;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.MailUtils;

import cobweb.ICobweb;

/**
 * 線程化的網頁擷取器
 * @author A-Ho
 */
public class ThreadSpider implements Runnable {

	/**  */
	protected int count = 0;

	/**  */
	protected long lastTime = 0;

	/**  */
	protected String dirPath = "";

	/**  */
	private int miningDepth = -1;
	
	/**  */
	protected ENCODE encode = ENCODE.UTF8; //擷取網頁的編碼預設為utf-8

	/**  */
	protected ICobweb cobweb;
	
	/**  */
	private String resultFilePath;
	
	/**  */
	private String description = "";
	
	/** 擷取完的email address */
	private String email;

	/** 抓完一個網頁後的等待時間 */
	private int sleepTime = 0;
	
	/** 一個檔案的最大包含網頁數量，預設值 */
	private int fileSize = 1000;

	public ThreadSpider(String startSite, String dirPath, ICobweb spiderEngine) {
		this.cobweb = spiderEngine;
		this.dirPath = dirPath;
		// 幫建目錄防呆
		File indexDirectory = new File(dirPath);  
		if (!indexDirectory.exists()) {   
            indexDirectory.mkdirs();   
		}
	}
	
	public ThreadSpider(String startSite, String dirPath, ICobweb spiderEngine, int miningDepth) throws Exception {
		this.cobweb = spiderEngine;
		this.dirPath = dirPath;
		// 幫建目錄防呆
		File indexDirectory = new File(dirPath);  
		if (!indexDirectory.exists()) {   
            indexDirectory.mkdirs();   
		}
		if(miningDepth < 0){
			throw new Exception("探勘深度小於0，這是昇天不是探勘...");
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
			leavesNodeFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
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
			final WebPage wp = cobweb.peekUnsearchQueue();// 查找列隊
			//初始節點
			if(wp.getDepth() == 0){
				try {
					leavesNodeFileWriter.write(wp.getUrl() + " ,level=" + wp.getDepth() + CrawlResultFormat.ln);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (!cobweb.isSearched(wp.getUrl())) {
				//
				try {
					if(wp.getDepth() <= getMaxSpyDepth()){
						processHtml(wp.getUrl(), wp.getDepth());
					}
					if(getSleepTime() > 0){//睡覺吧
						Thread.sleep(getSleepTime());
					}
					WebPage tmpWp = new WebPage();
					tmpWp.setDepth(wp.getDepth());
					tmpWp.setUrl(wp.getUrl());
					tmpWp.setCharSet(wp.getCharSet());
					tmpWp.setHtmlCode(engine.Parser.toPureHtml(tmpWp));
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
			
//			count++;
//			if(count%100 == 0){
//				System.out.println("尚未探勘數目: " + cobweb.getUnsearchQueue().size());
//				System.gc();
//			}
		}
		
		closeOutput();
		
		// 爬行完成，發送通知
		if(this.email != null && this.email != ""){
			MailUtils.mailTo(this.email, "網頁擷取完畢通知信", "您好，<BR>  您的" + this.description + "已擷取成功。");
			System.out.println("完成且發送通知信");
		}
	}
	
	/**
	 * 解析HTML
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
//		parser.setEncoding("UTF-8");
		
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
		// XML化
//		wp.patternText();

		parser = null;
		uc = null;
		nit = null;
		
		return wp;
	}
	
	/**
	 * 處理HTML標籤結點
	 */
	protected synchronized void parserNode(Node node, WebPage wp) throws Exception {
		if (node instanceof TextNode) {// 判斷是否是文本結點
//			srb.addText(((TextNode) node).getText());
		} else if (node instanceof Tag) {// 判斷是否是標籤庫結點
			Tag atag = (Tag) node;
			
			if(wp.getCharSet().equalsIgnoreCase("ISO-8859-1")){
				//抓此連結的 charSet
				if(atag instanceof MetaTag){
					final MetaTag metaTag = (MetaTag) atag;
					if(metaTag.getMetaContent().contains("charset=")){
						final String charSet = metaTag.getMetaContent().split("charset=")[1];
						wp.setCharSet(EncodingSet.getEncode(charSet).getValue());
					}
				}
			}
			
			if (atag instanceof LinkTag) { // 判斷是否是標LINK結點
				final LinkTag linkatag = (LinkTag) atag;
				String newUrl = linkatag.getLink();
				WebPage newWp = new WebPage();
				newWp.setDepth(wp.getDepth() + 1);				
				newWp.setUrl(newUrl);
				cobweb.checkLink(newWp);
			}
			
			dealTag(atag, wp);
			atag = null;
		}
	}

	/**
	 * 處理HTML標籤
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
