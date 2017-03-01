package any.audio.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import any.audio.Activity.UpdateThemedActivity;
import any.audio.Config.Constants;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.Config.URLS;
import any.audio.Network.VolleyUtils;
import any.audio.helpers.L;

/**
 * Created by Ankit on 10/4/2016.
 */
public class UpdateCheckService extends Service {

    private static final int SERVER_TIMEOUT_LIMIT = 10 * 1000; // 10 sec
    SharedPrefrenceUtils utils;
    private final String url = URLS.URL_LATEST_APP_VERSION;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        utils = SharedPrefrenceUtils.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null && intent.getAction()!=null)
        if(intent.getAction().equals("ACTION_UPDATE")){
            checkForUpdate();
        }

        return START_NOT_STICKY;
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

    private int getCurrentAppVersionCode() {

        try {

            PackageInfo _info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            return _info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            return -1;

        }
    }

    public void handleNewUpdateResponse(String response) {

        Log.d("UpdateService"," response "+response);

        try {

            JSONObject updateResp = new JSONObject(response);
            int newVersion = updateResp.getInt("versionCode");
            String newVersionName = updateResp.getString("versionName");
            String updateDescription = updateResp.getString("newInThisUpdate");
            String downloadUrl = updateResp.getString("appDownloadUrl");
            Log.d("UpdateServiceTest", " new Version " + newVersion + " old version " + getCurrentAppVersionCode() + " update Des " + updateDescription);

            if (newVersion > getCurrentAppVersionCode()) {
                // write update to shared pref..
                Log.d("UpdateService", " writing response to shared Pref..");
                utils.setNewVersionAvailibility(true);
                utils.setNewVersionName(newVersionName);
                utils.setNewVersionCode(newVersion);
                utils.setNewVersionDescription(updateDescription);
                utils.setNewUpdateUrl(downloadUrl);

                //start update themed activity

                Intent updateIntent = new Intent(this, UpdateThemedActivity.class);
                updateIntent.putExtra(Constants.EXTRAA_NEW_UPDATE_DESC, utils.getNewVersionDescription());
                updateIntent.putExtra(Constants.KEY_NEW_UPDATE_URL, utils.getNewUpdateUrl());
                updateIntent.putExtra(Constants.KEY_NEW_ANYAUDIO_VERSION,utils.getLatestVersionName());
                updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(updateIntent);


            }else {
                Intent intent = new Intent();
                intent.setAction("ACTION_UPDATE_CHECK");
                intent.putExtra("msg","You have Updated Version");
                sendBroadcast(intent);
                utils.setNewVersionAvailibility(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
