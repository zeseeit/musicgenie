package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.models.TrendingSongModel;
import musicgenie.com.musicgenie.models.ViewTypeModel;
import musicgenie.com.musicgenie.utilities.FontManager;

/**
 * Created by Ankit on 8/18/2016.
 */
public class TrendingItemsGridAdapter extends BaseAdapter {


    private static final int TYPE_SONG = 0;
    private static final int TYPE_SECTION_TITLE =1;
    private ArrayList<ViewTypeModel> typeViewList;
    private ArrayList<TrendingSongModel> trendingSongList;
    private ArrayList<Song> songs;
    // part  2
    private static final String TAG = "TRENDING_ADAPTER";
    private static Context context;
    private static TrendingItemsGridAdapter mInstance;
    private ArrayList<TrendingSongModel> list;
    private TextView downloadBtn;
    private TextView uploader_icon;
    private TextView views_icon;
    private TextView content_length;
    private TextView uploader;
    private TextView views;
    private TextView popMenuBtn;
    private TextView title;
    private int orientation;
    public TrendingItemsGridAdapter(Context context) {
        this.context = context;
    }

    public static TrendingItemsGridAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TrendingItemsGridAdapter(context);
        }
        return mInstance;
    }

    public void setTrendingItems(ArrayList<TrendingSongModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void setTrendingList(ArrayList<TrendingSongModel> list){
        this.trendingSongList = list;
        assimilate();
        notifyDataSetChanged();
    }


    private void assimilate(){
        // loops through each trendinglist and calls addItem depending on type of songs
        for(TrendingSongModel song: trendingSongList) {
            addItem((Song)song,song.type);
        }

    }

    public void addItem(Song song , String section){
        // if section is "" then it is song
        // else it is sectionType
        if(section.equals("")){ // means it is song
            int index = songs.size();
            songs.add(song);
            typeViewList.add(new ViewTypeModel(TYPE_SONG,"",index));
        }
        else{ //means it is Section Title
            typeViewList.add(new ViewTypeModel(TYPE_SECTION_TITLE,section,-1));
        }

    }




    public void setOrientation(int orientation){
        this.orientation = orientation;
        Log.d(TAG, "setOrientation " + orientation);
    }
    
    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View tempView = view;

        if(tempView==null){
            // handle type seperation


            if(!isLandscape(orientation)){
                tempView = LayoutInflater.from(context).inflate(R.layout.song_card_land_sw600, viewGroup, false);
            }else {
                tempView = LayoutInflater.from(context).inflate(R.layout.song_card_sw600, viewGroup, false);
            }
        }
        init(tempView);
        bind(position);

        return tempView;
    }

    private boolean isLandscape(int orientation) {
        return orientation%2==0;
    }

    private void bind(int pos) {
        Song song =  list.get(pos);
        title.setText(song.Title);
        uploader.setText(song.UploadedBy);
        views.setText(song.UserViews);
        popMenuBtn.setText("\uF142");
    }


    private void init(View v) {

        Typeface fontawesome = FontManager.getInstance(context).getTypeFace(FontManager.FONT_AWESOME);
        Typeface ralewayTfRegular = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        Typeface ralewayTfBold = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_BOLD);
        // material
        downloadBtn = (TextView) v.findViewById(R.id.download_btn_card);
        uploader_icon = (TextView) v.findViewById(R.id.uploader_icon);
        views_icon = (TextView) v.findViewById(R.id.views_icon);
        popMenuBtn = (TextView) v.findViewById(R.id.popUpMenuIcon);
        downloadBtn.setTypeface(fontawesome);
        uploader_icon.setTypeface(fontawesome);
        views_icon.setTypeface(fontawesome);
        popMenuBtn.setTypeface(fontawesome);
        // regular raleway
        content_length = (TextView) v.findViewById(R.id.song_time_length);
        uploader = (TextView) v.findViewById(R.id.uploader_name);
        views = (TextView) v.findViewById(R.id.views_text);
        title = (TextView) v.findViewById(R.id.song_title);
        title.setTypeface(ralewayTfBold);
        content_length.setTypeface(ralewayTfRegular);
        uploader.setTypeface(ralewayTfRegular);
        views.setTypeface(ralewayTfRegular);
        // plain text


    }


    @Override
    public int getItemViewType(int position) {
        return typeViewList.get(position).viewType;
    }

    @Override
    public int getCount() {
        return trendingSongList.size();
    }


}
