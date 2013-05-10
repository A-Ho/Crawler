/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 檔案處理工具
 * @author A-Ho
 */
public class FileUtils {

	public enum FileType {
		
		txt(".txt")
		,html(".html")//
		,jpg(".jpg")
		;
		
		String type;
		
		FileType(String type){
            this.type = type;			
		}
		
		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
	}

    /**
     * 刪除檔案
     */
    public static void delete(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        } else {
        	System.out.println("File does not exist. Delete aborted!");
        }
    }

    /**
     * 複製檔案
     * 支援類型:圖片...etc
     * @param inFilePath:被複製的檔案路徑
     * @param outFilePath:複製檔案路徑
     */
    public static void copyImage(String inFilePath, String outFilePath) {
        FileInputStream fin;
        FileOutputStream fout;
        try {
            fin = new FileInputStream(inFilePath);
            fout = new FileOutputStream(outFilePath);
            byte[] data = new byte[fin.available()];
            fin.read(data);
            fout.write(data);
            fout.close();
            fin.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改檔名
     */
    public static void rename(String sourceFile, String newFile) {
        File fs = new File(sourceFile);
        File fd = new File(newFile);

        if (fs.exists()) {
            if (fs.renameTo(fd)) {
//                System.out.println(fs.getName() + " has been renamed to " + fd.getName());
            } else {
            	System.out.println("Rename failed.");
            }
        } else {
        	System.out.println("File is not exit.");
        }
    }

    /**
     * 製作此路徑所需的目錄
     */
    public static void mkDirs(StringBuffer dirPath) {
        File dir = new File(dirPath.toString());
        if (!dir.isDirectory() && !dir.exists()) {
            dir.mkdirs();
        }
    }
	
	public static void newFile(String fileContent, String filePath, FileType fileType){
		java.io.File file = new java.io.File(filePath + fileType.getType());
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(fileContent);
			fw.close();
		} catch (IOException e) {
			System.out.println("製作檔案失敗");
			e.printStackTrace();
		}
	}
}
