package musicgenie.com.musicgenie.utilities;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.notification.LocalNotificationManager;

/**
 * Created by Ankit on 8/5/2016.
 */
public class App_Config extends Application {

    public static final String SERVER_URL = "http://ymp3.aavi.me";
    public static final String SDCARD = "sdcard";
    public static final String PHONE = "phone";
    public static final String ACTION_PROGRESS_UPDATE_BROADCAST = "action_progress_update";
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String FILES_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musicgenie/Audio";
    private Context context;

    public App_Config(Context context) {
        this.context = context;
    }

    //create dirs
    public void configureDevice() {

        String savePref = SharedPrefrenceUtils.getInstance(context).getFileSavingLocation();

       // LocalNotificationManager.getInstance(context).launchNotification("You Have "+ TaskHandler.getInstance(context).getTaskCount()+" Tasks Pending");

        if (savePref.equals(App_Config.PHONE)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root+ "/Musicgenie/Audio");

            if (dir.exists() == false) {
                dir.mkdirs();
            }
        } else {

        }


    }
}
