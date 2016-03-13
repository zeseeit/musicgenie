package com.mathapp.saurabhjn76.musicgenie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

import java.util.ArrayList;

/**
 * Created by saurabh on 12/3/16.
 */
public class MusicListAdapter extends ArrayAdapter<Song>{
    Context context;
    ArrayList<Song> songList;
    TextView title,artist;
    ImageView dnldBtn,pause;

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
        songList.add(song);
        notifyDataSetChanged();
    }

    public void setProgress(int value){
       Log.e("data re", "" + value);
       // progressBar.setProgress(value);
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
        title.setText(songList.get(pos).Title);
        artist.setText(songList.get(pos).artist);

      //  final DownLoadFile instance= new DownLoadFile(songList.get(pos).url);

        dnldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // instance.execute();

                if (isConnectedToNet()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", songList.get(pos).url);
                    bundle.putString("title", songList.get(pos).Title);
                    Intent dIntent = new Intent(context, Downloads.class);
                    dIntent.putExtras(bundle);
                    context.startActivity(dIntent);
                    Toast.makeText(context, "Download Started", Toast.LENGTH_LONG).show();
                }

                Snackbar.make(title,"No Connectivity !!! ",Snackbar.LENGTH_LONG).show();

            }
        });

        return tempV;
    }

    @Override
    public int getCount(){
        return songList.size();
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

}
