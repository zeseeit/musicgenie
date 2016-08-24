package musicgenie.com.musicgenie.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.security.Permission;

import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.notification.LocalNotificationManager;
import musicgenie.com.musicgenie.regulators.PermissionManager;

/**
 * Created by Ankit on 8/5/2016.
 */
public class AppConfig extends Application {

    public static final String SERVER_URL = "http://ymp3.aavi.me";
    public static final String SDCARD = "sdcard";
    public static final String PHONE = "phone";
    public static final int SCREEN_MODE_TABLET = 0 ;
    public static final int SCREEN_MODE_MOBILE = 1;
    public static final String ACTION_PROGRESS_UPDATE_BROADCAST = "action_progress_update";
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String FILES_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musicgenie/Audio";
    public static final String ACTION_NETWORK_CONNECTED = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final int SCREEN_ORIENTATION_PORTRAIT = 0;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 1;
    public static final String EXTRA_CONTENT_SIZE = "contentSize";
    private static final String TAG = "AppConfig";
    private static Context context;
    private static AppConfig mInstance;

    public AppConfig(Context context) {
        this.context = context;
    }

    public static AppConfig getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppConfig(context);
        }
        return mInstance;
    }


    //create dirs
    public static void configureDevice() {

        String savePref = SharedPrefrenceUtils.getInstance(context).getFileSavingLocation();
        int tasks_pending = TaskHandler.getInstance(context).getTaskCount();
         if(tasks_pending>0)LocalNotificationManager.getInstance(context).launchNotification("You Have "+ tasks_pending +" Tasks Pending");

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                PermissionManager.getInstance(context).seek();
            }

            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root+ "/Musicgenie/Audio");

            boolean s = false;
            if (dir.exists() == false) {
                s = dir.mkdirs();
            }

        Log.d(TAG, "configureDevice : made directory "+s );

    }
}
