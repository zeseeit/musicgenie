import java.io.IOException;  
import java.util.ArrayList;
import java.util.*;

import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;  
import org.jsoup.nodes.Element;  
import org.jsoup.select.Elements; 
public class HtmlParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{  
		Scanner in = new Scanner (System.in);
		Document doc = Jsoup.connect("https://www.youtube.com/results?search_query=sanam+re").get();  
		Elements links = doc.select("a[href]");  
		System.out.println("Pick One of the song to download");
		int counter=0;
		ArrayList<String> songs_links = new ArrayList<String>();
		ArrayList<String> songs_title = new ArrayList<String>();
		for (Element link : links) {  
		    if(link.attr("href").matches("/watch/?(.*)") && !(link.text().matches("[0-9]*:[0-9]*")))
		    {
		    System.out.println("\nlink : " + link.attr("href"));  
		    songs_links.add(link.attr("href"));
		    System.out.println("text : " + link.text()); 
		    songs_title.add(link.text());
		    System.out.println("INdex : "+ counter);
		    counter++;
		    }
		}  

		System.out.println("Stage One Cleared-----------------");
		
		//String intermediate_URL="http://www.listentoyoutube.com/process.php?url=https://www.youtube.com";
		
		

		

	}

}
