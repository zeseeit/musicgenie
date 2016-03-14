package com.mathapp.saurabhjn76.musicgenie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by saurabh on 12/3/16.
 */
public class MusicListAdapter extends ArrayAdapter<Song> {
    Context context;
    ArrayList<YoutubeLink> songList;

    TextView title,artist,progressPercentage;
    ProgressBar progressBar;
    ImageView dnldBtn;

    private static MusicListAdapter mInstance;

    public MusicListAdapter(Context context) {
        super(context,0);
       // Log.e("cont",""+context);//
        this.context=context;
        songList=new ArrayList<>();
    }

    public static MusicListAdapter getInstance(Context context){
        if(mInstance==null){
            mInstance= new MusicListAdapter(context);
        }
        return mInstance;
    }

    public void setSongList(ArrayList<YoutubeLink> list){
        songList=list;
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int pos,View view,ViewGroup group){
        View tempV= view;
       ;
        if(tempV==null){
            Log.e("MA",""+context);
            tempV= LayoutInflater.from(context).inflate(R.layout.song_item,null);
        }
        title= (TextView) tempV.findViewById(R.id.songTitle);
        artist = (TextView) tempV.findViewById(R.id.artist);
        dnldBtn= (ImageView) tempV.findViewById(R.id.dnld);

        title.setText(songList.get(pos).title);
        artist.setText("");

            //Log.e("MA","pro value"+songList.get(pos).progress);
            //progressBar.setProgress(songList.get(pos).progress);
           // progressPercentage.setText(songList.get(pos).progress+"");

      //  final DownLoadFile instance= new DownLoadFile(songList.get(pos).url);

        dnldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // instance.execute();

                if (isConnectedToNet()) {
                    songList.remove(pos);
                    notifyDataSetChanged();
                    Toast.makeText(context,"Downloading..........",Toast.LENGTH_LONG).show();
                    //requestIntermediatePage(songList.get(pos).link);
                    DownLoadFile i= new DownLoadFile("",context);
                    i.execute();

                } else
                    Snackbar.make(title, "No Connectivity !!! ", Snackbar.LENGTH_LONG).show();

            }
        });

        return tempV;
    }

    @Override
    public int getCount(){
        return songList.size();
    }

    public void startDownload(Document document){

       // DownLoadFile dnd= new DownLoadFile(getFinalURL(document),context);
        //dnd.execute();
    }

    public boolean isConnectedToNet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileData.isConnected()) {
            return true;
        } else if (wifi.isConnected()) {
            return true;
        }
        return false;
    }

    public String getFinalURL(Document doc2){
        //Elements scriptTags = doc2.getElementsByTag("script");
        String download_link = null;
        String songdownloadLink=null;

        Elements linkss=doc2.select("a[href]");
        for(Element link : linkss)
        {
            Log.e("attr",""+link.text());
            if(link.attr("href").matches("download.php?(.*)"))
            {
                download_link=new String(link.attr("href"));
                if(download_link.charAt(download_link.length()-1)!='3')
                    download_link=download_link.substring(0,download_link.length()-1);
                //	System.out.println(download_link);
                //download_link=download_link.replace("download","middle");
                String prefix=new String("http://www.listentoyoutube.com/");

                download_link=prefix.concat(download_link);
                Log.e("dnlink",""+download_link);
                break;
            }
        }
        System.out.println(download_link);
        //String download_link2="http://www.listentoyoutube.com/middle.php?server=srv44&hash=4pWTcXFon2hnabWr2NmXbLVhnGdra21wm5mXtIWZ26aZoY2nv9LYrK6SzQ%253D%253D&file=SANAM%20RE%20Song%20(VIDEO)%20%7C%20Pulkit%20Samrat%2C%20Yami%20Gautam%2C%20Urvashi%20Rautela%2C%20Divya%20Khosla%20Kumar%20%7C%20T-Series.mp3";

        String server_start="server=";
        String server_end="&hash=";
        String hash_end="%3D%3D&file=";
        int index_start = download_link.indexOf(server_start) + server_start.length();
        int index_end = download_link.indexOf(server_end);
        String server=download_link.substring(index_start, index_end);
        System.out.println("Server : "+server);
        index_start = download_link.indexOf(server_end) + server_end.length();
        index_end = download_link.indexOf(hash_end);
        String hash=download_link.substring(index_start, index_end);
        System.out.println("Hash : "+hash);
        index_start = download_link.indexOf(hash_end) + hash_end.length();
        String file=download_link.substring(index_start);
        System.out.println("File : "+file);
        String final_downloading_link="http://www.listentoyoutube.com/download/";
        final_downloading_link=final_downloading_link.replace("www", server);
        final_downloading_link=final_downloading_link.concat(hash);
        final_downloading_link=final_downloading_link.concat("==/");
        final_downloading_link=final_downloading_link.concat(file);
        System.out.println(final_downloading_link);
        Log.e("final ",""+final_downloading_link);
        return final_downloading_link;
    }

    public void requestIntermediatePage(String watch_url){

        String url="http://www.listentoyoutube.com/process.php?url=https://www.youtube.com"+watch_url;
        //final String term="https://www.youtube.com"+watch_url;

            InterURL ins= new InterURL(url);
            ins.execute();
    }

    public class DownLoadFile extends AsyncTask<String,Integer ,String > {

        private Context context;
        private String songURL;
        public DownLoadFile(){}


        public DownLoadFile(String uri,Context con){
            this.songURL=uri;
            this.context=con;
        }

        @Override
        protected String doInBackground(String... params) {

            //TODO: get the final link

            //end

            Log.e("download","in doinBack");
            int count;

            String songPath="http://dl.101songs.com/files/convert/27175/128/01%20Tere%20Bin%20(Wazir)%20Sonu%20Nigam%20(SongsMp3.Com).mp3";
            //"http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";
            try {
                URL url=new URL(songPath);
                URLConnection connection= url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(20000);
                connection.connect();

                int fileLength=connection.getContentLength();

                File root= Environment.getExternalStorageDirectory();
                File dir=new File(root.getAbsolutePath()+"/musicgenie");
                if(dir.exists()==false){
                    dir.mkdirs();
                }
                //   Log.e("dir:::",""+dir);
                File file=new File(dir,"tere.mp3");

                // download file
                InputStream inputStream= new BufferedInputStream(url.openStream());
                OutputStream outputStream= new FileOutputStream(file);


                byte data[] = new byte[1024];
                long total=0;
                //Log.e("dd",""+inputStream.read());
                while((count=inputStream.read(data))!=-1){
                    total+=count;
                    //     Log.e(">>",""+data);
                    publishProgress((int) total * 100 / fileLength);
                    outputStream.write(data,0,count);
                }
                Log.e("Downloaded",": size:"+fileLength);
                outputStream.flush();
                outputStream.close();
                inputStream.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "done";
        }

        @Override
        protected void onPostExecute(String result){
            Log.e("downloaad:","done !!!");

        }


        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);
            Log.e("done..", "" + values[0] + " %");
        }

        @Override
        protected void onPreExecute(){
            Log.e("download:","downloading.................");
        }

    }


    public class InterURL extends AsyncTask<Void,Void,Void>{

        String Ylink;
        String url=null;
        Document doc= null;
        public InterURL(String url){
            this.url=url;
        }



        @Override
        protected Void doInBackground(Void... params) {


            try {
                Log.e("url",""+url);

                URL url=new URL(this.url);



                final HttpURLConnection httpCon= (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod("GET");
                httpCon.addRequestProperty("Connection", "keep-alive");
                httpCon.addRequestProperty("Cache-Control", "max-age=0");
                httpCon.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpCon.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
                httpCon.addRequestProperty("Accept-Encoding", "UTF-8");
                httpCon.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                //httpCon.addRequestProperty("Cookie", "JSESSIONID=EC0F373FCC023CD3B8B9C1E2E2F7606C; lang=tr; __utma=169322547.1217782332.1386173665.1386173665.1386173665.1; __utmb=169322547.1.10.1386173665; __utmc=169322547; __utmz=169322547.1386173665.1.1.utmcsr=stackoverflow.com|utmccn=(referral)|utmcmd=referral|utmcct=/questions/8616781/how-to-get-a-web-pages-source-code-from-java; __gads=ID=3ab4e50d8713e391:T=1386173664:S=ALNI_Mb8N_wW0xS_wRa68vhR0gTRl8MwFA; scrElm=body");
                HttpURLConnection.setFollowRedirects(true);
                httpCon.setInstanceFollowRedirects(true);
                httpCon.setDoOutput(true);

                // connection.setReadTimeout(10000);
                //connection.setConnectTimeout(20000);
                httpCon.connect();

                Log.e("test", "" + httpCon.getResponseCode());

                BufferedReader buffer=new BufferedReader(new InputStreamReader(httpCon.getInputStream(),"UTF-8"));
                Log.e("test1", "" + httpCon.getURL());

                String p="";
                InputStream te=httpCon.getInputStream();
                Thread.sleep(2000);

                Log.e("test2",""+httpCon.getURL());
                String response="";
                while((p=buffer.readLine())!=null){
                    response+=p;
                    //Log.e("p",""+p.toString());
                }
              doc=Jsoup.parse(response);
                Ylink=getFinalURL(doc);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Log.e("result", "" + result);

        }

//        return Ylinks;
    }

}
