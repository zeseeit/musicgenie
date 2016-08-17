package musicgenie.com.musicgenie.notification;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import musicgenie.com.musicgenie.handlers.TaskHandler;

/**
 * Created by Ankit on 8/17/2016.
 */
public class AlertDialogManager {

    private static Context context;
    private static AlertDialogManager mInstance;

    public AlertDialogManager(Context context) {
        this.context = context;
    }

    public static AlertDialogManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AlertDialogManager(context);
        }
        return mInstance;
    }

    public void popAlertForPendings(int pendingCount){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        String title = "Download";
        String msg = pendingCount+" Files Pending. Do You Want To Retry Them ?";
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        //TODO: add alert dialog icon

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO: re-initiate handler if not running
                TaskHandler.getInstance(context).initiate();
                log("retry downloading all pendings");
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO: remove tasks
                TaskHandler.getInstance(context).removeAllTasks();
                log("removed all task");
            }
        });

        alertDialog.show();
    }

    public void log(String msg){
        Log.d("AlertDialogManager",msg);
    }
}
