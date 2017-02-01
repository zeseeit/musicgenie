package any.audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.Models.ItemModel;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.helpers.FileNameReformatter;
import any.audio.helpers.RoundedCornerTransformer;
import any.audio.helpers.PlaylistGenerator;

/**
 * Created by Ankit on 1/25/2017.
 */

public class ExploreLeftToRightAdapter extends RecyclerView.Adapter<ExploreLeftToRightAdapter.ExploreItemCardViewHolder>  {

    public ArrayList<ItemModel> itemModels;
    public ExploreActionListener exploreActionListener;

    private static Context context;
    private static ExploreLeftToRightAdapter mInstance;

    public ExploreLeftToRightAdapter(Context context) {
        this.context = context;
        itemModels = new ArrayList<>();
    }

    public static ExploreLeftToRightAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ExploreLeftToRightAdapter(context);
        }
        return mInstance;
    }

    public void setItemList(ArrayList<ItemModel> itemList){

        itemModels = itemList;
        notifyDataSetChanged();
    }

    @Override
    public ExploreLeftToRightAdapter.ExploreItemCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.explore_item,null,false);
        return new ExploreItemCardViewHolder(view , itemModels);

    }

    @Override
    public void onBindViewHolder(final ExploreItemCardViewHolder holder, int position) {



        ItemModel model = itemModels.get(position);

        if(ConnectivityUtils.isConnectedToNet()){

            int widthPx = (int) SharedPrefrenceUtils.getInstance(context).getScreenWidthPx();
            int thumbnailHeight = (int) (0.56*widthPx);

            Picasso.with(context)
                    .load(model.Thumbnail_url)
                    .transform(new RoundedCornerTransformer())
                    .into(holder.thumbnail);
            // .centerCrop()
//            .resize(widthPx,thumbnailHeight)

        }

        Typeface materialFace = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
        holder.duration.setText(model.TrackDuration);
        holder.title.setText(model.Title);
        holder.uploader.setText(model.UploadedBy);
        holder.views.setText(model.UserViews);

        holder.downloadBtn.setTypeface(materialFace);
        holder.popUpBtn.setTypeface(materialFace);
        holder.playBtn.setTypeface(materialFace);

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public static class ExploreItemCardViewHolder extends RecyclerView.ViewHolder{

        TextView playBtn;
        TextView downloadBtn;
        TextView popUpBtn;
        TextView title;
        TextView uploader;
        TextView views;
        TextView duration;
        ImageView thumbnail;
        LinearLayout cardWrapper;
        RelativeLayout thumbnailWrapper;

        ArrayList<ItemModel> itemModels;

        public ExploreItemCardViewHolder(View itemView , final ArrayList<ItemModel> itemModels) {
            super(itemView);

            this.itemModels = itemModels;
            thumbnailWrapper = (RelativeLayout) itemView.findViewById(R.id.explore_card_top_wrapper);
            cardWrapper = (LinearLayout) itemView.findViewById(R.id.cardWrapper);

            int widthPx = (int) SharedPrefrenceUtils.getInstance(context).getScreenWidthPx();
            int thumbnailHeight = (int) (0.56*widthPx);
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams(widthPx,thumbnailHeight);

            thumbnailWrapper.setLayoutParams(thumbnailParams);
            cardWrapper.setLayoutParams(new RelativeLayout.LayoutParams(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT));


            playBtn = (TextView) itemView.findViewById(R.id.play_btn_explore_card);
            downloadBtn = (TextView) itemView.findViewById(R.id.explore_item_download_btn);
            popUpBtn = (TextView) itemView.findViewById(R.id.explore_item_popup_btn);
            title = (TextView) itemView.findViewById(R.id.explore_item_title);
            uploader = (TextView) itemView.findViewById(R.id.explore_item_uploader);
            views = (TextView) itemView.findViewById(R.id.explore_item_views);
            duration = (TextView) itemView.findViewById(R.id.explore_item_duration);
            thumbnail = (ImageView) itemView.findViewById(R.id.explore_item_thumbnail);

            //attach click listeners

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExploreLeftToRightAdapter adapter = ExploreLeftToRightAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    Log.d("ExploreLeftRight"," size: "+itemModels.size());
                    String v_id = itemModels.get(pos).Video_id;
                    String file_name = FileNameReformatter.getInstance(context).getFormattedName(itemModels.get(pos).Title);
                    adapter.requestDownload(v_id, file_name);

                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ExploreLeftToRightAdapter adapter = ExploreLeftToRightAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    Log.d("ResultListAdapter","stream req for index "+pos);
                    String v_id = itemModels.get(pos).Video_id;

                    PlaylistGenerator.getInstance(context).preparePlaylist(v_id);

                    String file_name = itemModels.get(pos).Title;
                    String thumb_uri = itemModels.get(pos).Thumbnail_url;
                    String subTitle = itemModels.get(pos).UploadedBy;
                    StreamSharedPref.getInstance(context).setStreamTitle(file_name);
                    Log.d("StreamHome","v_id "+v_id);
                    Log.d("StreamingHome", " setting thumb uri " + thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamThumbnailUrl(thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamSubTitle(subTitle);
                    adapter.broadcastStreamAction(v_id,file_name);

                }
            });

        }
    }

    private void requestDownload(String v_id, String file_name) {
        if(exploreActionListener!=null){
            exploreActionListener.onDownloadAction(v_id,file_name);
        }
    }

    // set by ExploreTopDownAdapter
    public void setActionListener(ExploreActionListener actionListener){
        this.exploreActionListener = actionListener;
    }

    public interface ExploreActionListener{

        void onPlayAction(String video_id,String title);
        void onDownloadAction(String video_id,String title);
        void onAddToQueue(String video_id,String youtubeId,String title,String uploader);
        void onShowAll(String type);

    }

    private void broadcastStreamAction(String vid,String title){

        Intent intent = new Intent(Constants.ACTIONS.AUDIO_OPTIONS);
        intent.putExtra("actionType",101);
        intent.putExtra("vid", vid);
        intent.putExtra("title",title);
        context.sendBroadcast(intent);

    }

}
