/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package engine;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.WebPage;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cobweb.ICobweb;
import cobweb.MemoryBasedCobweb;

/**
 * 設定spider的工作內容
 * @author 960122
 */
public class XmlRuleSpider {
	
	String description;
	
	String startUrl;

	int depth;
	
	int thread;
	
	int sleepTime;
	
	String outputFilePath;
	
	String email;

	ICobweb cobweb;
	
	/**
	 * 僅提供嚴謹的建構子
	 */
	public XmlRuleSpider(final String filePath){
        this.cobweb = new MemoryBasedCobweb();
        parseXml(filePath);
	}
	
	/** 剖析XML */
	private void parseXml(final String filePath){
		DocumentBuilderFactory docBldFct = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBld = docBldFct.newDocumentBuilder();
			Document doc = docBld.parse(filePath);
			traceDom(doc);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 將 XML 的資料設定為程式的參數
	 */
	private void traceDom(Node node){
		NodeList childNodes = node.getChildNodes();
		Node childNode;
		for(int i=0; i < childNodes.getLength(); i++)
		{
			childNode = childNodes.item(i);
			Node item = childNode.getChildNodes().item(0);
			if(childNode.getNodeName().equalsIgnoreCase("description")){
				if(item != null){
					this.description = item.getTextContent();
				}
			}
			else if(childNode.getNodeName().equalsIgnoreCase("proxy-host")){
				if(item != null){
					System.setProperty("http.proxyHost", item.getTextContent());
				}
			}
			else if(childNode.getNodeName().equalsIgnoreCase("proxy-port")){
				if(item != null){
					System.setProperty("http.proxyPort", item.getTextContent());
				}
			}
			else if(childNode.getNodeName().equalsIgnoreCase("start-url")){
				String nodeValue = item.getTextContent();
				this.startUrl = nodeValue;
				WebPage wp = new WebPage();
				wp.setDepth(0);
				wp.setUrl(this.startUrl);
				this.cobweb.addUnsearchQueue(wp);
			}
			else if(childNode.getNodeName().equalsIgnoreCase("minig-depth")){
				this.depth = Integer.valueOf(item.getTextContent());
			}
			else if(childNode.getNodeName().equalsIgnoreCase("url")){
				if(item != null){
					this.cobweb.addDownloadRangeList(item.getTextContent());
				}
			}
			else if(childNode.getNodeName().equalsIgnoreCase("thread")){
				int tmp = item == null ? 1 : Integer.valueOf(item.getTextContent());
				this.thread = tmp < 1 ? 1 : tmp;
			}
			else if(childNode.getNodeName().equalsIgnoreCase("sleep-time")){
				this.sleepTime = item == null ? 1 : Integer.valueOf(item.getTextContent());
			}
			else if(childNode.getNodeName().equalsIgnoreCase("file-path")){
				this.outputFilePath = item == null ? "" : item.getTextContent();
			}
			else if(childNode.getNodeName().equalsIgnoreCase("email")){
				this.email = item == null ? "" : item.getTextContent();
			}

			if(childNode.hasChildNodes()){
				traceDom(childNode);
			}
		}
	}
	
	public static void main(String[] args) {
		String xmlFilePath = "";
		for(String s : args){
    		if(s!=null && s!=""){
    			if(s.startsWith("-s:")){
    				xmlFilePath = s.split(":")[1];
    			}
    		}
    	}
		if(xmlFilePath == ""){ // 預設檔
			xmlFilePath = "spider-config.xml";
		}
		System.out.println("剖析XML成功: " + xmlFilePath);
    	XmlRuleSpider xmlRuleSpider = new XmlRuleSpider(xmlFilePath);
    	System.out.println("=================爬行開始================");
    	System.out.println(xmlRuleSpider.toString());
    	xmlRuleSpider.spy();
	}
	
	/** 執行網站爬行 */
	public void spy(){
		ThreadSpider[] spider = new ThreadSpider[this.thread];
		for(int i=0;i<this.thread;i++){
			try {
				// 實作替換
				spider[i] = new ThreadSpider(this.startUrl, this.outputFilePath, this.cobweb, this.depth);
				spider[i].setDescription(this.description);
				spider[i].setResultFilePath(this.outputFilePath + (this.thread > 1 ? "_" +Integer.valueOf(i) : ""));
				spider[i].setSleepTime(this.sleepTime);
				spider[i].setEmail(this.email);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Thread[] searchs = new Thread[this.thread];
		for(int i=0;i<this.thread;i++){
			searchs[i] = new Thread(spider[i]);
			searchs[i].start();
			try {
				Thread.sleep(3000); //建檔需要時間
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	 * @return the startUrl
	 */
	public String getStartUrl() {
		return startUrl;
	}

	/**
	 * @param startUrl the startUrl to set
	 */
	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}
}
