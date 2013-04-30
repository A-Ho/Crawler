/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package database;

/**
 * SQL 查詢後得到的物件
 * @author 960122
 */
public class ResultMap {

	private String key;
	
	private String value;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
