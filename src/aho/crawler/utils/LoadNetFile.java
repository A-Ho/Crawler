package aho.crawler.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * 2010/04/18 使用執行緒下載網路檔案,可同時產生多個同時下載
 * 
 * @author 吉他手
 */
public class LoadNetFile implements Runnable {
	private boolean isRun;
	private String urlPath;
	private String savePath;
	private String fileName;
	private int speedKB; // 每秒下載kb,0:不限制
	private int currentBits;
	private BufferedInputStream bufInStream;
	private FileOutputStream fileOutStream;

	public LoadNetFile() {
		initLoadNetFile();
	}

	public void initLoadNetFile() {
		isRun = true;
		urlPath = "";
		savePath = "";
		fileName = "";
		speedKB = 0;
	}

	public void startLoad() {
		new Thread(this).start();
	}

	public void stopLoad() {
		isRun = false;
	}

	/**
	 * 設定要下載的檔案網址與要存檔檔名
	 * 
	 * @param url 下載網址
	 * @param tmpFname 存檔檔名
	 * @return
	 */
	public String setLoadData(String url, String saveP, String tmpFname) {
		urlPath = url;
		try {
			URL zeroFile = new URL(urlPath);
			bufInStream = new BufferedInputStream(zeroFile.openStream());
			String name = zeroFile.getFile();
			String tmpName = name.substring(name.lastIndexOf("."),
					name.length());// 取得副檔名
			fileName = ((tmpFname.indexOf(".") == -1) ? (tmpFname + tmpName) : tmpFname);
			savePath = saveP + fileName;
			fileOutStream = new FileOutputStream(savePath);
		} catch (Exception e) {
			e.printStackTrace();
			close();
			return "error";
		}
		return "";
	}

	public void run() {
		try {
			System.out.println("開始下載[" + fileName + "]");
			byte[] b = new byte[1024];// 一次取得 1024 個 bytes
			int len, bits = 0;// len單次讀取位元數,bits每秒累計下載位元數
			while ((len = bufInStream.read(b, 0, b.length)) != -1) {
				if (!isRun) {
					// System.out.println("停止下載");
					break;
				}
				fileOutStream.write(b, 0, len);
				bits += len;
				currentBits += len;
				if (bits >= (speedKB * 1024) && speedKB != 0) {// 1秒下載的kb足夠就sleep1秒
					bits = 0;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				System.out.println("檔案[" + fileName + "]目前下載[" + getCurrentKB()
						+ "]kb");
			}
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 設定每秒下載速度
	 * 
	 * @param kb
	 */
	public void setLoadKB(int kb) {
		if (kb <= -1) {
			kb = 0;
		}
		speedKB = kb;
	}

	/**
	 * 取得目前已下載位元數
	 * 
	 * @return
	 */
	public int getCurrentBits() {
		return currentBits;
	}

	/**
	 * 取得目前已下載kb
	 * 
	 * @return
	 */
	public double getCurrentKB() {
		double kb = Math.floor((currentBits / 1024.0) * 1000 + 0.001) / 1000;// 目前下載kb數
		return kb;
	}

	/**
	 * 取得存檔路徑
	 * 
	 * @return
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * 取得下載檔名
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	public void close() {
		try {
			if (fileOutStream != null) {
				fileOutStream.close();
			}
			if (bufInStream != null) {
				bufInStream.close();
			}
			fileOutStream = null;
			bufInStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
