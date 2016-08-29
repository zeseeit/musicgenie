package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.models.TrendingSongModel;
import musicgenie.com.musicgenie.models.ViewTypeModel;

/**
 * Created by Ankit on 8/14/2016.
 */
public class SectionedListViewAdapter extends ArrayAdapter<String> {

    private static final int TYPE_SONG = 0;
    private static final int TYPE_SECTION_TITLE =1;
    private static final String TAG = "SectionedListAdapter";
    private static Context context;
    private static SectionedListViewAdapter mInstance;
    private ArrayList<ViewTypeModel> typeViewList;
    private ArrayList<TrendingSongModel> trendingSongList;
    private ArrayList<Song> songs;
    public SectionedListViewAdapter(Context context) {
        super(context,0);
        this.context = context;
        typeViewList = new ArrayList<>();
        songs = new ArrayList<>();
    }

    public static SectionedListViewAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SectionedListViewAdapter(context);
        }
        return mInstance;
    }

    public void setTrendingList(ArrayList<TrendingSongModel> list){
        this.trendingSongList = list;
        assimilate();
        notifyDataSetChanged();
    }

    private void createMaps(){
        ArrayList<Song> _songs = new ArrayList<>();
        HashMap<String,ArrayList<Song>> map = new HashMap<>();
        ArrayList<Song> _temp = new ArrayList<>();
        for(TrendingSongModel song: trendingSongList){
            _temp = map.get(song.type);
            _temp.add(song);
            map.put(song.type,_temp);
        }

        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();

            // get each type and loop through songList and call addItem
        }

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
            String section_format = section.substring(0,1).toUpperCase()+section.substring(1);
            typeViewList.add(new ViewTypeModel(TYPE_SECTION_TITLE,section_format,-1));
            Toast.makeText(getContext(),section,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(getItemViewType(position)==TYPE_SECTION_TITLE){
            //
            log("section");
        }else{
            log("song");
        }

        return view;

    }

    @Override
    public int getItemViewType(int position) {
        return typeViewList.get(position).viewType;
    }

    @Override
    public int getCount() {
        return trendingSongList.size();
    }

    public void log(String msg){
        Log.d(TAG, "log "+msg);
    }

}
