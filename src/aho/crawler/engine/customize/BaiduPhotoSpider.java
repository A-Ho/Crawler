package aho.crawler.engine.customize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.htmlparser.util.ParserException;

import aho.crawler.cobweb.Cobweb;
import aho.crawler.engine.CrawlResultFormat;
import aho.crawler.engine.ThreadSpider;
import aho.crawler.model.WebPage;


/**
 * @author A-Ho
 */
public class BaiduPhotoSpider extends ThreadSpider {

	/**
	 * @throws Exception 
	 */
	public BaiduPhotoSpider(String startSite, String dirPath, Cobweb cobweb, int maxDepth) throws Exception {
		super(startSite, dirPath, cobweb, maxDepth);
	}

	private File leavesNodeFile;
	
	private FileWriter leavesNodeFileWriter;
	
	private void startOutput(String fileName){
		leavesNodeFile = new File(fileName + ".leavesNode.txt");
		try {
			leavesNodeFileWriter = new FileWriter(leavesNodeFile);
			leavesNodeFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			leavesNodeFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesNodeFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void search(String fileName){
		startOutput(fileName);
		
		while(!cobweb.getUnsearchQueue().isEmpty()){
			final WebPage wp = cobweb.peekUnsearchQueue();// 查找列隊
			System.out.println("!cobweb.getUnsearchQueue().isEmpty(): " + wp.getUrl());
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
					tmpWp.setUrl(wp.getUrl());
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
	


}
