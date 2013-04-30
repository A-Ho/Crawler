/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import utils.HttpProxySetter;

import model.WebPage;

/**
 * 網頁剖析器
 * @author 960122
 */
public class Parser {

	/** 擷取該網頁的Html */
	public static String toPureHtml(WebPage wp){
		final StringBuffer result = new StringBuffer();
		try {
			final URL linkUrl = new URL(wp.getUrl());
			final HttpURLConnection httpConnection = (java.net.HttpURLConnection) linkUrl.openConnection();
			httpConnection.connect();
			final java.io.InputStream urlStream = httpConnection.getInputStream();
			BufferedReader bufferReader = new BufferedReader(new java.io.InputStreamReader(urlStream, wp.getCharSet()));
			String tmpLine = "";
			while ((tmpLine = bufferReader.readLine()) != null) {
				result.append(tmpLine).append("\n");
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("剖析網頁發生錯誤:\n" + e);
		} catch (Exception e) {
			System.out.println("連接網頁錯誤:\n" + e);
		}
		
		return result.toString();
	}
	
	
	public static void main(String[] args) throws InterruptedException {
//		HttpProxySetter.IIISetting();
		for(int i=0;i<100;i++){
			WebPage wp = new WebPage(0, "http://www.cloud.org.tw/?q=node");
//            Thread.sleep(30000);			
            System.out.println("這是第幾次: " + i);
		}
	}
}
