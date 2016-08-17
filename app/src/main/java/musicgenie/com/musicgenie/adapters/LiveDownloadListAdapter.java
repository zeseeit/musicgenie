package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.interfaces.DownloadCancelListener;
import musicgenie.com.musicgenie.models.DownloadTaskModel;
import musicgenie.com.musicgenie.utilities.FontManager;

/**
 * Created by Ankit on 8/10/2016.
 */
public class LiveDownloadListAdapter extends ArrayAdapter<String> {

    private static Context context;
    private static LiveDownloadListAdapter mInstance;
    private ArrayList<DownloadTaskModel> downloadingList;
    private DownloadCancelListener downloadCancelListener;

    public LiveDownloadListAdapter(Context context) {
        super(context,0);
        this.context = context;
        downloadingList = new ArrayList<>();
    }

    public static LiveDownloadListAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LiveDownloadListAdapter(context);
        }
        return mInstance;
    }

    public void setOnDownloadCancelListener(DownloadCancelListener listener){
        this.downloadCancelListener = listener;
    }

    public void setDownloadingList(ArrayList<DownloadTaskModel> list){
        this.downloadingList = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


         ProgressBar progressBar;
         TextView progressText;
         final Button cancelBtn;
         TextView taskTitle;
        View v  = convertView;
        if(v == null){
            v = LayoutInflater.from(context).inflate(R.layout.downloading_item,parent,false);
        }
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressText = (TextView) v.findViewById(R.id.progressText);
        cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
        taskTitle = (TextView) v.findViewById(R.id.download_task_title);

        progressBar.setProgress(downloadingList.get(position).Progress);
        progressText.setText(downloadingList.get(position).Progress + " %");
        progressText.setTypeface(FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));
        taskTitle.setText(downloadingList.get(position).Title);
        taskTitle.setTypeface(FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDownload(downloadingList.get(position).taskID);
            }
        });

        return v;
    }

    @Override
    public int getCount() {
        return downloadingList.size();
    }


    private void cancelDownload(String taskID){
        log("cancelled task "+taskID+" li"+downloadCancelListener);
        if(this.downloadCancelListener!=null){
            downloadCancelListener.onDownloadCancel(taskID);
        }

    }

    private void log(String msg){
        Log.d("LiveDownloadAdapter",msg);
    }

}
