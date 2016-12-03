package any.audio.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import any.audio.Activity.UpdateThemedActivity;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.Config.URLS;
import any.audio.Network.VolleyUtils;
import any.audio.helpers.L;

/**
 * Created by Ankit on 10/4/2016.
 */
public class UpdateCheckService extends Service {

//    private static final long CHECK_UPDATE_INTERVAL = 6 * 60 * 60 * 1000;     // 6 hrs interval
    private static final long CHECK_UPDATE_INTERVAL = 20 * 1000;     // 20 sec interval
    private static final int SERVER_TIMEOUT_LIMIT = 10 * 1000; // 10 sec
    private static Timer mTimer;
    Handler mHandler = new Handler();
    private final String url = URLS.URL_LATEST_APP_VERSION;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new RegularUpdateTimerTask(), 0, CHECK_UPDATE_INTERVAL);

    }

    private void checkForUpdate() {

        Log.d("UpdateServiceAnyAudio", " UpdateCheck....");


        StringRequest updateCheckReq = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        handleNewUpdateResponse(s);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        updateCheckReq.setRetryPolicy(new DefaultRetryPolicy(
                SERVER_TIMEOUT_LIMIT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(updateCheckReq, "checkUpdateReq", getApplicationContext());
    }

    class RegularUpdateTimerTask extends TimerTask {

        @Override
        public void run() {
            checkForUpdate();
        }
    }

    public boolean isForeground(String myPackage) {

        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        String mpackageName = "";
        if (Build.VERSION.SDK_INT > 20) {
            mpackageName = String.valueOf(mActivityManager.getRunningAppProcesses().get(0).processName);
        } else {
            mpackageName = String.valueOf(mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName());
        }
        L.m("UpdateService", "found " + mpackageName);
        return mpackageName.equals(myPackage);
    }

    private int getCurrentAppVersionCode() {
        return SharedPrefrenceUtils.getInstance(getApplicationContext()).getCurrentVersionCode();
    }

    public void handleNewUpdateResponse(String response) {
        /*
        * New Update Message Format
        *
        * {
        *   "version":1.0,
        *   "newInThisUpdate":"v1.0:  bug fixes",
        *   "appDownloadUrl":"..."
        * }
        *
        * */

        try {
            JSONObject updateResp = new JSONObject(response);

            double newVersion = updateResp.getDouble("version");
            String updateDescription = updateResp.getString("newInThisUpdate");
            String downloadUrl = updateResp.getString("appDownloadUrl");
            Log.d("UpdateServiceTest"," new Version "+newVersion+" old version "+getCurrentAppVersionCode()+" update Des "+updateDescription);

            if (newVersion > getCurrentAppVersionCode()) {
                // write update to shared pref..
                Log.d("UpdateService", " writing response to shared Pref..");
                SharedPrefrenceUtils.getInstance(getApplicationContext()).setNewVersionAvailibility(true);
                SharedPrefrenceUtils.getInstance(getApplicationContext()).setNewVersionDescription(updateDescription);
                SharedPrefrenceUtils.getInstance(getApplicationContext()).setNewUpdateUrl(downloadUrl);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
