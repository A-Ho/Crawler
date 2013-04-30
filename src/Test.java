import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Test extends Thread{

	
	public void kkboxCounter() throws IOException {
		HashSet<String> uniSet = new HashSet<String>();
		
		String regex = "[0-9]";
		int count = 1;
		String filename = "E:\\kkbox.txt";
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line = "";
		while((line = in.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);
			while(st.hasMoreTokens()){
				String tmp = st.nextToken("(");
				if(tmp.substring(0, 1).matches(regex)){
					String id = tmp.substring(0, 9);
					if(!uniSet.add(id)){
						System.out.println(id);
					}
					count++;
				}
			}
		}
        System.out.println(count);			
        System.out.println(uniSet.size());
	}
	
	public void run(){
		System.out.println("a");
	}

	public static void main(String[] args) throws Exception {
		switch(5){
			case 1:
				System.out.println("1");
			case 2: 
				System.out.println("2");
				break;
			case 5:
				System.out.println("5");
			default:
				System.out.println("none");
		}
		
		
	}


	
}
