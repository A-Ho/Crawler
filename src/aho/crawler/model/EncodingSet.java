/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.model;

/**
 * Encode
 * @author A-Ho
 */
public class EncodingSet {

	/** Web page encodes */
	public static enum ENCODE {
		Big5("big5") //
		, UTF8("utf-8") //
		, MS950("MS950") //
		, GB2312("GB2312") //
		;
		
		ENCODE(String value){
			this.value = value;
		}

		private String value;
		
		public String getValue(){
			return this.value;
		}
	}
	
	/**  */
	public static ENCODE vauleOf(String value){
		if(value.equalsIgnoreCase(ENCODE.Big5.getValue())){
			return ENCODE.Big5;
		} else if(value.equalsIgnoreCase(ENCODE.UTF8.getValue())){
			return ENCODE.UTF8;
		} else if(value.equalsIgnoreCase(ENCODE.MS950.getValue())){
			return ENCODE.MS950;
		} else if(value.equalsIgnoreCase(ENCODE.GB2312.getValue())){
			return ENCODE.GB2312;
		} 
		
		// Default
		return ENCODE.UTF8;
	}
	
}
