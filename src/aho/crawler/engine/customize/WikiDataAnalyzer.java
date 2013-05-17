/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.engine.customize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import aho.crawler.cobweb.FileBasedCobweb;
import aho.crawler.cobweb.MemoryBasedCobweb;
import aho.crawler.model.WebPage;



/**
 * 
 * @author A-Ho
 */
public class WikiDataAnalyzer {
	
	static Set<String> leavesSet = new HashSet<String>();
	
	private static int buildLeavesSet(final String filePath) throws IOException{
		File leavesFile = new File(filePath);
		FileReader leavesFileReader = new FileReader(leavesFile);
		BufferedReader br = new BufferedReader(leavesFileReader);
		int count = 0;
		String line;
		while((line = br.readLine()) != null){
			count++;
			leavesSet.add(line);
		}
		br.close();
		return count;
	}
	
	public static void divideLargeFile(final String filePath) throws IOException {
		int fileIndex = 0;
		File extractFile = new File("E://WDMRDB//Wiki//2009-11-5_LeavesNode_" + fileIndex + ".txt");
		FileWriter extractFileWriter = new FileWriter(extractFile);
		int count=0;
		for(String tmp : leavesSet){
			count++;
			extractFileWriter.write(tmp + "\r\n");
			if(count % 10000 == 0){ //每10,000筆就存成新檔，否則檔案太大之後處理擷取網頁JVM會爆掉
				extractFileWriter.close();
				extractFile = new File("E://WDMRDB//Wiki//2009-11-5_LeavesNode_" + (fileIndex++) + ".txt");
				extractFileWriter = new FileWriter(extractFile);
			}
		}
	}
	
	public static int countPageHtmlCode(final String filePath) throws IOException{
		File file = new File(filePath);
		FileReader leavesFileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(leavesFileReader);
		int count = 0;
		String line;
		while((line = br.readLine()) != null){
			if(line.contains("page_html_code")){
				count++;
			}
		}
		br.close();
		return count;
	}
	
	public static void spy(final String inputFilePath) throws IOException{
		MemoryBasedCobweb multiThreadSpider = new MemoryBasedCobweb();
		
		File leavesFile = new File(inputFilePath);
		FileReader leavesFileReader = new FileReader(leavesFile);
		BufferedReader br = new BufferedReader(leavesFileReader);
		int count = 0;
		String line;
		while((line = br.readLine()) != null){
			count++;
			leavesSet.add(line);
		}
		br.close();
		for(String tmpUrl : leavesSet){
			String[] split = tmpUrl.split(" ,");
			if(split.length != 3){
				continue;
			}
			multiThreadSpider.addUnsearchQueue(new WebPage(Integer.valueOf(split[1].substring(6,7)), split[0]));
		}
		FileBasedCobweb phs = new FileBasedCobweb(null, null, multiThreadSpider);
		phs.spy(inputFilePath);

	}

}
