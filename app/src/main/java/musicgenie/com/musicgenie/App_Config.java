package musicgenie.com.musicgenie;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Ankit on 8/5/2016.
 */
public class App_Config {
    //TODO: add server url
    public static final String SERVER_URL = "http://ymp3.aavi.me";
    public static final String SDCARD = "sdcard";
    public static final String PHONE = "phone";
    private Context context;

    public App_Config(Context context) {
        this.context = context;
    }

    //create dirs
    public void configureDevice() {

        String savePref = SharedPrefrenceUtils.getInstance(context).getFileSavingLocation();

        if (savePref.equals(App_Config.PHONE)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Musicgenie/Audio");

            if (dir.exists() == false) {
                dir.mkdirs();
            }
        } else {

        }


    }
}
