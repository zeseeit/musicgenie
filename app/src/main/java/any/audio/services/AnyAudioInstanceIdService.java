package any.audio.services;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import any.audio.Config.Constants;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 12/22/2016.
 */

public class AnyAudioInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = AnyAudioInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        String newToken = FirebaseInstanceId.getInstance().getToken();
        if (newToken != null) {
            Log.e(TAG, "Refreshed token :" + newToken);
            setupFirebasePushNotification(newToken);
        }
    }

    private void setupFirebasePushNotification(String token) {
        Log.d("AnyAudio", "token:" + token);
        //Subscribe to topics if opted and not yet subscribed
        SharedPrefrenceUtils sharedPrefrenceUtils = SharedPrefrenceUtils.getInstance(this);
        sharedPrefrenceUtils.setFcmToken(token);

        //if (!sharedPrefrenceUtils.donotRemindForAppUpdate() && !sharedPrefrenceUtils.subscribedForUpdate()) {
            //subscribe

            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE.TOPIC_UPDATE);
            sharedPrefrenceUtils.setSubscribedForUpdate(true);
        //}

       // if (sharedPrefrenceUtils.subscribeForDefaults_get()) {
            Log.d("AnyAudio", "Subscribing to Topics");
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE.TOPIC_RECOMMEND);
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE.TOPIC_EVE);
            sharedPrefrenceUtils.subscribeForDefaults_set(false);
        //}


    }



}
