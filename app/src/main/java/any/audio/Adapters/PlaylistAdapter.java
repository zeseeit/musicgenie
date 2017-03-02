package any.audio.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.Models.PlaylistItem;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.QueueManager;
import any.audio.helpers.ToastMaker;
import de.hdodenhof.circleimageview.CircleImageView;

import static any.audio.Activity.AnyAudioActivity.anyAudioActivityInstance;

/**
 * Created by Ankit on 1/31/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistItemHolder> {

    private static Context context;
    private static PlaylistAdapter mInstance;
    public ArrayList<PlaylistItem> playlistItems;
    private Typeface typeface;
    static SharedPrefrenceUtils utils;
    private PlaylistItemListener playlistItemListener;

    public PlaylistAdapter(Context context) {
        this.context = context;
        playlistItems = new ArrayList<>();
        utils = SharedPrefrenceUtils.getInstance(context);
        typeface = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
    }

    public void setPlaylistItem(ArrayList<PlaylistItem> items) {
        this.playlistItems = items;
        notifyDataSetChanged();
    }

    public static PlaylistAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlaylistAdapter(context);
        }
        return mInstance;
    }

    @Override
    public PlaylistItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View playlistView = LayoutInflater.from(context).inflate(R.layout.playlist_item, null, false);
        return new PlaylistItemHolder(playlistView);

    }

    @Override
    public void onBindViewHolder(PlaylistItemHolder holder, int position) {

        PlaylistItem currentItem = playlistItems.get(position);

        holder.title.setText(currentItem.getTitle());
        holder.artist.setText(currentItem.getUploader());

        if (utils.getAutoPlayMode()) {
            holder.popUp.setVisibility(View.VISIBLE);
            holder.removeBtn.setVisibility(View.INVISIBLE);
        } else {
            holder.popUp.setVisibility(View.INVISIBLE);
            holder.removeBtn.setVisibility(View.VISIBLE);
            holder.removeBtn.setTypeface(typeface);
        }

        Picasso.with(context).load(getImageUrl(currentItem.getYoutubeId())).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    private String getImageUrl(String vid) {
        return "https://i.ytimg.com/vi/" + vid + Constants.THUMBNAIL_VERSION_MEDIUM;  // additional query params => ?custom=true&w=240&h=256
    }

    public void popItem() {
        playlistItems.remove(0);
        notifyItemRemoved(0);
    }

    public void appendItem(PlaylistItem item) {
        playlistItems.add(item);
        notifyItemInserted(playlistItems.size() - 1);
    }

    public static class PlaylistItemHolder extends RecyclerView.ViewHolder {

        CircleImageView thumbnail;
        TextView title;
        TextView artist;
        TextView removeBtn;
        TextView popUp;
        RelativeLayout infoWrapper;

        public PlaylistItemHolder(View itemView) {
            super(itemView);

            thumbnail = (CircleImageView) itemView.findViewById(R.id.playlist_item_thumbnail);
            title = (TextView) itemView.findViewById(R.id.playlist_item_title);
            artist = (TextView) itemView.findViewById(R.id.playlist_item_artist);
            removeBtn = (TextView) itemView.findViewById(R.id.playlist_item_cancel_btn_text);
            popUp = (TextView) itemView.findViewById(R.id.playlist_item_popup_btn);
            infoWrapper = (RelativeLayout) itemView.findViewById(R.id.playlist_item_info_wrapper);
            popUp.setTypeface(FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL));


            infoWrapper.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                    return false;
                }
            });

            infoWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PlaylistAdapter adapter = PlaylistAdapter.getInstance(context);
                    adapter.streamItem(getAdapterPosition());

                }
            });


            popUp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                    return false;
                }
            });

            popUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlaylistAdapter adapter = PlaylistAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    // show pop for download + add To Queue
                    anyAudioActivityInstance.showAutoPlayItemPopUp(view,adapter.playlistItems.get(pos).videoId,adapter.playlistItems.get(pos).youtubeId,adapter.playlistItems.get(pos).title,adapter.playlistItems.get(pos).getUploader());

                }
            });

            removeBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                    return false;
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!utils.getAutoPlayMode()) {   // auto-playlist are not cancellable
                        // queue items are visible => items are cancellable

                        PlaylistAdapter adapter = PlaylistAdapter.getInstance(context);
                        int pos = getAdapterPosition();
                        ToastMaker.getInstance(context).toast(adapter.playlistItems.get(pos).getTitle()+" Removed");
                        QueueManager.getInstance(context).removeQueueItem(adapter.playlistItems.get(pos).getVideoId());
                        adapter.playlistItems.remove(pos);
                        adapter.notifyItemRemoved(pos);

                    }

                }
            });
        }

    }

    private void streamItem(int adapterPosition) {

        utils.setCurrentQueueIndex(adapterPosition);
        if (playlistItemListener != null) {
            playlistItemListener.onPlaylistItemTapped(playlistItems.get(adapterPosition));
        }

    }

    public void setPlaylistItemListener(PlaylistItemListener listener) {
        this.playlistItemListener = listener;
    }

    public interface PlaylistItemListener {

        void onPlaylistItemTapped(PlaylistItem item);

    }

}
