package any.audio.AnyAudioMains;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

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
        //SharedPrefrenceUtils.getInstance(this).setFirstPageLoadedStatus(false);

        Log.d("AnyAudioApp", "reset shared pref. for stream status..&");
        startService(new Intent(this, UpdateCheckService.class));
        setForUpdate();
        Log.d("AnyAudioApp", " started Update Service");

        super.onCreate();
    }

    public void setForUpdate() {

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);

        Log.d("AnyAudio"," is New Version "+utils.getNewVersionAvailibility()+" is dnd active "+utils.getDoNotRemindMeAgainForAppUpdate());

        if (utils.getNewVersionAvailibility() && !utils.getDoNotRemindMeAgainForAppUpdate()) {

            utils.setNotifiedForUpdate(false);

        }
    }

}
