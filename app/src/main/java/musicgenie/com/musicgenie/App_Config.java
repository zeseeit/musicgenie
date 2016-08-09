package musicgenie.com.musicgenie;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

import br.com.bemobi.medescope.Medescope;

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
    private Context context;

    public App_Config(Context context) {
        this.context = context;
    }

    //create dirs
    public void configureDevice() {

        Medescope.getInstance(context).setApplicationName("MusicGenie");

        String savePref = SharedPrefrenceUtils.getInstance(context).getFileSavingLocation();

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
