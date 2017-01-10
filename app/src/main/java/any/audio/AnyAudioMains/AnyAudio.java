package any.audio.AnyAudioMains;

import android.app.Application;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import any.audio.Config.Constants;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;

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
//        //SharedPrefrenceUtils.getInstance(this).setFirstPageLoadedStatus(false);
////
////        Log.d("AnyAudioApp", "reset shared pref. for stream status..&");
////        startService(new Intent(this, UpdateCheckService.class));
////        setForUpdate();
////        Log.d("AnyAudioApp", " started Update Service");
//        setupFirebasePushNotification();
        super.onCreate();
    }
//
//    public void setForUpdate() {
//
//        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
//        if (utils.getNewVersionAvailibility() && !utils.donotRemindForAppUpdate()) {
//            utils.setNotifiedForUpdate(false);
//        }
//    }

}
