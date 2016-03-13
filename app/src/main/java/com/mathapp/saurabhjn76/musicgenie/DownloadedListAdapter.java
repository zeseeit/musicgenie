package com.mathapp.saurabhjn76.musicgenie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by saurabh on 13/3/16.
 */
public class DownloadedListAdapter extends ArrayAdapter<Song> {

    ArrayList<String> songs;
    static DownloadedListAdapter mInstance;
    Context context;
    TextView title,duration,artist;
    ImageView art;
    ImageButton play;
    public DownloadedListAdapter(Context context,ArrayList<String> list) {
        super(context, 0);
        this.context=context;
        songs=new ArrayList<>();
        songs=list;

    }

    public static DownloadedListAdapter getInstance(Context context,ArrayList<String> list){
        if(mInstance==null){
            mInstance=new DownloadedListAdapter(context,list);
        }
        return mInstance;
    }

    public void addToList(String item){
        songs.add(0,item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int pos,View view,ViewGroup group){
        View temp=view;

        if(temp==null){
            temp= LayoutInflater.from(context).inflate(R.layout.downloaded_items,null);
        }
        art= (ImageView) temp.findViewById(R.id.albumArt);
        title= (TextView) temp.findViewById(R.id.title_dnld_pg);
        duration= (TextView) temp.findViewById(R.id.duration);
        artist= (TextView) temp.findViewById(R.id.songArtist);
        play= (ImageButton) temp.findViewById(R.id.play);
        final android.media.MediaMetadataRetriever mmr= new MediaMetadataRetriever();
        mmr.setDataSource(songs.get(pos));
        byte[] data= mmr.getEmbeddedPicture();
        if(data!=null){
            Bitmap bmp= BitmapFactory.decodeByteArray(data,0,data.length);
            art.setImageBitmap(bmp);
        }
        else{
            art.setImageResource(R.drawable.dwnld_bg);
        }

        title.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        artist.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        duration.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        final MediaPlayer mp = new MediaPlayer();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!mp.isPlaying()){
                   try {
                       mp.setDataSource(songs.get(pos));
                       mp.prepare();
                       mp.start();

                       play.setImageResource(android.R.drawable.ic_media_pause);
                       notifyDataSetInvalidated();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }else
               {
                   mp.pause();
               }

            }
        });

        return temp;
    }

    @Override
    public int getCount(){
        return songs.size();
    }



}
