/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.engine.customize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.ParserException;

import aho.crawler.cobweb.Cobweb;
import aho.crawler.engine.CrawlResultFormat;
import aho.crawler.engine.ThreadSpider;
import aho.crawler.engine.WebPageParser;
import aho.crawler.model.EncodingSet;
import aho.crawler.model.WebPage;


/**
 * Yahoo lifestyle+
 * @author A-Ho
 */
public class YahooLifestyleSpider extends ThreadSpider {

	/**
	 * @throws Exception 
	 */
	public YahooLifestyleSpider(String startSite, String dirPath, Cobweb cobweb, int maxDepth) throws Exception {
		super(startSite, dirPath, cobweb, maxDepth);
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
					tmpWp.setHtmlCode(WebPageParser.toPureHtml(tmpWp));
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
						wp.setCharSet(EncodingSet.vauleOf(charSet).getValue());
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
	

}
