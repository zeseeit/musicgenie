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
        //SharedPrefrenceUtils.getInstance(this).setFirstPageLoadedStatus(false);
//
//        Log.d("AnyAudioApp", "reset shared pref. for stream status..&");
//        startService(new Intent(this, UpdateCheckService.class));
//        setForUpdate();
//        Log.d("AnyAudioApp", " started Update Service");
        setupFirebasePushNotification();
        super.onCreate();
    }

    private void setupFirebasePushNotification() {
        //Register device and get device token
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("AnyAudio", "token:" + token);

        //Subscribe to topics if opted and not yet subscribed
        SharedPrefrenceUtils sharedPrefrenceUtils = SharedPrefrenceUtils.getInstance(this);
        if (!sharedPrefrenceUtils.donotRemindForAppUpdate() && !sharedPrefrenceUtils.subscribedForUpdate()) {
            //subscribe
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE.TOPIC_UPDATE);
            sharedPrefrenceUtils.setSubscribedForUpdate(true);
        }

        if (sharedPrefrenceUtils.subscribeForDefaults_get()) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE.TOPIC_RECOMMEND);
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE.TOPIC_EVE);
            sharedPrefrenceUtils.subscribeForDefaults_set(false);
        }


    }

    public void setForUpdate() {

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
        if (utils.getNewVersionAvailibility() && !utils.donotRemindForAppUpdate()) {
            utils.setNotifiedForUpdate(false);
        }
    }

}
