/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;

import cobweb.ICobweb;
import cobweb.MemoryBasedCobweb;

import utils.HttpProxySetter;

import model.WebPage;

/**
 * 
 * @author 960122
 */
public class MiningDepthFactorSpider extends ThreadSpider {

	/**
	 * @param startSite
	 * @param dirPath
	 * @param spiderEngine
	 */
	public MiningDepthFactorSpider(String startSite, String dirPath, ICobweb spiderEngine) {
		super(startSite, dirPath, spiderEngine);
	}

	/**
	 * @param startSite
	 * @param dirPath
	 * @param spiderEngine
	 * @throws Exception 
	 */
	public MiningDepthFactorSpider(String startSite, String dirPath, ICobweb spiderEngine, int maxDepth) throws Exception {
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
			fw.write("開始時間: " + Calendar.getInstance().getTime() + "\r\n");
			fwHistory.write("開始時間: " + Calendar.getInstance().getTime() + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
						fwHistory.write("需解析的鏈結列表: " + this.cobweb.getUnsearchList().size() + "\r\n");
						fwHistory.write("已經被搜索站點列表: " + this.cobweb.getSearchedSites().size() + "\r\n");
						long crossTime = Calendar.getInstance().getTimeInMillis() - lastTime;
						fwHistory.write("此回搜尋用了: " + crossTime + "毫秒("+ getRightNowTimeStr("-", ":", "_") + ")\r\n");
						lastTime = Calendar.getInstance().getTimeInMillis();
						//
						//System.gc();
						//
						System.out.println("需解析的鏈結列表: " + this.cobweb.getUnsearchList().size());
						System.out.println("已經被搜索站點列表: " + this.cobweb.getSearchedSites().size());
						System.out.println("此回搜尋用了: " + crossTime);
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
		
		// 全部爬完，關檔案
		try {
			fw.write("結束時間: " + Calendar.getInstance().getTime() + "\r\n");
			fwHistory.write("結束時間: " + Calendar.getInstance().getTime() + "\r\n");
			fw.close();
			fwHistory.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected synchronized void parserNode(Node node, WebPage wp) throws Exception {
		
		if(wp.getHtmlCode()== null || wp.getHtmlCode() == ""){
			
//			System.out.println("##: "+node.getPage().getText());
			wp.setHtmlCode(node.getPage().getText());
		}
//			srb.addText(node.getPage().getText()+"\r\n");
		
		if (node instanceof Tag) {// 判斷是否是標籤庫結點
			Tag atag = (Tag) node;
			if (atag instanceof LinkTag) {// 判斷是否是標LINK結點
				LinkTag linkatag = (LinkTag) atag;
				wp.setUrl(linkatag.getLink());
				this.cobweb.checkLink(wp);
			}
			dealTag(atag, wp);
			atag = null;
		}
	}
	
	public static void main(String[] args) throws Exception {
		HttpProxySetter.CHTTLSetting();
		
		final String url = "http://www.cloud.org.tw/?q=node";
		final String filePath = "E://WDMRDB//chttl//";
		MemoryBasedCobweb multiThreadSpider = new MemoryBasedCobweb();
		WebPage wp = new WebPage();
		wp.setDepth(2);
		wp.setUrl(url);
		multiThreadSpider.addUnsearchQueue(wp);
		
		multiThreadSpider.addDownloadRangeList("http://www.cloud.org.tw/?q=node");
		
		final int threadCount = 1;
		
		MiningDepthFactorSpider[] phs = new MiningDepthFactorSpider[threadCount];
		for(int i=0;i<phs.length;i++){
			phs[i] = new MiningDepthFactorSpider(url, filePath, multiThreadSpider, 2);
		}
		Thread[] searchs = new Thread[threadCount];
		for(int i=0;i<searchs.length;i++){
			searchs[i] = new Thread(phs[i]);
			searchs[i].start();
			
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}


	}
}
