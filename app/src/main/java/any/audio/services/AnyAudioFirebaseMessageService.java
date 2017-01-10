package any.audio.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import any.audio.helpers.AnyAudioPushHandler;

/**
 * Created by Ankit on 12/22/2016.
 */

public class AnyAudioFirebaseMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("AnyAudioFirebase", "message rec");

        // if payloads available
        if (remoteMessage.getData().size() > 0) {
            Log.d("AnyAudioFirebase", "message received");
            AnyAudioPushHandler.getInstance(this)
                    .setNotificationPayload(remoteMessage.getNotification())
                    .handlePush(remoteMessage.getData());
        }

    }

}
