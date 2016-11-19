package any.audio.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

    private static final long CHECK_UPDATE_INTERVAL = 6 * 60 * 60 * 1000;     // 6 hrs interval
    private static final int SERVER_TIMEOUT_LIMIT = 10000;
    private Timer mTimer;
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
            //mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new RegularUpdateTimerTask(), 0, CHECK_UPDATE_INTERVAL);

    }

    class RegularUpdateTimerTask extends TimerTask {

        @Override
        public void run() {


            StringRequest updateCheckReq = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            double currentVersion = Double.parseDouble(s);

                            if (currentVersion > getCurrentAppVersionCode()) {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String homeActivityPackage = "any.audio";
                                        //launch activity when : home is in foreground & no previous launch of this
                                        L.m("UpdateService", "check for foreground");
                                        if (isForeground(homeActivityPackage)) {

                                            Intent updateIntent = new Intent(getApplicationContext(), UpdateThemedActivity.class);
                                            updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(updateIntent);
                                        }


                                    }
                                });
                            }

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


}
