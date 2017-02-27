package any.audio.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Interfaces.DownloadCancelListener;
import any.audio.Managers.FontManager;
import any.audio.Models.DownloadingItemModel;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.L;
import any.audio.helpers.TaskHandler;
import any.audio.helpers.ToastMaker;

/**
 * Created by Ankit on 9/25/2016.
 */
public class DownloadingAdapter extends ArrayAdapter<String> {

    private static Context context;
    private static DownloadingAdapter mInstance;
    private ArrayList<DownloadingItemModel> downloadingList;
    private DownloadCancelListener downloadCancelListener;
    private Typeface tfIcon;
    private SharedPrefrenceUtils utils;

    public DownloadingAdapter(Context context) {
        super(context, 0);
        DownloadingAdapter.context = context;
        downloadingList = new ArrayList<>();
        utils = SharedPrefrenceUtils.getInstance(context);
        tfIcon = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);

    }

    public static DownloadingAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadingAdapter(context);
        }
        return mInstance;
    }

    public void setOnDownloadCancelListener(DownloadCancelListener listener) {
        this.downloadCancelListener = listener;
    }

    public void setDownloadingList(ArrayList<DownloadingItemModel> list) {
        this.downloadingList = list;
        notifyDataSetChanged();
    }

    public void removeItem(String taskId){
        for(DownloadingItemModel item:downloadingList){
            if(taskId.equals(item.taskId)){
                downloadingList.remove(item);
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final DownloadingItemViewHolder viewHolder;

        if(convertView==null){
            viewHolder = new DownloadingItemViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.downloading_item_layout,parent,false);
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.downloading_item_thumbnail);
            viewHolder.taskTitle = (TextView) convertView.findViewById(R.id.downloading_item_title);
            viewHolder.taskArtist = (TextView) convertView.findViewById(R.id.downloading_item_artist);
            viewHolder.statusMessage = (TextView) convertView.findViewById(R.id.downloading_item_status);
            viewHolder.contentSizeMB = (TextView) convertView.findViewById(R.id.content_size_MB);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.downloading_item_progressBar);
            viewHolder.progressText = (TextView) convertView.findViewById(R.id.downloading_item_progressText);
            viewHolder.stopStartBtn = (TextView) convertView.findViewById(R.id.downloading_item_startStopBtn);
            viewHolder.stopStartBtn.setTypeface(tfIcon);
            viewHolder.cancelBtn = (TextView) convertView.findViewById(R.id.downloading_item_cancel_btn);
            viewHolder.cancelBtn.setTypeface(tfIcon);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (DownloadingItemViewHolder) convertView.getTag();
        }

        final DownloadingItemModel data = downloadingList.get(position);
        // value bindings
        if(ConnectivityUtils.isConnectedToNet()){
            Log.d("Downloading","thumbnail "+data.thumbnailUrl);
            Picasso.with(context).load(data.thumbnailUrl).into(viewHolder.thumbnail);
        }

        String title = data.title.replace("\n","").replace("\r","");
        int lastInd = title.length()>32?30:title.length();
        viewHolder.taskTitle.setText(data.title.substring(0,lastInd
        ));
        viewHolder.taskArtist.setText(data.artist);

        String toStopDownloadText = "\uE047";
        switch (data.downloadingState) {
            case Constants.DOWNLOAD.STATE_STOPPED: {
                //stopped state

                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.progressText.setVisibility(View.GONE);

                viewHolder.statusMessage.setVisibility(View.VISIBLE);
                String msg = "Stopped Downloading";
                viewHolder.statusMessage.setText(msg);
                String toStartDownloadText = "\uE037";
                viewHolder.stopStartBtn.setText(toStartDownloadText);

                break;
            }
            case Constants.DOWNLOAD.STATE_WAITING: {
                // waiting item


                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.progressText.setVisibility(View.GONE);

                viewHolder.statusMessage.setVisibility(View.VISIBLE);
                String msg = "Waiting To Start";
                viewHolder.statusMessage.setText(msg);
                viewHolder.stopStartBtn.setText(toStopDownloadText);


                break;
            }
            default:

                //downloading state
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.progressText.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setProgress(Integer.parseInt(data.progress));

                viewHolder.progressText.setText(data.progress + " %");
                viewHolder.contentSizeMB.setText(data.contentSize);
                viewHolder.statusMessage.setVisibility(View.GONE);
                viewHolder.stopStartBtn.setText(toStopDownloadText);

                break;
        }

        viewHolder.stopStartBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                return false;
            }
        });

        viewHolder.stopStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(utils.getTaskStatus(data.taskId).equals(Constants.DOWNLOAD.STATE_STOPPED)) {

                    if(ConnectivityUtils.isConnectedToNet()) {
                        TaskHandler.getInstance(context).restartTask(data.taskId);
                    }else{
                        ToastMaker.getInstance(context).toast("No Internet Connection!");
                    }

                }else{
                    // either waiting or downloading = > stop the task
                    TaskHandler.getInstance(context).stopTask(data.taskId);
                }

            }
        });

        viewHolder.cancelBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getBackground().setHotspot(motionEvent.getX(),motionEvent.getY());
                return false;
            }
        });

        viewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskHandler.getInstance(context).cancelTask(data.taskId);
                removeItem(data.taskId);
            }
        });

        return convertView;
    }

    public static class DownloadingItemViewHolder{

        ImageView thumbnail;
        ProgressBar progressBar;
        TextView progressText;
        TextView cancelBtn;
        TextView taskTitle;
        TextView taskArtist;
        TextView statusMessage;
        TextView stopStartBtn;
        TextView contentSizeMB;

    }

    @Override
    public int getCount() {
        return downloadingList.size();
    }


    private void cancelDownload(String taskID) {
        L.m("LiveDownloadAdapter","cancelled task " + taskID + " li" + downloadCancelListener);
        if (this.downloadCancelListener != null) {
            downloadCancelListener.onDownloadCancel(taskID);
        }
        // remove items from list

    }

    private void log(String msg) {
        Log.d("LiveDownloadAdapter", msg);
    }

}
