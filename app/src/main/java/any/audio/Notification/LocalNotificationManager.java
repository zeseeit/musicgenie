package any.audio.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

import any.audio.Activity.AnyAudioActivity;
import any.audio.R;

/**
 * Created by Ankit on 9/13/2016.
 */
public class LocalNotificationManager {

    public static LocalNotificationManager mInstance;
    private static Context context;
    private int mNotificationId = 0;
    public LocalNotificationManager(){}

    public LocalNotificationManager(Context context){
        LocalNotificationManager.context = context;
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
        mBuilder.setContentTitle("AnyAudio");
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_app);
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setSmallIcon(R.drawable.notification);
        mBuilder.setContentText(msg);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        this.mNotificationId +=1;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(mNotificationId, mBuilder.build());
        cancelPendingNotification();

    }

    public void publishProgressOnNotification(final int progress,String item_name){

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        Intent intent = new Intent(context, AnyAudioActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle("AnyAudio");
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_app);
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.notification);
        mBuilder.setContentText(item_name);
        mNotificationId = 0; // single notificationId is enough as there is single downoad at a time

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mBuilder.setProgress(100,progress,false);
                manager.notify(mNotificationId , mBuilder.build());

                if(progress>=100){
                    manager.cancel(mNotificationId);
                }
            }
        }).start();


    }

    private void cancelPendingNotification(){

        // collapse download progress notifications
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

}
