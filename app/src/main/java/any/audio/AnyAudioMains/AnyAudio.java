package any.audio.AnyAudioMains;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import any.audio.Activity.Home;
import any.audio.Activity.UpdateThemedActivity;
import any.audio.Config.Constants;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.services.UpdateCheckService;

/**
 * Created by Ankit on 11/27/2016.
 */

public class AnyAudio extends Application {

    public AnyAudio() {
        super();
    }

    @Override
    public void onCreate() {
        Log.d("AnyAudioApp", "[Application] onCreate()");

        StreamSharedPref.getInstance(this).resetStreamInfo();
        StreamSharedPref.getInstance(this).setStreamUrlFetchedStatus(false);
        startService(new Intent(this, UpdateCheckService.class));
        checkForUpdate();
        Log.d("AnyAudioApp", "reset shared pref. for stream status");
        super.onCreate();
    }

    public void checkForUpdate() {

        if (SharedPrefrenceUtils.getInstance(this).getNewVersionAvailibility()) {

            Intent updateIntent = new Intent(getApplicationContext(), UpdateThemedActivity.class);
            updateIntent.putExtra(Constants.EXTRAA_NEW_UPDATE_DESC,SharedPrefrenceUtils.getInstance(this).getNewVersionDescription());
            updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(updateIntent);

        }
    }

}
