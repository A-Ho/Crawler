package performanceTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.WebPage;

import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.parserapplications.WikiCapturer;
import org.htmlparser.*;
import org.htmlparser.tags.*;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import cobweb.ICobweb;

import utils.PerformanceTester;
import utils.HttpProxySetter;


/**
 * 
 */
public class SpiderTest implements Runnable {

	private int count = 0;

	private long lastTime = 0;

	private String hostName = "";

	private String dirPath = "";

	private ICobweb spiderEngine;
	
	static PerformanceTester pt = new PerformanceTester();
	
	public SpiderTest(String hostName, String startSite, String dirPath, ICobweb spiderEngine) {
		this.hostName = hostName;
		this.dirPath = dirPath;
		this.spiderEngine = spiderEngine;
	}
	
	private String createFileName(){
		return this.dirPath + getRightNowTimeStr("-", "-", "_");
	}

	public void run() {
		final String fileName = createFileName();
		search(fileName);
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
	}
	
	/**
	 * 檢查該鏈結是否已經被掃描
	 */
	public static boolean isSearched(String url, List<String> list) {
		String url_end_ = "";
		if (url.endsWith("/")) {
			url_end_ = url.substring(0, url.lastIndexOf("/"));
		} else {
			url_end_ = url + "/";
		}
		if (list.size() > 0) {
			if (list.indexOf(url) != -1 || list.indexOf(url_end_) != -1) {
				url_end_ = null;
				return true;
			}
		}
		url_end_ = null;
		url = null;
		return false;
	}
	
	public static void checkLink(String link, String startUrl, List<String>list) {
		if (link != null && !link.equals("") && link.indexOf("#") == -1) {
			if (!link.startsWith("http://") && !link.startsWith("ftp://")
					&& !link.startsWith("www.")) {
				link = "file:///" + link;
			} else if (link.startsWith("www.")) {
				link = "http://" + link;
			}

			if (list.isEmpty()) {
				link = null;
			} else {
				String link_end_ = link.endsWith("/") ? link.substring(0, link.lastIndexOf("/")) : (link + "/");
				if (!list.contains(link) && !list.contains(link_end_)) {
					link_end_ = null;
				}
				link = null;
			}
		}
	}
	
	public static void main(String[] args) {
//		HttpProxySetter.CHTTLSetting();
		SpiderTest st = new SpiderTest("", "", "", null);
		try {
			st.processHtml("http://www.cloud.org.tw/?q=node");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private synchronized WebPage processHtml(String url) throws ParserException, Exception {
		Parser parser = new Parser();
		parser.setURL(url);
		parser.setEncoding("UTF-8");
		
		URLConnection uc = parser.getConnection();
		uc.connect();
		//
		WebPage srb = new WebPage();
		
//		System.out.println(parser.getLexer().);
		
		NodeIterator nit = parser.elements();
		while (nit.hasMoreNodes()) {
			parserNode((Node)nit.nextNode(), srb);
		}
		srb.setKeywords(hostName);
		srb.setUrl(url);

		srb.patternText();

		parser = null;
		uc = null;
		
		return srb;
	}

	private synchronized void parserNode(Node node, WebPage srb) {
		if (node instanceof TextNode) {// 判斷是否是文本結點
			srb.addText(((TextNode) node).getText());
			System.out.println(((TextNode) node).getText());
		} else if (node instanceof Tag) {// 判斷是否是標籤庫結點
			Tag atag = (Tag) node;
			System.out.println(((Tag) node).getText());
			if (atag instanceof TitleTag) {// 判斷是否是標TITLE結點
				srb.setTitle(atag.getText());
			}
			if (atag instanceof LinkTag) {// 判斷是否是標LINK結點
				LinkTag linkatag = (LinkTag) atag;
			}
			dealTag(atag, srb);
			atag = null;
		} else if (node instanceof RemarkNode) {// 判斷是否是注釋
			 System.out.println((RemarkNode) node);
		}
	}

	/**
	 * 處理HTML標籤
	 * 
	 * @param tag
	 * @throws Exception
	 */
	private void dealTag(Tag tag, WebPage srb) {
		NodeList list = tag.getChildren();
		if (list != null) {
			NodeIterator it = list.elements();
			try {
				while (it.hasMoreNodes()) {
					parserNode(it.nextNode(), srb);
				}
			} catch (ParserException e) {
				System.out.println("取得 node 錯誤");
			}
			it = null;
		}
		list = null;
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
}
