package utils;

/**
 * Proxy 設定者
 *  
 * @author 960122
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
	
//	public static String III_PROXY_HOST = "proxy.iii.org.tw";
//	public static String III_PROXY_PORT = "3128";
//	
//	/** 資策會 Proxy 設定 */
//	private static HttpProxySetter IIIUrlUtil;
//	
//	/** 取得資策會內部使用的 UrlUtils */
//	public static HttpProxySetter getIIIUrlUtils() {
//		if(IIIUrlUtil == null){
//			ProxyInfo proxyInfo = new ProxyInfo();
//			proxyInfo.setHost(III_PROXY_HOST);
//			proxyInfo.setPort(III_PROXY_PORT);
//			IIIUrlUtil = new HttpProxySetter(proxyInfo);
//		}
//		
//		return IIIUrlUtil;
//	}

//	public static void IIISetting(){
//		System.setProperty("http.proxyHost", III_PROXY_HOST);
//		System.setProperty("http.proxyPort", III_PROXY_PORT);
//	}

	
	/************************************************************
	 * CHTTL Proxy 
	 ************************************************************/
	public static String CHTTL_PROXY_HOST = "proxy.cht.com.tw";
	public static String CHTTL_PROXY_PORT = "8080";
	
	private static HttpProxySetter CHTTLUrlUtil;
	
	/** 取得資策會內部使用的 UrlUtils */
	public static HttpProxySetter getCHTTLUrlUtils() {
		if(CHTTLUrlUtil == null){
			ProxyInfo proxyInfo = new ProxyInfo();
			proxyInfo.setHost(CHTTL_PROXY_HOST);
			proxyInfo.setPort(CHTTL_PROXY_PORT);
			CHTTLUrlUtil = new HttpProxySetter(proxyInfo);
		}
		
		return CHTTLUrlUtil;
	}
	
	public static void CHTTLSetting(){
		System.setProperty("http.proxyHost", CHTTL_PROXY_HOST);
		System.setProperty("http.proxyPort", CHTTL_PROXY_PORT);
		System.setProperty("https.proxyHost", CHTTL_PROXY_HOST);
		System.setProperty("https.proxyPort", CHTTL_PROXY_PORT);
	}

//	public HttpProxySetter(String url) {
//	}

	/**
	 * TODO[昇賀] 未來考慮設計為 public ?
	 */
	private HttpProxySetter(ProxyInfo proxy) {
		System.setProperty("http.proxyHost", proxy.getHost());
		System.setProperty("http.proxyPort", proxy.getPort());
	}

}
