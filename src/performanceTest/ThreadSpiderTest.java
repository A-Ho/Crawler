package performanceTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Calendar;

import model.WebPage;

import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.*;
import org.htmlparser.tags.*;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import cobweb.ICobweb;


/**
 * 
 */
public class ThreadSpiderTest implements Runnable {

	private int count = 0;

	private long lastTime = 0;

	private String hostName = "";

	private String dirPath = "";

	private ICobweb spiderEngine;
	
	public ThreadSpiderTest(String hostName, String startSite, String dirPath, ICobweb spiderEngine) {
		this.hostName = hostName;
		this.dirPath = dirPath;
		File indexDirectory = new File(dirPath);  
		if (!indexDirectory.exists()) {   
            indexDirectory.mkdirs();   
		}
		
		this.spiderEngine = spiderEngine;
//		for(int i=0;i<100;i++){
//			this.spiderEngine.checkLink(java.util.UUID.randomUUID().toString(), "http://zh.wikipedia.org");
//		}
	}
	
	private String createFileName(){
		return this.dirPath + getRightNowTimeStr("-", "-", "_");
	}

	public void run() {
		final String fileName = createFileName();
		search(fileName);
	}

	private String getRightNowTimeStr(final String dateDep, final String secDep, final String dep) {
		final String month = (Calendar.getInstance().get(Calendar.MONTH) + 1) > 9 //
		? String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1)
				: "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1);

		return //
		Calendar.getInstance().get(Calendar.YEAR) //
				+ dateDep + month //
				+ dateDep + (Calendar.getInstance().get(Calendar.DATE)) //
				+ dep + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) //
				+ secDep + (Calendar.getInstance().get(Calendar.MINUTE)) //
				+ secDep + (Calendar.getInstance().get(Calendar.SECOND));
	}

	/**
	 * 爬網站
	 */
	private void search(String fileName) {
        
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

		String url = "";
		lastTime = Calendar.getInstance().getTimeInMillis();
		while(!this.spiderEngine.getUnsearchList().isEmpty()){
			url = this.spiderEngine.peekUnsearchList();// 查找列隊
			try {
				if (!this.spiderEngine.isSearched(url)) {
					WebPage srb = processHtml(url);
					fw.write(srb.getText().toString());
					fwHistory.write(url + "\r\n");
					srb = null;
					url = null;
					count++;
					if(count % 50 == 0){
						System.gc();
						System.out.println("GC:"+count);
					}
					
					if (count % 100 == 0) {
						fwHistory.write("需解析的鏈結列表: " + this.spiderEngine.getUnsearchList().size() + "\r\n");
						fwHistory.write("已經被搜索站點列表: " + this.spiderEngine.getSearchedSites().size() + "\r\n");
						long crossTime = Calendar.getInstance().getTimeInMillis() - lastTime;
						fwHistory.write("此回搜尋用了: " + crossTime + "毫秒("+ getRightNowTimeStr("-", ":", "_") + ")\r\n");
						lastTime = Calendar.getInstance().getTimeInMillis();
						//
						//System.gc();
						//
						System.out.println("需解析的鏈結列表: " + this.spiderEngine.getUnsearchList().size());
						System.out.println("已經被搜索站點列表: " + this.spiderEngine.getSearchedSites().size());
						System.out.println("此回搜尋用了: " + crossTime);
						System.out.println(count);
					}
					if (count % 1000 == 0) {
						count = 0;
						fw.close();
						fw = null;
						file = null;
						file = new java.io.File(createFileName() + ".html.txt");
						fw = new FileWriter(file);
						System.out.println(count);
					}
				}

			} catch (Exception ex) {

			}
		}
	}

	/**
	 * 解析HTML
	 * 
	 * @param url
	 * @throws ParserException
	 * @throws Exception
	 */
	private synchronized WebPage processHtml(String url) throws ParserException, Exception {
		this.spiderEngine.addSearchedSites(url);
		//
		Parser parser = new Parser();
//		parser.setURL("http://www.iii.org.tw/");
		parser.setURL(url);
		parser.setEncoding("UTF-8");
		
		System.out.println(Thread.currentThread().getName() + ": " + url);
		URLConnection uc = parser.getConnection();
		uc.connect();
		//
		WebPage srb = new WebPage();
		NodeIterator nit = parser.elements();
		while (nit.hasMoreNodes()) {
			parserNode((Node)nit.nextNode(), srb);
		}
//		parserNode(new LinkTag(), srb);

		srb.setKeywords(hostName);
		srb.setUrl(url);
		// XML 化
		srb.patternText();

		parser = null;
		uc = null;
		nit = null;
		
		return srb;
	}

	/**
	 * 處理HTML標籤結點
	 * 
	 * @param node
	 * @throws Exception
	 */
	private synchronized void parserNode(Node node, WebPage srb) throws Exception {
		if (node instanceof TextNode) {// 判斷是否是文本結點
			srb.addText(((TextNode) node).getText());
		} else if (node instanceof Tag) {// 判斷是否是標籤庫結點
			Tag atag = (Tag) node;
			if (atag instanceof TitleTag) {// 判斷是否是標TITLE結點
				srb.setTitle(atag.getText());
			}
			if (atag instanceof LinkTag) {// 判斷是否是標LINK結點
				LinkTag linkatag = (LinkTag) atag;
				this.spiderEngine.checkLink(linkatag.getLink(), "");
			}
			dealTag(atag, srb);
			atag = null;
		} else if (node instanceof RemarkNode) {// 判斷是否是注釋
			// System.out.println("this is remark");
		}
	}

	/**
	 * 處理HTML標籤
	 * 
	 * @param tag
	 * @throws Exception
	 */
	private void dealTag(Tag tag, WebPage srb) throws Exception {
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

}
