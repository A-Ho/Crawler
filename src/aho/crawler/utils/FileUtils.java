/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import aho.crawler.model.FileType;


/**
 * @author A-Ho
 */
public class FileUtils {

	static Logger log = Logger.getLogger(FileUtils.class);
	
	public static void loadFile(String imageURL, String dir, String fileExt){
		LoadNetFile lf = new LoadNetFile();
		String[] splits = imageURL.split("/");
		lf.setLoadData(
				imageURL,
				dir, splits[splits.length-1] + fileExt);
		lf.startLoad();
	}
	
    public static void delete(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        } else {
        	log.error("File does not exist. Delete aborted!");
        }
    }

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

    public static void rename(String sourceFile, String newFile) {
        File fs = new File(sourceFile);
        File fd = new File(newFile);

        if (fs.exists()) {
            if (fs.renameTo(fd)) {
//                log.info(fs.getName() + " has been renamed to " + fd.getName());
            } else {
            	log.error("Rename failed.");
            }
        } else {
        	log.error("File is not exit.");
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
			log.error("Create file failed.");
			e.printStackTrace();
		}
	}
}
