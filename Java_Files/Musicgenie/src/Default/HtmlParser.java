package Default;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class HtmlParser {

/**
 * @param args
 */
public static void main(String[] args) throws IOException{  
    Document doc = Jsoup.connect("https://www.youtube.com/results?search_query=sanam+re").get();  
    
    Elements links = doc.select("a[href]");  
    for (Element link : links) {  
    	if(link.attr("href").matches("/watch/?(.*)") && !(link.text().matches("[0-9]*:[0-9]*")))
    	{
        System.out.println("\nlink : " + link.attr("href"));  
        System.out.println("text : " + link.text()); 
    	}
    }
    
    
    
    



}

} 