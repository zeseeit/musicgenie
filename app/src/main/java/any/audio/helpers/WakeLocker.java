package any.audio.helpers;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by Ankit on 12/5/2016.
 */

public class WakeLocker {

    private Context mContext;
    private WifiManager.WifiLock wifiLock;
    private String WIFI_LOCK_TAG = "anyaudio_lock";

    public WakeLocker(Context context) {
        this.mContext = context;
        wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, WIFI_LOCK_TAG);
    }

    public void release() {
        if (wifiLock != null) {
            wifiLock.release();
        }
    }

    public void acquire() {
        wifiLock.acquire();
    }

}
