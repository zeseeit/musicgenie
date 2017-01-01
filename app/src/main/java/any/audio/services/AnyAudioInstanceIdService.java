package any.audio.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ankit on 12/22/2016.
 */

public class AnyAudioInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = AnyAudioInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        String newToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token :" + newToken);

    }
}
