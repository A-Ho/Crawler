/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package cobweb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import engine.CrawlResultFormat;
import engine.Parser;
import engine.ThreadSpider;

import model.WebPage;

/**
 * 實作一個讀取準備好的WebPage集合的文字檔Spider
 * @author 960122
 */
public class FileBasedCobweb extends ThreadSpider {
	
	public FileBasedCobweb(String startSite, String dirPath, ICobweb spiderEngine) {
		super(startSite, dirPath, spiderEngine);
	}
	
	private File leavesContentFile;
	
	private FileWriter leavesContentFileWriter;
	
	private void startOutput(String fileName){
		leavesContentFile = new java.io.File(fileName + ".leavesContent.txt");
		try {
			leavesContentFileWriter = new FileWriter(leavesContentFile);
			leavesContentFileWriter.write("FileBasedThreadSpider 開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			leavesContentFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 以process去執行探勘  */
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
					leavesContentFileWriter.write(Parser.toPureHtml(wp));
					leavesContentFileWriter.write(CrawlResultFormat.webPageTail());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				count++;
				if(count%100 == 0){
					System.out.println("尚未探勘數目: "+cobweb.getUnsearchQueue().size());
					System.gc();
				}
			}
		}
		
		closeOutput();
	}
	
	
}
