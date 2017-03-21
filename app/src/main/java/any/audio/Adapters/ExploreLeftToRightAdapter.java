package any.audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.Models.ItemModel;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.helpers.FileNameReformatter;
import any.audio.helpers.PlaylistGenerator;

import static any.audio.Activity.AnyAudioActivity.anyAudioActivityInstance;

/**
 * Created by Ankit on 1/25/2017.
 */

public class ExploreLeftToRightAdapter extends RecyclerView.Adapter<ExploreLeftToRightAdapter.ExploreItemCardViewHolder> {

    public ArrayList<ItemModel> itemModels;
    private static Context context;
    private static ExploreLeftToRightAdapter mInstance;
    private Typeface materialFace;
    private int COUNT = 0;

    public ExploreLeftToRightAdapter(Context context) {
        this.context = context;
        itemModels = new ArrayList<>();
        materialFace = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
    }

    public static ExploreLeftToRightAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ExploreLeftToRightAdapter(context);
        }
        return mInstance;
    }

    public void setItemList(ArrayList<ItemModel> itemList) {

        itemModels = itemList;
        COUNT = itemList.size();
        notifyDataSetChanged();
    }

    @Override
    public ExploreLeftToRightAdapter.ExploreItemCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.light_explore_card, null, false);
        return new ExploreItemCardViewHolder(view, itemModels);

    }

    @Override
    public void onBindViewHolder(final ExploreItemCardViewHolder holder, int position) {

        ItemModel model = itemModels.get(holder.getAdapterPosition());
        holder.thumbnail.setImageURI(itemModels.get(position).Thumbnail_url);
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
        //lt(">>>>getCount");
        return COUNT;
    }

    public static class ExploreItemCardViewHolder extends RecyclerView.ViewHolder {
        TextView playBtn;
        TextView downloadBtn;
        TextView popUpBtn;
        TextView title;
        TextView uploader;
        TextView views;
        TextView duration;
        SimpleDraweeView thumbnail;
        LinearLayout cardWrapper;
        RelativeLayout thumbnailWrapper;

        ArrayList<ItemModel> itemModels;

        public ExploreItemCardViewHolder(View itemView, final ArrayList<ItemModel> itemModels) {
            super(itemView);

            this.itemModels = itemModels;
            thumbnailWrapper = (RelativeLayout) itemView.findViewById(R.id.explore_card_top_wrapper);
            cardWrapper = (LinearLayout) itemView.findViewById(R.id.cardWrapper);

            int widthPx = (int) SharedPrefrenceUtils.getInstance(context).getScreenWidthPx();
            int thumbnailHeight = (int) (0.5 * widthPx);
            int thumbnail_shift = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams(widthPx-thumbnail_shift, thumbnailHeight);

            thumbnailWrapper.setLayoutParams(thumbnailParams);
            cardWrapper.setLayoutParams(new RelativeLayout.LayoutParams(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT));


            playBtn = (TextView) itemView.findViewById(R.id.play_btn_explore_card);
            downloadBtn = (TextView) itemView.findViewById(R.id.explore_item_download_btn);
            popUpBtn = (TextView) itemView.findViewById(R.id.explore_item_popup_btn);
            title = (TextView) itemView.findViewById(R.id.explore_item_title);
            uploader = (TextView) itemView.findViewById(R.id.explore_item_uploader);
            views = (TextView) itemView.findViewById(R.id.explore_item_views);
            duration = (TextView) itemView.findViewById(R.id.explore_item_duration);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.explore_item_thumbnail);
            //attach click listeners
            thumbnail.setMaxHeight(thumbnailHeight);
            thumbnail.setMinimumHeight(thumbnailHeight);

            popUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ExploreLeftToRightAdapter adapter = ExploreLeftToRightAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    final String v_id = itemModels.get(pos).Video_id;
                    final String t_url = itemModels.get(pos).Thumbnail_url.substring(26, itemModels.get(pos).Thumbnail_url.length() - Constants.THUMBNAIL_VERSION_MEDIUM.length());
                    final String artist = itemModels.get(pos).UploadedBy;
                    final String file_name = FileNameReformatter.getInstance(context).getFormattedName(itemModels.get(pos).Title);

                    adapter.requestPopUp(view, v_id, t_url, file_name, artist);

                }
            });


            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExploreLeftToRightAdapter adapter = ExploreLeftToRightAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    String v_id = itemModels.get(pos).Video_id;
                    String t_url = itemModels.get(pos).Thumbnail_url;
                    String artist = itemModels.get(pos).UploadedBy;
                    String file_name = FileNameReformatter.getInstance(context).getFormattedName(itemModels.get(pos).Title);
                //    Log.d("ExploreCard", " >  dnd tapped");
                    adapter.requestDownload(v_id, file_name, t_url, artist);

                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ExploreLeftToRightAdapter adapter = ExploreLeftToRightAdapter.getInstance(context);
                    int pos = getAdapterPosition();
              //      Log.d("ResultListAdapter", "stream req for index " + pos);
                    String v_id = itemModels.get(pos).Video_id;

                    PlaylistGenerator.getInstance(context).preparePlaylist(v_id);

                    String file_name = itemModels.get(pos).Title;
                    String thumb_uri = itemModels.get(pos).Thumbnail_url;
                    String subTitle = itemModels.get(pos).UploadedBy;
                    StreamSharedPref.getInstance(context).setStreamTitle(file_name);
          //          Log.d("StreamHome", "v_id " + v_id);
            //        Log.d("StreamingHome", " setting thumb uri " + thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamThumbnailUrl(thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamSubTitle(subTitle);
                    //todo: remove streamshared pref infos
                    SharedPrefrenceUtils.getInstance(context).setCurrentItemTitle(file_name);
                    SharedPrefrenceUtils.getInstance(context).setCurrentItemThumbnailUrl(thumb_uri);
                    SharedPrefrenceUtils.getInstance(context).setCurrentItemArtist(subTitle);
                    SharedPrefrenceUtils.getInstance(context).setCurrentItemStreamUrl(itemModels.get(pos).Video_id);
                    adapter.broadcastStreamAction(v_id, file_name);

                }
            });

            cardWrapper.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                    return false;
                }
            });

        }
    }

    private void requestPopUp(View view, String video_id, String youtubeId, String title, String uploader) {
        anyAudioActivityInstance.onPopUpMenuTap(view, video_id, youtubeId, title, uploader);
    }

    private void requestDownload(String v_id, String file_name, String t_url, String artist) {
        anyAudioActivityInstance.onDownloadAction(v_id, file_name, t_url, artist);
    }

    private void broadcastStreamAction(String vid, String title) {

        Intent intent = new Intent(Constants.ACTIONS.AUDIO_OPTIONS);
        intent.putExtra("actionType", 101);
        intent.putExtra("vid", vid);
        intent.putExtra("title", title);
        context.sendBroadcast(intent);

    }

    public static void lt(String point) {

        Log.i("PerformanceTest", "At " + point + " on " + System.currentTimeMillis());

    }

}
