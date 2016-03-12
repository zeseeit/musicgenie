package Default;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetMusicLink {
	
	public void getMusic(Document newdoc) throws IOException
	{
		String download_link = null;
		String songdownloadLink=null;
		Elements links=newdoc.select("a[href]");
		for(Element link : links)	
		{
			if(link.attr("href").matches("download.php?(.*)"))
			{
				download_link=new String(link.attr("href"));
				String prefix=new String("www.listentoyoutube.com/");
				download_link=prefix.concat(download_link);
				
				break;
			}
		}
		
		Document downloadpage=Jsoup.connect(download_link).get();
		Elements downloadlink=downloadpage.select("href");
		for(Element link: downloadlink)
		{
			if(link.attr("href").matches("http://srv36.listentoyoutube.com/download/(.*)"))
			{
				songdownloadLink=new String(link.attr("href"));
			}
		}
		
		
		
		
		
		
		
		
	}

}
