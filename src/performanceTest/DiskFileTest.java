/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package performanceTest;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import utils.PerformanceTester;

/**
 * 
 * @author A-Ho
 */
public class DiskFileTest {
	final long testTimes = 1000000;

	static String filePath = "E://WDMRDB//PerformanceTest//1.txt";
	
	public static void writeFileTest(int count){
		PerformanceTester pt = new PerformanceTester();
		String testName = "寫檔效能測試";
		pt.startPoint(testName);

		String fileContent = "Thread-13: http://zh.wikipedia.org/w/index.php?title=Wiki&amp;action=edit&amp;section=2";
		java.io.File file = new java.io.File(filePath);
		try {
			FileWriter fw = new FileWriter(file);
			for(int i=0;i<count;i++){
				fw.write(fileContent);
			}
			fw.close();
		} catch (IOException e) {
			System.out.println("寫入檔案發生錯誤");
			e.printStackTrace();
		}
		
		pt.stopPoint(testName);
		pt.printResult();
	}
	
	public static void readFileTest(){
		List<String> list = new LinkedList<String>();
		while(true){
			list.add(java.util.UUID.randomUUID().toString());
		}
	}
	
	public static void main(String[] args) {
		
//		writeFileTest(2000000);
		readFileTest();
	}
	
}
