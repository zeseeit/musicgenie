package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.models.TrendingSongModel;
import musicgenie.com.musicgenie.models.ViewTypeModel;
import musicgenie.com.musicgenie.utilities.FontManager;

/**
 * Created by Ankit on 8/21/2016.
 */
public class TrendingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_SONG = 0;
    private static final int TYPE_SECTION_TITLE =1;
    private static final String TAG = "TrendingRecylerAdapter";
    private ArrayList<ViewTypeModel> typeViewList;
    private ArrayList<TrendingSongModel> trendingSongList;
    private ArrayList<Song> songs;
    private int orientation;

    private static Context context;
    private static TrendingRecyclerViewAdapter mInstance;

    public TrendingRecyclerViewAdapter(Context context) {
        this.context = context;
        typeViewList = new ArrayList<>();
        songs = new ArrayList<>();
    }

    public static TrendingRecyclerViewAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TrendingRecyclerViewAdapter(context);
        }
        return mInstance;
    }

    public void addSongs(ArrayList<Song> list , String type){

        // add section header and loops through list and call addItem on each item
        // adding section
        addItem(null, type);
        for(Song s: list){
            //  adding each item of type
            addItem(s, "");
        }

        notifyDataSetChanged();
    }

    public void addItem(Song song , String section){   //     create view list
        // if section is "" then it is song
        // else it is sectionType
        if(section.equals("")){ // means it is song
            log("add song:");
            int index = songs.size();
            songs.add(song);
            typeViewList.add(new ViewTypeModel(TYPE_SONG,"",index));
        }
        else{ //means it is Section Title
            log("section:");
            typeViewList.add(new ViewTypeModel(TYPE_SECTION_TITLE,section,-1));
        }

        log("now typeViewList: \n\n");
        for(ViewTypeModel t: typeViewList){
            log(t.viewType + " \t " + t.sectionTitle + " \t " + t.index);
        }
        log("===================");
        for(Song s: songs){
            log("song "+s.Title+ " ");
        }

    }

    private void log(String s) {
        Log.d(TAG, "log>>"+s);
    }


    public void setOrientation(int orientation){
        this.orientation = orientation;
        Log.d(TAG, "setOrientation " + orientation);
    }

    private boolean isLandscape(int orientation) {
        return orientation%2==0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        log("VH "+" rec = "+viewType);
        if(viewType==TYPE_SECTION_TITLE){
            view = LayoutInflater.from(context).inflate(R.layout.section_header_layout, parent, false);
            return new SectionTitleViewHolder(view);
        }else{
            //TODO: handle mobile version
            if (!isLandscape(orientation)) {
                view = LayoutInflater.from(context).inflate(R.layout.song_card_land_sw600, parent, false);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.song_card_sw600, parent, false);
            }
            return new SongViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Typeface fontawesome = FontManager.getInstance(context).getTypeFace(FontManager.FONT_AWESOME);
        Typeface ralewayTfRegular = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        Typeface ralewayTfBold = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_BOLD);

        if(holder instanceof SongViewHolder){

            // bind section data
            log("binding song " + position);
            Song song = songs.get(typeViewList.get(position).index);
            ((SongViewHolder) holder).title.setText(song.Title);
            ((SongViewHolder) holder).uploader.setText(song.UploadedBy);
            ((SongViewHolder) holder).views.setText(song.UserViews);
            ((SongViewHolder) holder).popMenuBtn.setText("\uF142");

            // setting typeface to fonta
            ((SongViewHolder) holder).downloadBtn.setTypeface(fontawesome);
            ((SongViewHolder) holder).uploader_icon.setTypeface(fontawesome);
            ((SongViewHolder) holder).views_icon.setTypeface(fontawesome);
            ((SongViewHolder) holder).popMenuBtn.setTypeface(fontawesome);
            //setting typeface to raleway
            ((SongViewHolder) holder).title.setTypeface(ralewayTfBold);
            ((SongViewHolder) holder).content_length.setTypeface(ralewayTfRegular);
            ((SongViewHolder) holder).uploader.setTypeface(ralewayTfRegular);
            ((SongViewHolder) holder).views.setTypeface(ralewayTfRegular);

        }else{
            // binnd song data
            log("binding header " + position);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            ((SectionTitleViewHolder) holder).sectionTitle.setText(typeViewList.get(position).sectionTitle);
            ((SectionTitleViewHolder) holder).sectionTitle.setTypeface(ralewayTfRegular);
        }

    }

    @Override
    public int getItemCount() {
        return typeViewList.size();
    }

    @Override
    public int getItemViewType(int position) {
        log("view type at"+position+" = "+typeViewList.get(position).viewType);
       return typeViewList.get(position).viewType;
    }

    public static class SectionTitleViewHolder extends RecyclerView.ViewHolder{

        TextView sectionTitle;
        public SectionTitleViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder{

        TextView downloadBtn;
        TextView uploader_icon;
        TextView views_icon;
        TextView popMenuBtn;
        TextView content_length;
        TextView uploader;
        TextView title;
        TextView views;

        public SongViewHolder(View itemView) {
            super(itemView);
            Typeface fontawesome = FontManager.getInstance(context).getTypeFace(FontManager.FONT_AWESOME);
            Typeface ralewayTfRegular = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
            Typeface ralewayTfBold = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_BOLD);
            // material
            downloadBtn = (TextView) itemView.findViewById(R.id.download_btn_card);
            uploader_icon = (TextView) itemView.findViewById(R.id.uploader_icon);
            views_icon = (TextView) itemView.findViewById(R.id.views_icon);
            popMenuBtn = (TextView) itemView.findViewById(R.id.popUpMenuIcon);
            downloadBtn.setTypeface(fontawesome);
            uploader_icon.setTypeface(fontawesome);
            views_icon.setTypeface(fontawesome);
            popMenuBtn.setTypeface(fontawesome);
            // regular raleway
            content_length = (TextView) itemView.findViewById(R.id.song_time_length);
            uploader = (TextView) itemView.findViewById(R.id.uploader_name);
            views = (TextView) itemView.findViewById(R.id.views_text);
            title = (TextView) itemView.findViewById(R.id.song_title);
            title.setTypeface(ralewayTfBold);
            content_length.setTypeface(ralewayTfRegular);
            uploader.setTypeface(ralewayTfRegular);
            views.setTypeface(ralewayTfRegular);
            // plain text
        }
    }
}
