package musicgenie.com.musicgenie.regulators;

import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.mtp.MtpObjectInfo;
import android.net.IpPrefix;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import musicgenie.com.musicgenie.models.SecModel;

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

        //get imei number for unique identification
        //send to server
    }

    private SecModel getInfo(){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return new SecModel(info.getSSID().toString(),info.getMacAddress().toString(),String.valueOf(info.getIpAddress()),String.valueOf(info.getLinkSpeed()),String.valueOf(info.getNetworkId()));
    }

    public void checkForAppUpdates(){
        // check for apps update and notifies
    }

    public void getPromotionalData(){
        // receive ads from server and append as last item of download
    }



}
