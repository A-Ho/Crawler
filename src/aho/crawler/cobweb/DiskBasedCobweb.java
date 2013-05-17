/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.cobweb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import aho.crawler.model.WebPage;
import aho.crawler.utils.FileUtils;



/**
 * Multi-thread spider engine, tmp data are stored on disk
 * @author A-Ho
 */
public class DiskBasedCobweb extends Cobweb {

	final static Logger log = Logger.getLogger(DiskBasedCobweb.class);
	
	private File searchedSitesFile;
	
	private File unsearchedSitesFile;
	
	private FileWriter unsearchedSitesFileWriter;
	
	private FileWriter searchedSitesFileWriter;

	String searchedSitesFilePath = "E:\\WDMRDB\\searchedSites.txt";
	String unarchedSitesFilePath = "E:\\WDMRDB\\unsearchedSites.txt";
	
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
		
		this.searchedSitesFile = new File(searchedSitesFilePath);
		try {
			this.searchedSitesFileWriter = new FileWriter(this.searchedSitesFile);
		} catch (IOException e) {
			log.error("searchedSitesFileWriter write failed.");
		}

		
		this.unsearchedSitesFile = new File(unarchedSitesFilePath);
		try {
			this.unsearchedSitesFileWriter = new FileWriter(this.unsearchedSitesFile);
		} catch (IOException e) {
			log.error("unsearchedSitesFileWriter write failed.");
		}
		
	}

	/**
	 * TODO
	 */
	public List<String> getSearchedSites() {
		List<String> searchedSites = new ArrayList<String>();
		// TODO
		
		return searchedSites;
	}
	
	public synchronized void addSearchedSites(String url){
		try{
			String line = "";
			while((line = in.readLine()) != null){
				bw.write(line + "\r\n");
			}
			bw.write(url);
		}catch (Exception ex){
			System.out.println(ex);
		}
	}
	
	/*
	 * TODO
	 */
	public Queue<String> getUnsearchList(){
//		this.unsearchList.add(getUnsearchList().);
		//TODO read file
		
		return null;
	}
	
	public synchronized String peekUnsearchList(){
		return peekUrl(unarchedSitesFilePath);
	}
	
	public synchronized void addUnsearchList(String url){
		String filename = unarchedSitesFilePath;
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
	 * Fetch the first URL in queue.
	 * TODO not complete
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
			log.error("Peek URL failed: " + ex);
		}
		return firstUrl;
	}

	@Override
	public Queue<WebPage> getUnsearchQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUnsearchQueue(WebPage unsearchQueue) {
		// TODO Auto-generated method stub
		
	}

}
