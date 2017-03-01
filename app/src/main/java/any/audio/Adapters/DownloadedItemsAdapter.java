package any.audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.Models.DownloadedItemModel;
import any.audio.R;
import any.audio.helpers.AnyAudioMediaPlayer;
import any.audio.helpers.MetaDataHelper;
import de.hdodenhof.circleimageview.CircleImageView;


public class DownloadedItemsAdapter extends ArrayAdapter<String> {

    private static final String TAG = "DownloadedSongsAdapter";
    private static Context context;
    private MetaDataHelper metaDataHelper;
    private static DownloadedItemsAdapter mInstance;
    //Views
    public static boolean isPlaying = false;
    private String playBtnString = "\uE039";
    private String pauseBtnString = "\uE036";
    private TextView playBtnTv;
    private TextView mTitle;
    private ImageView mAlbumArt;
    private DownloadedItemDeleteListener deleteListener;
    private ArrayList<DownloadedItemModel> downloadedListItems;
    private Typeface tfIcon;

    public DownloadedItemsAdapter(Context context) {

        super(context, 0);
        DownloadedItemsAdapter.context = context;
        tfIcon = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
        downloadedListItems = new ArrayList<>();
    }

    public static DownloadedItemsAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadedItemsAdapter(context);
        }
        return mInstance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DownloadedItemViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new DownloadedItemViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.downloaded_items_layout, parent, false);
            viewHolder.thumbnail = (CircleImageView) convertView.findViewById(R.id.downloaded_item_thumbnail);
            viewHolder.contentLength = (TextView) convertView.findViewById(R.id.downloaded_item_duration);
            viewHolder.title = (TextView) convertView.findViewById(R.id.downloaded_item_title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.downloaded_item_artist);
            viewHolder.deleteBtn = (TextView) convertView.findViewById(R.id.deleteDownloadedItem);
            viewHolder.playBtnTv = (TextView) convertView.findViewById(R.id.playDownloadedItem);
            viewHolder.deleteBtn.setTypeface(tfIcon);
            viewHolder.playBtnTv.setTypeface(tfIcon);
            viewHolder.infoWrapper = (RelativeLayout) convertView.findViewById(R.id.downloaded_item_info_wrapper);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DownloadedItemViewHolder) convertView.getTag();
        }

        bind(viewHolder, position);
        return convertView;
    }

    private class DownloadedItemViewHolder {

        public CircleImageView thumbnail;
        public TextView title;
        public TextView playBtnTv;
        public TextView deleteBtn;
        public TextView contentLength;
        public TextView artist;
        public RelativeLayout infoWrapper;

    }

    private void bind(final DownloadedItemViewHolder viewHolder, final int position) {

        String title = downloadedListItems.get(position).title;
        viewHolder.title.setText(title.substring(title.lastIndexOf('/') + 1));
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(title);
        String durationText = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        viewHolder.contentLength.setText(getTimeFromMillisecond(Integer.valueOf(durationText)));
        viewHolder.artist.setText(artist);

        //attach listeners

        viewHolder.playBtnTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                return false;
            }
        });


        viewHolder.deleteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                return false;
            }
        });

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteListener != null) {
                    deleteListener.onDelete(position);
                }
            }
        });

        viewHolder.playBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((TextView) view).getText().equals(playBtnString)) {
                    // current item is not playing

                    if (isPlaying) {
                        // some item is playing
                        // so stop it
                        AnyAudioMediaPlayer.getInstance(context).stopPlaying();
                    }
                    // start the current item

                    AnyAudioMediaPlayer.getInstance(context)
                            .setViewCallback((TextView) view)
                            .setAudioPath(downloadedListItems.get(position).title)
                            .startPlay();

                    ((TextView) view).setText(pauseBtnString);

                } else {
                    // current item is playing
                    // so pause it[item is doubly tapped]
                    ((TextView) view).setText(playBtnString);
                    AnyAudioMediaPlayer.getInstance(context).stopPlaying();

                }
            }
        });

    }


    @Override
    public int getCount() {
        return downloadedListItems.size();
    }


    private String getTimeFromMillisecond(int millis) {

        String hr;
        String min;
        String sec;
        String time;
        int i_hr = (millis / 1000) / 3600;
        int i_min = (millis / 1000) / 60;
        int i_sec = (millis / 1000) % 60;

        if (i_hr == 0) {
            min = (String.valueOf(i_min).length() < 2) ? "0" + i_min : String.valueOf(i_min);
            sec = (String.valueOf(i_sec).length() < 2) ? "0" + i_sec : String.valueOf(i_sec);
            time = min + " : " + sec;
        } else {
            hr = (String.valueOf(i_hr).length() < 2) ? "0" + i_hr : String.valueOf(i_hr);
            min = (String.valueOf(i_min).length() < 2) ? "0" + i_min : String.valueOf(i_min);
            sec = (String.valueOf(i_sec).length() < 2) ? "0" + i_sec : String.valueOf(i_sec);
            time = hr + " : " + min + " : " + sec;
        }

        // Log.d("StreamingHome"," time returned "+time);

        return time;


    }


    public void setDownloadingList(ArrayList<DownloadedItemModel> downloadedItemModelArrayList) {
        this.downloadedListItems = downloadedItemModelArrayList;
        notifyDataSetChanged();
    }

    public void setOnDownloadCancelListener(DownloadedItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public interface DownloadedItemDeleteListener {
        void onDelete(int index);
    }

}
