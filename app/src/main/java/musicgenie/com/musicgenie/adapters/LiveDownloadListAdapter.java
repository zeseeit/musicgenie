package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Ankit on 8/10/2016.
 */
public class LiveDownloadListAdapter extends ArrayAdapter<String> {

    private static Context context;
    private static LiveDownloadListAdapter mInstance;

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



}
