package any.audio.Config;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import java.io.File;

import any.audio.Managers.PermissionManager;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
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
        return SharedPrefrenceUtils.getInstance(context).getCurrentVersionCode();
    }

    private String getCurrentAppVersionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pInfo.versionName;
    }


    public static void configureDevice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionManager.getInstance(context).seek();
        }

        File dir = new File(Constants.DOWNLOAD_FILE_DIR);
        boolean s = false;
        if (!dir.exists()) {
            s = dir.mkdirs();
        }

        Log.d(TAG, "configureDevice : made directory " + s);

    }

}
