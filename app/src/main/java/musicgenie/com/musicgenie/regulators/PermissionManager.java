package musicgenie.com.musicgenie.regulators;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;

import musicgenie.com.musicgenie.R;

/**
 * Created by Ankit on 8/24/2016.
 */
public class PermissionManager {

    private static final int PERMISSION_GRANTED = 1;
    private static final int PERMISSION_DENIED = 0;
    private static Context context;
    private static PermissionManager mInstance;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public static PermissionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PermissionManager(context);
        }
        return mInstance;
    }


    public void seek(){


        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
                );
            }
        }

    }
}
