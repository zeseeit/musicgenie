package com.mathapp.saurabhjn76.musicgenie;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Downloads extends AppCompatActivity{

    ProgressBar progressBar;
    TextView percentage;
    ImageButton pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_downloads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        percentage= (TextView) findViewById(R.id.percentageProgress);
        toolbar.setTitle("Downloads");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle= getIntent().getExtras();

        final DownLoadFile ins= new DownLoadFile(bundle.getString("url"));
        ins.execute();
        pause= (ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ins.cancel(true);
                Toast.makeText(Downloads.this,"Download Canceled",Toast.LENGTH_LONG).show();
                pause.setImageResource(android.R.drawable.ic_media_play);
            }
        });


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
            Log.e("done..",""+values[0]+" %");
            progressBar.setProgress(values[0]);
            percentage.setText(values[0]+" %");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute(){
            Log.e("download:","downloading.................");
        }

    }
}
