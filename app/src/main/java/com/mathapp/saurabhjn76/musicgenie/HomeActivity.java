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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity{
    ProgressDialog progressBar;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUSIC GENIE");
        getSupportActionBar().setIcon(android.R.drawable.ic_menu_search);

        // Search("Asdf");

        listView = (ListView) findViewById(R.id.songListView);
        final MusicListAdapter adapter = new MusicListAdapter(this);
        listView.setAdapter(adapter);
        String songPath = "http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";

        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit",0));
        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit",0));
        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit",0));
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
        if(isConnectedToNet())
        refress();
        else
            Snackbar.make(listView,"No Connectivity !!! ",Snackbar.LENGTH_LONG).show();
    }

    public void refress(){
        String searchTerm=((EditText)findViewById(R.id.searchBox)).getText().toString();
        //TODO: take the search term and format it
        //show progressDialoge till it loads list
        // after it gets list add all to adapter


    }

    private void Search(final String term){

        String testPath="http://www.listentoyoutube.com/download.php?server=srv44&hash=4pWTcXFon2hnabWr2NmXb" +
                "LVhnGdra21wm5mXtIWZ26aZoY2nv9LYrK6SzQ%3D%3D&file=SANAM%20RE%20Song%20%28VIDEO%29%20%7C%20Pulkit%20Samrat%2C%20Yami%20Gautam%2C%20Urvashi%20Rautela%2C%20Divya%20Khosla%20Kumar%20%7C%20T-Series.mp3";

        StringRequest request= new StringRequest(Request.Method.GET,AppConfig.URL_YOUTUBE_LINK_FETCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(HomeActivity.this,""+s,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                    Log.e("Home",""+volleyError);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("search_query",term);
               return params;
            }

        };

        RequestManager.getInstance().addToRequestQueue(request, "ytd_req", this);

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
            MusicListAdapter adapter= MusicListAdapter.getInstance(context);
            adapter.setProgress(values[0],0);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute(){
            Log.e("download:","downloading.................");
        }

    }


}
