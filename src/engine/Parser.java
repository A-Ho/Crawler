/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import model.WebPage;

/**
 * Web page parser
 * @author A-Ho
 */
public class Parser {

	/** Fetch Html from the web page*/
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
			System.out.println("Parsing error:\n" + e);
		} catch (Exception e) {
			System.out.println("Connecting error:\n" + e);
		}
		
		return result.toString();
	}
	
}
