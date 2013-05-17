/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.cobweb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import aho.crawler.engine.CrawlResultFormat;
import aho.crawler.engine.ThreadSpider;
import aho.crawler.engine.WebPageParser;
import aho.crawler.model.WebPage;



/**
 * Multi-thread spider engine, tmp data are stored in a file
 * @author A-Ho
 */
public class FileBasedCobweb extends ThreadSpider {
	
	public FileBasedCobweb(String startSite, String dirPath, Cobweb spiderEngine) {
		super(startSite, dirPath, spiderEngine);
	}
	
	private File leavesContentFile;
	
	private FileWriter leavesContentFileWriter;
	
	private void startOutput(String fileName){
		leavesContentFile = new java.io.File(fileName + ".leavesContent.txt");
		try {
			leavesContentFileWriter = new FileWriter(leavesContentFile);
			leavesContentFileWriter.write("FileBasedThreadSpider Start: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			leavesContentFileWriter.write("Stop: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*  */
	public void spy(String fileName){
		search(fileName);
	}
	
	protected void search(String fileName){
		
		startOutput(fileName);
		
		while(!cobweb.getUnsearchQueue().isEmpty()){
			final WebPage wp = cobweb.peekUnsearchQueue();// 查找列隊
			
			if (!cobweb.isSearched(wp.getUrl())) {
				try {
					leavesContentFileWriter.write(CrawlResultFormat.webPageHead(wp));
					leavesContentFileWriter.write(WebPageParser.toPureHtml(wp));
					leavesContentFileWriter.write(CrawlResultFormat.webPageTail());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		closeOutput();
	}
	
	
}
