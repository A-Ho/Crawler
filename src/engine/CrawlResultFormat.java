/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package engine;

import model.WebPage;

/**
 * 格式化輸出的資料
 * @author 960122
 */
public class CrawlResultFormat {
	
	public static String ln = "\r\n";
	
	public static String webPageHead(WebPage wp){
		return "<page_html_code url=\"" + wp.getUrl() //
		+ "\" depth=\"" + wp.getDepth() //
		+ "\" father=\"" + wp.getFatherUrl() //
		+ "\" charset=\"" + wp.getCharSet() //
		+ "\">" + ln;
	}
	
	public static String webPageTail(){
		return "</page_html_code>" + ln;
	}
	
}
