package aho.crawler.model;

import org.apache.log4j.Logger;

public enum FileType {
  txt(".txt"), //
	html(".html"), //
	jpg(".jpg"), //
	png(".png"), //
	;

	static Logger log = Logger.getLogger(FileType.class);
	
	String type;
	
	FileType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	public static String getUrlFileType(String url){
		if(url.endsWith(FileType.jpg.getType())){
			return FileType.jpg.getType();
		} else if(url.endsWith(FileType.jpg.getType())){
			return FileType.png.getType();
		}
		log.warn("System could not analyze this url's file type: " + url);
		return null;
	}
	
}
