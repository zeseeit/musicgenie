package any.audio.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import any.audio.Activity.ErrorSplash;

/**
 * Created by Ankit on 8/5/2016.
 */
public class ConnectivityUtils {

    private static Context context;
    private static ConnectivityUtils mInstance;

    public ConnectivityUtils(Context context) {
        ConnectivityUtils.context = context;
    }

    public static ConnectivityUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ConnectivityUtils(context);
        }
        return mInstance;
    }

    public static boolean isConnectedToNet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileData.isConnected()) {
            return true;
        } else if (wifi.isConnected()) {
            return true;
        }

//        context.startActivity(new Intent(context, ErrorSplash.class));
//        ((AppCompatActivity) context).finish();

        return false;
    }

    // for wifi service [where login is required before internet ]
    private static boolean isWorkingNet(){
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}