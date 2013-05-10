/*
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package utils;

/**
 * Proxy 設定者
 * @author A-Ho
 */
public class HttpProxySetter {

	/**
	 * Proxy 的資訊<p>
	 * Inner class 不給外面用
	 */
	public static class ProxyInfo {
		String host;
		String port;
		
		public ProxyInfo(){
		}
		
		/**
		 * @return the host
		 */
		public String getHost() {
			return host;
		}
		/**
		 * @param host the host to set
		 */
		public void setHost(String host) {
			this.host = host;
		}
		/**
		 * @return the port
		 */
		public String getPort() {
			return port;
		}
		/**
		 * @param port the port to set
		 */
		public void setPort(String port) {
			this.port = port;
		}
		
	}
	
	
	/************************************************************
	 * Dynamic proxy 
	 ************************************************************/
	public static String DYNA_PROXY_HOST = "proxy.XXX.XXX";
	public static String DYNA_PROXY_PORT = "8080";
	
	private static HttpProxySetter UrlUtil;
	
	public static HttpProxySetter getCHTTLUrlUtils() {
		if(UrlUtil == null){
			ProxyInfo proxyInfo = new ProxyInfo();
			proxyInfo.setHost(DYNA_PROXY_HOST);
			proxyInfo.setPort(DYNA_PROXY_PORT);
			UrlUtil = new HttpProxySetter(proxyInfo);
		}
		
		return UrlUtil;
	}
	
	public static void CHTTLSetting(){
		System.setProperty("http.proxyHost", DYNA_PROXY_HOST);
		System.setProperty("http.proxyPort", DYNA_PROXY_PORT);
		System.setProperty("https.proxyHost", DYNA_PROXY_HOST);
		System.setProperty("https.proxyPort", DYNA_PROXY_PORT);
	}

	/**
	 * TODO 未來考慮設計為 public ?
	 */
	private HttpProxySetter(ProxyInfo proxy) {
		System.setProperty("http.proxyHost", proxy.getHost());
		System.setProperty("http.proxyPort", proxy.getPort());
	}

}
