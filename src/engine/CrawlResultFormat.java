/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package engine;

import model.WebPage;

/**
 * The format of the output .txt file
 * 
 * @author A-Ho
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
