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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by saurabh on 12/3/16.
 */
public class MusicListAdapter extends ArrayAdapter<Song> implements ProgressUpdataListener{
    Context context;
    ArrayList<Song> songList;
    ArrayList<Integer> progressList;
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

    public void addSongToList(Song song){
        songList.add(0,song);
        notifyDataSetChanged();
    }

    public void setProgress(int value,int pos){
       Log.e("MA > to progressBr", "" + value);
        Log.e("list size",""+songList.size());
        if(getCount()>0)songList.get(pos).progress=value;

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
        progressBar= (ProgressBar) tempV.findViewById(R.id.progressBar);
        progressPercentage= (TextView) tempV.findViewById(R.id.percentageProgress);

        title.setText(songList.get(pos).Title);
        artist.setText(songList.get(pos).artist);

            Log.e("MA","pro value"+songList.get(pos).progress);
            progressBar.setProgress(songList.get(pos).progress);
            progressPercentage.setText(songList.get(pos).progress+"");

      //  final DownLoadFile instance= new DownLoadFile(songList.get(pos).url);

        dnldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // instance.execute();

                if (isConnectedToNet()) {
                    /*Bundle bundle = new Bundle();
                    bundle.putString("url", songList.get(pos).url);
                    bundle.putString("title", songList.get(pos).Title);
                    Intent dIntent = new Intent(context, Downloads.class);
                    dIntent.putExtras(bundle);
                    context.startActivity(dIntent);
                    Toast.makeText(context, "Download Started", Toast.LENGTH_LONG).show();*/
                    startDownload(songList.get(pos).url);
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

    public void startDownload(String url){
        DownLoadFile dnd= new DownLoadFile(url,context);
        dnd.execute();
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


    @Override
    public void update(int value,int pos) {
        setProgress(value,pos);
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
            super.onProgressUpdate(values);

            Log.e("done..", "" + values[0] + " %");
            final MusicListAdapter adapter= MusicListAdapter.getInstance(context);
         //  if(adapter!=null) adapter.setProgress(values[0],0);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if(adapter!=null) adapter.setProgress(values[0],0);
                }
            });
        }

        @Override
        protected void onPreExecute(){
            Log.e("download:","downloading.................");
        }

    }

}
