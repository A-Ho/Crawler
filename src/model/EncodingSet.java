/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package model;

/**
 * 編碼集合
 * @author 960122
 */
public class EncodingSet {

	/** 網頁編碼種類 */
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
	
	/** 取得編碼種類 */
	public static ENCODE getEncode(String value){
		if(value.equalsIgnoreCase(ENCODE.Big5.getValue())){
			return ENCODE.Big5;
		} else if(value.equalsIgnoreCase(ENCODE.UTF8.getValue())){
			return ENCODE.UTF8;
		} else if(value.equalsIgnoreCase(ENCODE.MS950.getValue())){
			return ENCODE.MS950;
		} else if(value.equalsIgnoreCase(ENCODE.GB2312.getValue())){
			return ENCODE.GB2312;
		} 
		
		// 預設UTF8
		return ENCODE.UTF8;
	}
	
	public static void main(String[] args) {
//		System.out.println(ENCODE.Big5.getValue());
		System.out.println(getEncode("big5"));
	}
	
}