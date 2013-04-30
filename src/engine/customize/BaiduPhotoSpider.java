package engine.customize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.EncodingSet;
import model.WebPage;

import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.ParserException;

import utils.HttpProxySetter;
import utils.LoadNetFile;

import cobweb.ICobweb;
import cobweb.MemoryBasedCobweb;
import engine.CrawlResultFormat;
import engine.ThreadSpider;

/**
 * @author Sean Chang
 *         2013/4/26
 */
public class BaiduPhotoSpider extends ThreadSpider {

	/**
	 * @throws Exception 
	 */
	public BaiduPhotoSpider(String startSite, String dirPath, ICobweb cobweb, int maxDepth) throws Exception {
		super(startSite, dirPath, cobweb, maxDepth);
	}

	private File leavesNodeFile;
	
	private FileWriter leavesNodeFileWriter;
	
	private void startOutput(String fileName){
		leavesNodeFile = new File(fileName + ".leavesNode.txt");
		try {
			leavesNodeFileWriter = new FileWriter(leavesNodeFile);
			leavesNodeFileWriter.write("開始時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			leavesNodeFileWriter.write("結束時間: " + getRightNowTimeStr("/", ":", " ") + "\r\n");
			leavesNodeFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void search(String fileName){
		startOutput(fileName);
		
		while(!cobweb.getUnsearchQueue().isEmpty()){
			final WebPage wp = cobweb.peekUnsearchQueue();// 查找列隊
			//初始節點
			if(wp.getDepth() == 0){
				try {
					leavesNodeFileWriter.write(wp.getUrl() + " ,level=" + wp.getDepth() + CrawlResultFormat.ln);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (!cobweb.isSearched(wp.getUrl())) {
				//
				try {
					if(wp.getDepth() <= getMaxSpyDepth()){
						processHtml(wp.getUrl(), wp.getDepth());
					}
					if(getSleepTime() > 0){//睡覺吧
						Thread.sleep(getSleepTime());
					}
				} catch (ParserException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
//			count++;
//			if(count%100 == 0){
//				System.out.println("尚未探勘數目: " + cobweb.getUnsearchQueue().size());
//				System.gc();
//			}
		}
		
		closeOutput();
		
	}
	
	/**
	 * 處理HTML標籤結點
	 */
	protected synchronized void parserNode(Node node, WebPage wp) throws Exception {
		if (node instanceof TextNode) {// 判斷是否是文本結點
//			srb.addText(((TextNode) node).getText());
		} else if (node instanceof Tag) {// 判斷是否是標籤庫結點
			Tag atag = (Tag) node;
			
			if(wp.getCharSet().equalsIgnoreCase("ISO-8859-1")){
				//抓此連結的 charSet
				if(atag instanceof MetaTag){
					final MetaTag metaTag = (MetaTag) atag;
					if(metaTag.getMetaContent().contains("charset=")){
						final String charSet = metaTag.getMetaContent().split("charset=")[1];
						wp.setCharSet(EncodingSet.getEncode(charSet).getValue());
					}
				}
			}
			
			if (atag instanceof LinkTag) { // 判斷是否是標LINK結點
				final LinkTag linkatag = (LinkTag) atag;
				String newUrl = linkatag.getLink();
				WebPage newWp = new WebPage();
				newWp.setDepth(wp.getDepth() + 1);				
				newWp.setUrl(newUrl);
				cobweb.checkLink(newWp);
			}
			
			if(atag instanceof org.htmlparser.tags.ImageTag){
				final ImageTag imageTag = (ImageTag) atag;
				String imageURL = imageTag.getImageURL();
				System.out.println(imageURL);
				if(imageURL.endsWith(".jpg")){
					LoadNetFile lf = new LoadNetFile();
					String[] splits = imageURL.split("/");
					lf.setLoadData(
							imageURL,
							"E:/WDMRDB/photo", splits[splits.length-1]+".jpg");
					lf.startLoad();
					System.out.println("Downloading... " + imageURL);
					leavesNodeFileWriter.write(imageURL + CrawlResultFormat.ln);
				}
			}
			
			dealTag(atag, wp);
			atag = null;
		}
	}
	
	public static void main(String[] args) throws Exception {
		HttpProxySetter.CHTTLSetting();
		
		final String url = "http://www.37tp.com/";
		final String filePath = "E://WDMRDB/photo";
		MemoryBasedCobweb multiThreadSpider = new MemoryBasedCobweb();
		WebPage wp = new WebPage();
		wp.setUrl(url);
		multiThreadSpider.addUnsearchQueue(wp);
		
		multiThreadSpider.addDownloadRangeList("http://www.37tp.com/");
		
		final int threadCount = 1;
		BaiduPhotoSpider[] phs = new BaiduPhotoSpider[threadCount];
		for(int i=0;i<phs.length;i++){
			phs[i] = new BaiduPhotoSpider(url, filePath, multiThreadSpider, 3);
		}
		Thread[] searchs = new Thread[threadCount];
		for(int i=0;i<searchs.length;i++){
			searchs[i] = new Thread(phs[i]);
			searchs[i].start();
//			Thread.sleep(3000);
		}
	}

}
