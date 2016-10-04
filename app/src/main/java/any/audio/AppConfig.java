package any.audio;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.File;

/**
 * Created by Ankit on 9/13/2016.
 */
public class AppConfig extends Application {

    private static final String TAG = "AppConfig";
    private static final int SERVER_TIMEOUT_LIMIT = 10000;
    private static Context context;
    private static AppConfig mInstance;

    public AppConfig(Context context) {
        AppConfig.context = context;
    }

    public static AppConfig getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppConfig(context);
        }
        return mInstance;
    }

    private int getCurrentAppVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pInfo.versionCode;
    }

    private String getCurrentAppVersionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pInfo.versionName;
    }


    public static void configureDevice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionManager.getInstance(context).seek();
        }

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root + "/Musicgenie/Audio");

        boolean s = false;
        if (dir.exists() == false) {
            s = dir.mkdirs();
        }

        Log.d(TAG, "configureDevice : made directory " + s);

    }

    public void checkUpdates(final Handler mRefHandler) {
        final String url = URLS.URL_LATEST_APP_VERSION;

        StringRequest updateCheckReq = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                            L.m("AppConfig","response "+s);
                        if (Integer.parseInt(s)>getCurrentAppVersionCode()){
                            //  new version is available
                            Message msg = Message.obtain();
                            msg.arg1 = Constants.FLAG_NEW_VERSION;
                            mRefHandler.sendMessage(msg);
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

        VolleyUtils.getInstance().addToRequestQueue(updateCheckReq, "checkUpdateReq", context);

    }
}
