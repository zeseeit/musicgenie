package musicgenie.com.musicgenie;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ankit on 8/5/2016.
 */
public class SharedPrefrenceUtils {

    private static final String PREF_NAME = "musicgenie_tasks";
    private static SharedPrefrenceUtils mInstance;
    private static int MODE = 0;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    public SharedPrefrenceUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, MODE);
        editor = preferences.edit();
    }

    public static SharedPrefrenceUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefrenceUtils(context);
        }
        return mInstance;
    }

    public String getFileSavingLocation() {
        return preferences.getString("saveLocation", App_Config.PHONE);
    }

    public void setFileSavingLocation(String location) {
        editor.putString("saveLocation", location);
        editor.commit();
    }

}
