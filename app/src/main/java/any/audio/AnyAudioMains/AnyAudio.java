package any.audio.AnyAudioMains;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import any.audio.Activity.Home;
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
        Log.d("AnyAudioApp","[Application] onCreate()");

        StreamSharedPref.getInstance(this).resetStreamInfo();
        StreamSharedPref.getInstance(this).setStreamUrlFetchedStatus(false);
        startService(new Intent(this, UpdateCheckService.class));
        Log.d("AnyAudioApp","reset shared pref. for stream status");
        super.onCreate();
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onTerminate() {
        StreamSharedPref.getInstance(this).setStreamState(false);
        super.onTerminate();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


}
