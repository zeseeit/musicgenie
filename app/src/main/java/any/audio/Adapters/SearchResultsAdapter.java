package any.audio.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import any.audio.Managers.FontManager;
import any.audio.Models.ItemModel;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.helpers.CircularImageTransformer;
import any.audio.helpers.FileNameReformatter;
import any.audio.helpers.PlaylistGenerator;

/**
 * Created by Ankit on 1/27/2017.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchItemCardViewHolder>{

    private ArrayList<ItemModel> itemModels;

    private static Context context;
    private static SearchResultsAdapter mInstance;
    private SearchResultActionListener searchActionListener;

    public SearchResultsAdapter(Context context) {
        this.context = context;
    }

    public static SearchResultsAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SearchResultsAdapter(context);
        }
        return mInstance;
    }

    public void setItemList(ArrayList<ItemModel> itemList){
        this.itemModels = itemList;
        notifyDataSetChanged();
    }

    @Override
    public SearchResultsAdapter.SearchItemCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.search_item_layout,null,false);
        return new SearchResultsAdapter.SearchItemCardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SearchItemCardViewHolder holder, int position) {


        Log.d("SearchAdapter"," binding ");
        ItemModel model = itemModels.get(position);

        if(ConnectivityUtils.isConnectedToNet()){

            Picasso.with(context).load(model.Thumbnail_url).transform(new CircularImageTransformer()).into(holder.thumbnail);

        }

        Typeface materialFace = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
        holder.duration.setText(model.TrackDuration);
        holder.title.setText(model.Title);
        holder.uploader.setText(model.UploadedBy);
        holder.views.setText(model.UserViews);

        holder.addbtn.setTypeface(materialFace);
        holder.downloadBtn.setTypeface(materialFace);
        holder.playBtn.setTypeface(materialFace);

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public static class SearchItemCardViewHolder extends RecyclerView.ViewHolder{

        TextView playBtn;
        TextView downloadBtn;
        TextView title;
        TextView uploader;
        TextView views;
        TextView duration;
        TextView addbtn;
        ImageView thumbnail;

        public SearchItemCardViewHolder(View itemView) {
            super(itemView);

            playBtn = (TextView) itemView.findViewById(R.id.search_item_play_btn);
            downloadBtn = (TextView) itemView.findViewById(R.id.search_item_download_btn);
            title = (TextView) itemView.findViewById(R.id.search_item_title);
            uploader = (TextView) itemView.findViewById(R.id.search_item_artist);
            views = (TextView) itemView.findViewById(R.id.search_item_views);
            duration = (TextView) itemView.findViewById(R.id.search_item_duration);
            thumbnail = (ImageView) itemView.findViewById(R.id.search_item_thumbnail);
            addbtn = (TextView) itemView.findViewById(R.id.addToQueue);

            //attach click listeners

            addbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SearchResultsAdapter adapter = SearchResultsAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    String v_id = adapter.itemModels.get(pos).Video_id;
                    String file_name = FileNameReformatter.getInstance(context).getFormattedName(adapter.itemModels.get(pos).Title);
                    String youtubId = adapter.itemModels.get(pos).Thumbnail_url.substring(26,adapter.itemModels.get(pos).Thumbnail_url.length()-6);
                    String uploader = adapter.itemModels.get(pos).UploadedBy;
                    adapter.addItemToQueue(v_id,youtubId,file_name,uploader);

                }
            });

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SearchResultsAdapter adapter = SearchResultsAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    String v_id = adapter.itemModels.get(pos).Video_id;
                    String file_name = FileNameReformatter.getInstance(context).getFormattedName(adapter.itemModels.get(pos).Title);
                    adapter.requestDownload(v_id, file_name);

                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SearchResultsAdapter adapter = SearchResultsAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    Log.d("ResultListAdapter","stream req for index "+pos);
                    String v_id = adapter.itemModels.get(pos).Video_id;

                    PlaylistGenerator.getInstance(context).preparePlaylist(v_id);

                    String file_name = adapter.itemModels.get(pos).Title;
                    String thumb_uri = adapter.itemModels.get(pos).Thumbnail_url;
                    String subTitle = adapter.itemModels.get(pos).UploadedBy;
                    StreamSharedPref.getInstance(context).setStreamTitle(file_name);
                    Log.d("StreamHome","v_id "+v_id);
                    Log.d("StreamingHome", " setting thumb uri " + thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamThumbnailUrl(thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamSubTitle(subTitle);
                    adapter.requestStream(v_id, file_name);

                }
            });

        }
    }

    private void addItemToQueue(String v_id,String youtubeId ,String title,String uploader) {

        if(searchActionListener!=null){

            searchActionListener.onAddToQueue(v_id,youtubeId,title,uploader);

        }
    }

    private void requestStream(String v_id, String file_name) {
        if(searchActionListener!=null){
            searchActionListener.onPlayAction(v_id,file_name);
        }
    }

    private void requestDownload(String v_id, String file_name) {
        if(searchActionListener!=null){
            searchActionListener.onDownloadAction(v_id,file_name);
        }
    }

    // set by ExploreTopDownAdapter
    public void setActionListener(SearchResultActionListener actionListener){
        this.searchActionListener = actionListener;
    }

    public interface SearchResultActionListener{

        void onPlayAction(String video_id,String title);
        void onDownloadAction(String video_id,String title);
        void onAddToQueue(String video_id,String youtubeId,String title,String uploader);

    }

}
