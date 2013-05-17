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
import org.htmlparser.util.ParserException;

import aho.crawler.cobweb.Cobweb;
import aho.crawler.cobweb.MemoryBasedCobweb;
import aho.crawler.engine.ThreadSpider;
import aho.crawler.model.WebPage;



/**
 * 
 * @author A-Ho
 */
public class WikiTreeNodeFactorSpider extends ThreadSpider {

	/**
	 * @param startSite
	 * @param dirPath
	 * @param spiderEngine
	 * @throws Exception 
	 */
	public WikiTreeNodeFactorSpider(String startSite, String dirPath, Cobweb spiderEngine, int maxDepth) throws Exception {
		super(startSite, dirPath, spiderEngine, maxDepth);
	}

	private File forefathersNodeFile;
	
	private File leavesNodeFile;
	
	private File leavesContentFile;
	
	private FileWriter forefathersNodeFileWriter;
	
	private FileWriter leavesNodeFileWriter;
	
	private FileWriter leavesContentFileWriter;
	
	private void startOutput(String fileName){
		forefathersNodeFile = new java.io.File(fileName + ".forefathers.txt");
		leavesNodeFile = new java.io.File(fileName + ".leavesNode.txt");
		leavesContentFile = new java.io.File(fileName + ".leavesContent.txt");
		try {
			forefathersNodeFileWriter = new FileWriter(forefathersNodeFile);
			leavesNodeFileWriter = new FileWriter(leavesNodeFile);
			leavesContentFileWriter = new FileWriter(leavesContentFile);
			
			forefathersNodeFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesNodeFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			forefathersNodeFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesNodeFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesContentFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			
			forefathersNodeFileWriter.close();
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
			final WebPage srb = cobweb.peekUnsearchQueue();// 查找列隊
			
			//初使節點
			if(srb.getDepth() == 1){
				try {
					forefathersNodeFileWriter.write(srb.getUrl() + " ,level=" + srb.getDepth() + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (!cobweb.isSearched(srb.getUrl())) {
				if(srb.getDepth() <= getMaxSpyDepth()){
					//
					try {
						WebPage tmpWp = processHtml(srb.getUrl(), srb.getDepth());
						
					} catch (ParserException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
	
	/**
	 * 處理HTML標籤結點
	 */
	protected synchronized void parserNode(Node node, WebPage wp) throws Exception {
		if (node instanceof TextNode) {// 判斷是否是文本結點
//			srb.addText(((TextNode) node).getText());
		} else if (node instanceof Tag) {// 判斷是否是標籤庫結點
			Tag atag = (Tag) node;
			if (atag instanceof LinkTag) {// 判斷是否是標LINK結點
				LinkTag linkatag = (LinkTag) atag;
				final String tmpUrl = linkatag.getLink();
				WebPage newWp = new WebPage();
                newWp.setDepth(wp.getDepth()+1);				
				newWp.setUrl(tmpUrl);
				
//				System.out.println(tmpUrl);
				if(tmpUrl.startsWith("http://zh.wikipedia.org/zh-tw/")){
					if(tmpUrl.startsWith("http://zh.wikipedia.org/zh-tw/%")){
						leavesNodeFileWriter.write(tmpUrl + " ,level=" + newWp.getDepth() + " ,father=" + wp.getUrl() + "\r\n");
//						addNodeList(newSrb);
					} 
//					else if(tmpUrl.startsWith("http://zh.wikipedia.org/zh-tw/Category")){
					else {
						forefathersNodeFileWriter.write(tmpUrl + " ,level=" + newWp.getDepth() + " ,father=" + wp.getUrl()  + "\r\n");
						this.cobweb.checkLink(newWp);
					}
				}
				
			}
			dealTag(atag, wp);
			atag = null;
		}
	}

	
}

