package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import musicgenie.com.musicgenie.interfaces.DownloadCancelListener;

/**
 * Created by Ankit on 8/10/2016.
 */
public class LiveDownloadListAdapter extends ArrayAdapter<String> {

    private static Context context;
    private static LiveDownloadListAdapter mInstance;
    private DownloadCancelListener downloadCancelListener;
    public LiveDownloadListAdapter(Context context) {
        super(context,0);
        this.context = context;
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

    private void cancelDownload(String taskID){
        if(this.downloadCancelListener!=null){
            downloadCancelListener.onDownloadCancel(taskID);
        }

        //TODO: remove item from live-download list
    }


}
