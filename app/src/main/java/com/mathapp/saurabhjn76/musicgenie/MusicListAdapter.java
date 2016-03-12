package com.mathapp.saurabhjn76.musicgenie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saurabh on 12/3/16.
 */
public class MusicListAdapter extends ArrayAdapter<Song>{
    Context context;
    ArrayList<Song> songList;
    public MusicListAdapter(Context context) {
        super(context,0);
        this.context=context;
        songList=new ArrayList<>();
    }

    public void addSongToList(Song song){
        songList.add(song);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos,View view,ViewGroup group){
        View tempV= view;
        TextView title,artist;
        ImageView dnldBtn;
        if(tempV==null){
            tempV= LayoutInflater.from(context).inflate(R.layout.song_item,null);
        }
        title= (TextView) tempV.findViewById(R.id.songTitle);
        artist = (TextView) tempV.findViewById(R.id.artist);
        dnldBtn= (ImageView) tempV.findViewById(R.id.dnld);
        return tempV;
    }

    @Override
    public int getCount(){
        return songList.size();
    }

}
