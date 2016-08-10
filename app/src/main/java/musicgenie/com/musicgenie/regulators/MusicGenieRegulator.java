package musicgenie.com.musicgenie.regulators;

import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.mtp.MtpObjectInfo;
import android.net.IpPrefix;

/**
 * Created by Ankit on 8/10/2016.
 */
public class MusicGenieRegulator {

    private static Context context;
    private static MusicGenieRegulator mInstance;

    public MusicGenieRegulator(Context context) {
        this.context = context;
    }

    public static MusicGenieRegulator getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MusicGenieRegulator(context);
        }
        return mInstance;
    }

    public void reportUserAct(){
        //TODO: how to get mac-add of device
        //get ip-add of device
        //send to server
    }

    public void checkForAppUpdates(){
        // check for apps update and notifies
    }

    public void getPromotionalData(){
        // receive ads from server and append as last item of download
    }



}
