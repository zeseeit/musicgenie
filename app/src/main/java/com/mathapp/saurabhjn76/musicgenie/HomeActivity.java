package com.mathapp.saurabhjn76.musicgenie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.jsoup.nodes.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class HomeActivity extends AppCompatActivity{
    ProgressDialog progressBar;
    ListView listView;
    MusicListAdapter adapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUSIC GENIE");
        getSupportActionBar().setIcon(android.R.drawable.ic_menu_search);
        progressDialog=new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Searching.....");
        // Search("Asdf");

        listView = (ListView) findViewById(R.id.songListView);
        adapter = new MusicListAdapter(this);
        listView.setAdapter(adapter);
        //String songPath = "http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Downloads.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(View view) {
        if(isConnectedToNet()){
            progressDialog.show();
            refress();
        }
        else
            Snackbar.make(listView,"No Connectivity !!! ",Snackbar.LENGTH_LONG).show();
    }

    public void refress(){
        String searchTerm=((EditText)findViewById(R.id.searchBox)).getText().toString();
        Search(searchTerm.replace(' ','+'));
        //TODO: take the search term and format it
        //show progressDialoge till it loads list
        // after it gets list add all to adapter


    }

    public void setListItems(ArrayList<YoutubeLink> list){
        adapter.setSongList(list);
    }
    private void Search(final String term){

        SongURL ins= new SongURL(AppConfig.URL_YOUTUBE_LINK_FETCH+"?search_query="+term);
            ins.execute();
    }


    public boolean isConnectedToNet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileData.isConnected()) {
            return true;
        } else if (wifi.isConnected()) {
            return true;
        }
        return false;
    }

    public class DownLoadFile extends AsyncTask<String,Integer ,String > {

        private Context context;
        private String songURL;
        public DownLoadFile(){}

        public DownLoadFile(String uri){
            this.songURL=uri;
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("download","in doinBack");
            int count;

            //songPath="http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";
            try {
                URL url=new URL(songURL);
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
                File file=new File(dir,"song.mp3");

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
            Log.e("done..", "" + values[0] + " %");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute(){
            Log.e("download:","downloading.................");
        }

    }


    public class SongURL extends AsyncTask<Void,Void,Void>{

        ArrayList<YoutubeLink> Ylinks;
        String url=null;
        Document doc= null;
       public SongURL(String url){
           this.url=url;
       }



        @Override
        protected Void doInBackground(Void... params) {


            try {
                Log.e("url",""+url);

                URL url=new URL(this.url);



                HttpURLConnection httpCon= (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod("GET");
                httpCon.addRequestProperty("Connection", "keep-alive");
                httpCon.addRequestProperty("Cache-Control", "max-age=0");
                httpCon.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpCon.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
                httpCon.addRequestProperty("Accept-Encoding", "UTF-8");
                httpCon.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                //httpCon.addRequestProperty("Cookie", "JSESSIONID=EC0F373FCC023CD3B8B9C1E2E2F7606C; lang=tr; __utma=169322547.1217782332.1386173665.1386173665.1386173665.1; __utmb=169322547.1.10.1386173665; __utmc=169322547; __utmz=169322547.1386173665.1.1.utmcsr=stackoverflow.com|utmccn=(referral)|utmcmd=referral|utmcct=/questions/8616781/how-to-get-a-web-pages-source-code-from-java; __gads=ID=3ab4e50d8713e391:T=1386173664:S=ALNI_Mb8N_wW0xS_wRa68vhR0gTRl8MwFA; scrElm=body");
                HttpURLConnection.setFollowRedirects(false);
                httpCon.setInstanceFollowRedirects(false);
                httpCon.setDoOutput(true);

               // connection.setReadTimeout(10000);
                //connection.setConnectTimeout(20000);
                httpCon.connect();
                BufferedReader buffer=new BufferedReader(new InputStreamReader(httpCon.getInputStream(),"UTF-8"));

                String p="";
                String response="";
                while((p=buffer.readLine())!=null){
                    response+=p;
                    //Log.e("p",""+p.toString());
                }
                //Log.e("alt",""+connection.);

                doc = Jsoup.parse(response);
                Elements links = doc.select("a[href]");
              //  Log.e("links", doc.html().toString() + "");
              //  Log.e("getSONGURL","Pick One of the song to download");
                int counter=0;
                 Ylinks= new ArrayList<>();
                for (Element link : links) {
                   // Log.e("songURL",""+link);
                    if(link.attr("href").matches("/watch/?(.*)") && !(link.text().matches("[0-9]*:[0-9]*")))
                    {
                        Ylinks.add(new YoutubeLink(link.text(), link.attr("href")));
                        Log.e("link>",""+link.text()+" "+link.attr("href"));
                        counter++;
                    }
                }
              //  setListItems(Ylinks);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            setListItems(Ylinks);
            progressDialog.hide();
            progressDialog.dismiss();
            Log.e("result",""+result);

        }

//        return Ylinks;
    }
}
