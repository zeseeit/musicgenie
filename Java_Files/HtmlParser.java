import java.io.IOException;  
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;  
import org.jsoup.nodes.DataNode;
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
		
		String intermediate_URL="http://www.listentoyoutube.com/process.php?url=https://www.youtube.com";
		String intermediate_URL2="http://www.listentoyoutube.com/middle.php?server=srv42&hash=4pWVbWtlnGRtY7Wr2NmXarVhnGdra2pwl5aWtIWZ26aZoY2nv9LYrK6SzQ%253D%253D&file=SANAM%20RE%20Title%20Song%20FULL%20VIDEO%20%7C%20Pulkit%20Samrat%2C%20Yami%20Gautam%2C%20Urvashi%20Rautela%20%7C%20Divya%20Khosla%20Kumar.mp3";
		
		int i=in.nextInt();
		// check if it is of valid range and isdigit()
		String song_title=songs_title.get(i);
		String song_link=songs_links.get(i);
		System.out.println("Title : "+ song_title +"  Link : " + song_link);
		 intermediate_URL+= song_link;
		System.out.println(intermediate_URL);
		
		
		Document doc2 = Jsoup.connect(intermediate_URL2).get(); 
		
		 Elements scriptTags = doc2.getElementsByTag("script");
		/* for (Element tag : scriptTags){                
		        for (DataNode node : tag.dataNodes()) {
		            System.out.println(node.getWholeData());
		        }    
		 }
		  */
		 String download_link = null;
		String songdownloadLink=null;
		Elements linkss=doc2.select("a[href]");
		for(Element link : linkss)	
		{
			if(link.attr("href").matches("download.php?(.*)"))
			{
				download_link=new String(link.attr("href"));
				String prefix=new String("www.listentoyoutube.com/");
				download_link=prefix.concat(download_link);
				
				break;
			}
		}
		System.out.println(download_link);
		String download_link2="http://www.listentoyoutube.com/download.php?server=srv42&hash=4pWVbWtlnGRtY7Wr2NmXarVhnGdra2pwl5aWtIWZ26aZoY2nv9LYrK6SzQ%3D%3D&file=SANAM%20RE%20Title%20Song%20FULL%20VIDEO%20|%20Pulkit%20Samrat%2C%20Yami%20Gautam%2C%20Urvashi%20Rautela%20|%20Divya%20Khosla%20Kumar.mp3";
		
		Document downloadpage=Jsoup.connect(download_link2).get();
		System.out.println(downloadpage);
		Elements downloadlink=downloadpage.select("href");
		for(Element link: downloadlink)
		{
			if(link.attr("href").matches("http://srv(.*)listentoyoutube.com/download/(.*)"))
			{
				songdownloadLink=new String(link.attr("href"));
				System.out.println(songdownloadLink);
				break;
			}
		}
		
		

	}

}
