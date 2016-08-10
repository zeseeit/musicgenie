package musicgenie.com.musicgenie.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.interfaces.ConnectivityUtils;

/**
 * Created by Ankit on 8/10/2016.
 */
public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(ConnectivityUtils.getInstance(context).isConnectedToNet()){
            TaskHandler.getInstance(context).initiate();
        }else{
            TaskHandler.getInstance(context).pauseHandler();
        }

    }
}