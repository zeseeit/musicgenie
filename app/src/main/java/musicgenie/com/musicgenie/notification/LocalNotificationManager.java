package musicgenie.com.musicgenie.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import musicgenie.com.musicgenie.R;

/**
 * Created by Ankit on 8/10/2016.
 */
public class LocalNotificationManager {

    public static LocalNotificationManager mInstance;
    private static Context context;
    private int mNotificationId = 0;
    public LocalNotificationManager(){}

    public LocalNotificationManager(Context context){
        this.context = context;
    }

    public static LocalNotificationManager getInstance(Context context){
        if(mInstance==null){
            mInstance = new LocalNotificationManager(context);
        }
        return mInstance;
    }

    public void launchNotification(String msg){

        //TODO: change icon and add pendingIntent , which navigates user to downloads activity

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.app_icon);
        mBuilder.setContentTitle("MusicGenie");
        mBuilder.setContentText(msg);

        this.mNotificationId +=1;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(mNotificationId , mBuilder.build());
    }



}
