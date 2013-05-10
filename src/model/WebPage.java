/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package model;

import engine.CrawlResultFormat;

/**
 * Data structure of a web page
 */
public class WebPage implements java.io.Serializable {

	private static final long serialVersionUID = -7199419114525662897L;
	
	String url = "";
	String fatherUrl = "";
	String title = "";
	String hostName = "";
	StringBuilder text = new StringBuilder("");
	boolean urlModified = false;
	int depth = 0; //深度預設為 0 
	String htmlCode;
	String charSet = EncodingSet.ENCODE.UTF8.getValue();

	/**
	 * @return the charSet
	 */
	public String getCharSet() {
		return charSet;
	}

	/**
	 * @param charSet the charSet to set
	 */
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	/** Constructor */
	public WebPage(){
	}

	/** Constructor */
	public WebPage(final int depth, final String url){
		this.depth = depth;
		this.url = url;
	}
	
	/**
	 * 將文字包裝起來 
	 */
	public void patternText(){
		this.text.insert(0, CrawlResultFormat.webPageHead(this));
		this.text.append(CrawlResultFormat.webPageTail());
	}
	
	/**
	 * 將Html包裝起來 
	 */
	public void patternHtmlCode(){
		this.htmlCode = CrawlResultFormat.webPageHead(this) + this.htmlCode;
		this.htmlCode += CrawlResultFormat.webPageTail();
	}
	
	public void resetText(){
		this.text = new StringBuilder("");
	}
	
	/**
	 * @return the fatherUrl
	 */
	public String getFatherUrl() {
		return fatherUrl;
	}

	/**
	 * @param fatherUrl the fatherUrl to set
	 */
	public void setFatherUrl(String fatherUrl) {
		this.fatherUrl = fatherUrl;
	}
	/**
	 * @return the level
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth the level to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	/**
	 * @return the text
	 */
	public StringBuilder getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void addText(String text) {
		this.text.append(text);
	}

	/**
	 * @return the urlModified
	 */
	public boolean isUrlModified() {
		return urlModified;
	}

	/**
	 * @param urlModified the urlModified to set
	 */
	public void setUrlModified(boolean urlModified) {
		this.urlModified = urlModified;
	}
	
	public String getKeywords() {
		return hostName;
	}

	public void setKeywords(String keywords) {
		this.hostName = keywords;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the htmlCode
	 */
	public String getHtmlCode() {
		return htmlCode;
	}

	/**
	 * @param htmlCode the htmlCode to set
	 */
	public void setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
	}
}
