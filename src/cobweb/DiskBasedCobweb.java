/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package cobweb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.WebPage;


import utils.FileUtils;
import utils.PerformanceTester;
import utils.HttpProxySetter;

/**
 * 多線程 Spider Engine, 暫存資料由記憶體處理 
 * @author 960122
 */
public class DiskBasedCobweb implements ICobweb {

	private List<String> searchedSites = new ArrayList<String>();// 已經被搜索站點列表

	private Queue<String> unsearchList = new LinkedList<String>();// 需解析的鏈結列表
	
	private File searchedSitesFile;
	
	private FileWriter searchedSitesFileWriter;
	
	private File unsearchedSitesFile;
	
	private FileWriter unsearchedSitesFileWriter;
	
	String searchedSitesFilePath = "E:\\WDMRDB\\searchedSites.txt";
	
	String tmpSearchedSitesFilePath = "E:\\WDMRDB\\searchedSites.txt.tmp";
	
	BufferedReader in;
	BufferedWriter bw;
	
	public DiskBasedCobweb(){
		try {
			in = new BufferedReader(new FileReader(searchedSitesFilePath));
			bw = new BufferedWriter(new FileWriter(tmpSearchedSitesFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// PS. file path 不能直接直接放在某個disk底下, 起碼要有一層資料夾去包著, 否則無法讀寫
		//
		
//		this.searchedSitesFile = new File("E:\\WDMRDB\\searchedSites.txt");
//		try {
//			this.searchedSitesFileWriter = new FileWriter(this.searchedSitesFile);
//		} catch (IOException e) {
//			System.out.println("製作已經被搜索站點列表檔案失敗");
//			e.printStackTrace();
//		}
//
//		
//		this.unsearchedSitesFile = new File("E:\\WDMRDB\\unsearchedSites.txt");
//		try {
//			this.unsearchedSitesFileWriter = new FileWriter(this.unsearchedSitesFile);
//		} catch (IOException e) {
//			System.out.println("製作已經被搜索站點列表檔案失敗");
//			e.printStackTrace();
//		}
		
	}

	/**
	 * @return the searchedSites
	 */
	public List<String> getSearchedSites() {
		
		return searchedSites;
	}
	
	public synchronized void addSearchedSites(String url){
		
		try{
			
			
			String line = "";
			while((line = in.readLine()) != null){
//				System.out.println(line);
				bw.write(line + "\r\n");
//				bw.write(line);
//				bw.write("\r\n");
			}
//			in.reset();
			bw.write(url);
//			in.close();
//			bw.close();
//			FileUtils.delete(searchedSitesFilePath);
//			FileUtils.rename(tmpSearchedSitesFilePath , searchedSitesFilePath);
		}catch (Exception ex){
			System.out.println(ex);
		}
	}
	
	
	public static void main(String[] args) {
//		HttpProxySetter.IIISetting();
		
		final String goalName = "ez";
//		final String wikiUrl = "http://zh.wikipedia.org/zh-tw/%E7%B6%AD%E5%9F%BA%E7%99%BE%E7%A7%91";
		final String wikiUrl = "http://www.2888.me";
		final String filePath = "E://WDMRDB//Wiki//";
		DiskBasedCobweb multiThreadSpider = new DiskBasedCobweb();
		PerformanceTester pt = new PerformanceTester();
		pt.startPoint("peek");
		for(int i=0;i<30;i++){
			multiThreadSpider.addSearchedSites(wikiUrl);
		}
		
		pt.stopPoint("peek");
		pt.printResult();
		
//		int threadCount = 4;
//		ThreadSpider[] phs = new ThreadSpider[threadCount];
//		for(int i=0;i<phs.length;i++){
//			phs[i] = new ThreadSpider(goalName, wikiUrl, filePath, multiThreadSpider);
//		}
//		Thread[] searchs = new Thread[threadCount];
//		for(int i=0;i<searchs.length;i++){
//			searchs[i] = new Thread(phs[i]);
//			searchs[i].start();
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}


	}
	
	public Queue<String> getUnsearchList(){
		this.unsearchList.add("getUnsearchList()");
		
		return this.unsearchList;
	}
	
	public synchronized String peekUnsearchList(){
		return peekUrl("E:\\WDMRDB\\unsearchedSites.txt");
	}
	
	public synchronized void addUnsearchList(String url){
		String filename = "E:\\WDMRDB\\unsearchedSites.txt";
		try{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String tmpFileName = filename + ".tmp";
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileName));
			String line = "";
			while((line = in.readLine()) != null){
				bw.write(line + "\r\n");
			}
			
			bw.write(url);
			in.close();
			bw.close();
			FileUtils.delete(filename);
			FileUtils.rename(tmpFileName , filename);
		}catch (Exception ex){
			System.out.println(ex);
		}
	}

	/**
	 * 取出第一個 URL
	 */
	private String peekUrl(String filename){
		String firstUrl = "";
		try{
			int i = 0;
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String tmpFileName = filename + ".tmp";
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileName));
			String line = "";
			while((line = in.readLine()) != null){
				if(i == 0){
					firstUrl = line;
					i++;
					continue;
				}
				bw.write(line + "\r\n");
			}
			in.close();
			bw.close();
			FileUtils.delete(filename);
			FileUtils.rename(tmpFileName , filename);
		}catch (Exception ex){
			System.out.println(ex);
		}
		return firstUrl;
	}
	
	
	/**
	 * 檢查鏈結是否需要加入列隊
	 */
	public void checkLink(String link, String startUrl) {
		if (link != null && !link.equals("") && link.indexOf("#") == -1) {
			if (!link.startsWith("http://") && !link.startsWith("ftp://")
					&& !link.startsWith("www.")) {
				link = "file:///" + link;
			} else if (link.startsWith("www.")) {
				link = "http://" + link;
			}
			//過濾非此區段開頭的網址
//			if (!link.startsWith(startUrl)) {
//				link = null;
//				return;
//			}
			startUrl = null;

			if (this.unsearchList.isEmpty()) {
				addUnsearchList(link);
				link = null;
			} else {
				String link_end_ = link.endsWith("/") ? link.substring(0, link.lastIndexOf("/")) : (link + "/");
				if (!this.unsearchList.contains(link) && !this.unsearchList.contains(link_end_)) {
					link_end_ = null;
					addUnsearchList(link);
				}
				link = null;
			}
		}
	}
	
	

	/**
	 * 檢查該鏈結是否已經被掃描
	 */
	public boolean isSearched(String url) {
		String url_end_ = "";
		if (url.endsWith("/")) {
			url_end_ = url.substring(0, url.lastIndexOf("/"));
		} else {
			url_end_ = url + "/";
		}
		if (this.searchedSites.size() > 0) {
			if (this.searchedSites.indexOf(url) != -1 || this.searchedSites.indexOf(url_end_) != -1) {
				url_end_ = null;
				return true;
			}
		}
		url_end_ = null;
		url = null;
		return false;
	}

	@Override
	public void checkLink(WebPage srb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Queue<WebPage> getUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebPage peekUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUnsearchQueue(WebPage unsearchQueue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDownloadRangeList(String url) {
		// TODO Auto-generated method stub
		
	}
	

}
