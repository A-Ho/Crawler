/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package engine.customize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cobweb.FileBasedCobweb;
import cobweb.MemoryBasedCobweb;

import model.WebPage;

import utils.HttpProxySetter;

/**
 * 
 * @author 960122
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
		return count;
	}
	
	public static void divideLargeFile(final String filePath) throws IOException {
		int allLeavesCount = buildLeavesSet(filePath);
//		System.out.println("總共擷取的葉子數目: " + allLeavesCount);
//		System.out.println("沒有重複的葉子數目: " + leavesSet.size());
		
		File leavesFile = new File(filePath);
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
		return count;
	}
	
	public static void spy(final String inputFilePath) throws IOException{
//		HttpProxySetter.IIISetting();
		
//		final String outputFilePath = "E://WDMRDB//Wiki//";
		MemoryBasedCobweb multiThreadSpider = new MemoryBasedCobweb();
		
//		buildLeavesSet(inputFilePath);
		File leavesFile = new File(inputFilePath);
		FileReader leavesFileReader = new FileReader(leavesFile);
		BufferedReader br = new BufferedReader(leavesFileReader);
		int count = 0;
		String line;
		while((line = br.readLine()) != null){
			count++;
			leavesSet.add(line);
		}
		for(String tmpUrl : leavesSet){
			String[] split = tmpUrl.split(" ,");
			if(split.length != 3){
				continue;
			}
			multiThreadSpider.addUnsearchQueue(new WebPage(Integer.valueOf(split[1].substring(6,7)), split[0]));
		}
		System.out.println("此檔案吃進的 url 數量: "+count);
		FileBasedCobweb phs = new FileBasedCobweb(null, null, multiThreadSpider);
		phs.spy(inputFilePath);

	}
	
    public static void main(String[] args) throws Exception {
//    	WikiDataAnalyzer.divideLargeFile("E:\\WDMRDB\\Wiki\\2009-11-5_18-17-27.leavesNode.txt.ext");
    	
//    	for(int i=0;i<=21;i++){
//    		spy("E:\\WDMRDB\\Wiki\\2009-11-5_18-17-27.leavesNode.txt.ext("+i+")");
//    	}
    	
//    	spy("E:\\WDMRDB\\Wiki\\切成220份\\2009-11-5_LeavesNode_10.txt");
    	for(String s : args){
    		if(s!=null && s!=""){

    			if(s.startsWith("-s:")){
    				spy(s.split(":")[1]);
    				System.out.println(s.split(":")[1]);
    			}
    			
    		}
    	}
    	
//    	System.out.println(countPageHtmlCode("E:\\WDMRDB\\Wiki\\2009-11-5_18-17-27.leavesNode.txt.ext(0).leavesContent.txt"));
    }
    
}
