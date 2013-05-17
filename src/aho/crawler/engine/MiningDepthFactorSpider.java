/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;

import aho.crawler.cobweb.Cobweb;
import aho.crawler.database.SqlUpdater;
import aho.crawler.model.WebPage;


/**
 * 
 * @author A-Ho
 */
public class MiningDepthFactorSpider extends ThreadSpider {
	
	final static Logger log = Logger.getLogger(SqlUpdater.class);

	/**
	 * @param startSite
	 * @param dirPath
	 * @param spiderEngine
	 */
	public MiningDepthFactorSpider(String startSite, String dirPath, Cobweb spiderEngine) {
		super(startSite, dirPath, spiderEngine);
	}

	/**
	 * @param startSite
	 * @param dirPath
	 * @param spiderEngine
	 * @throws Exception 
	 */
	public MiningDepthFactorSpider(String startSite, String dirPath, Cobweb spiderEngine, int maxDepth) throws Exception {
		super(startSite, dirPath, spiderEngine, maxDepth);
	}

	protected void search(String fileName){
		File file = new java.io.File(fileName + ".html.txt");
		File fileHistory = new java.io.File(fileName + ".history.txt");
		FileWriter fw = null;
		FileWriter fwHistory = null;
		try {
			fw = new FileWriter(file);
			fwHistory = new FileWriter(fileHistory);
			fw.write("Start: " + Calendar.getInstance().getTime() + "\r\n");
			fwHistory.write("Stop: " + Calendar.getInstance().getTime() + "\r\n");
		} catch (IOException e1) {
			log.error("Write file error: " + e1.getMessage());
		}

		lastTime = Calendar.getInstance().getTimeInMillis();
		while(!cobweb.getUnsearchQueue().isEmpty()){
			final WebPage srb = cobweb.peekUnsearchQueue();// 查找列隊
			try {
				if (!cobweb.isSearched(srb.getUrl())) {
					// 儲存 html code
					
					// 儲存歷史紀錄
					fwHistory.write(srb.getUrl() + "\r\n");
					// 抓出此頁面內的超連結
					if(srb.getDepth() <= getMaxSpyDepth()){
						WebPage tmpSrb = processHtml(srb.getUrl(), srb.getDepth());
						fw.write("<page_html_code url=\"" + srb.getUrl()+"\" level=\""+srb.getDepth() + "\">");
						fw.write(tmpSrb.getHtmlCode());
//						fw.write(engine.Parser.toPureHtml(srb));
						fw.write("</page_html_code>\r\n");
						tmpSrb = null;
					}
					
//					Thread.sleep(30000);
					
					count++;
					if(count % 50 == 0){
						System.gc();
					}
					if (count % 100 == 0) {
						fwHistory.write("Unsearch web links count: " + this.cobweb.getUnsearchList().size() + "\r\n");
						fwHistory.write("Searched web links count: " + this.cobweb.getSearchedSites().size() + "\r\n");
						long crossTime = Calendar.getInstance().getTimeInMillis() - lastTime;
						fwHistory.write("100 fetch page waste time: " + crossTime + "毫秒("+ getRightNowTimeStr("-", ":", "_") + ")\r\n");
						lastTime = Calendar.getInstance().getTimeInMillis();
						System.gc();
					}
					if (count % 1000 == 0) {
						count = 0;
						fw.close();
						fw = null;
						file = null;
						file = new java.io.File(createFileName() + ".html.txt");
						fw = new FileWriter(file);
					}
				}

			
			} catch (Exception ex) {

			}
		}
		
		// Jobs finished, close files.
		try {
			fw.write("結束時間: " + Calendar.getInstance().getTime() + "\r\n");
			fwHistory.write("結束時間: " + Calendar.getInstance().getTime() + "\r\n");
			fw.close();
			fwHistory.close();
		} catch (IOException e) {
			log.error("Jobs finished, close files failed: " + e.getMessage());
		}
		
	}
	
	protected synchronized void parserNode(Node node, WebPage wp) throws Exception {
		
		if(wp.getHtmlCode()== null || wp.getHtmlCode() == ""){
			wp.setHtmlCode(node.getPage().getText());
		}
//			srb.addText(node.getPage().getText()+"\r\n");
		if (node instanceof Tag) {
			Tag atag = (Tag) node;
			if (atag instanceof LinkTag) {
				LinkTag linkatag = (LinkTag) atag;
				wp.setUrl(linkatag.getLink());
				this.cobweb.checkLink(wp);
			}
			dealTag(atag, wp);
			atag = null;
		}
	}

}
