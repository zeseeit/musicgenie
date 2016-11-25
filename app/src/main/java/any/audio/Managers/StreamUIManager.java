package any.audio.Managers;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import any.audio.Fragments.StreamFragment;
import any.audio.R;
import any.audio.SharedPreferences.StreamSharedPref;

/**
 * Created by Ankit on 11/19/2016.
 */

public class StreamUIManager  {
    private static Context context;
    private static StreamUIManager mInstance;

    public StreamUIManager(Context context) {
        this.context = context;
    }

    public static StreamUIManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamUIManager(context);
        }
        return mInstance;
    }

    public void init(){

    }

    public boolean isStreaming(){
        boolean streaming = false;
        streaming = StreamSharedPref.getInstance(context).getStreamState();
       return streaming;
    }

    private void transactStreamFragment(){
            // just transact the new fragment that`s it ,
    }


}
