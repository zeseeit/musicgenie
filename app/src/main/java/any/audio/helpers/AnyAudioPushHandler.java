package any.audio.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

import any.audio.Activity.EveWisherThemedActivity;
import any.audio.Activity.RecommendationThemed;
import any.audio.Activity.UpdateThemedActivity;
import any.audio.Config.Constants;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 12/24/2016.
 */

public class AnyAudioPushHandler {

    private static Context context;
    private static AnyAudioPushHandler mInstance;
    private SharedPrefrenceUtils utils;
    private RemoteMessage.Notification notificationPayload = null;

    public AnyAudioPushHandler(Context context) {
        this.context = context;
        utils = SharedPrefrenceUtils.getInstance(context);
    }

    public static AnyAudioPushHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AnyAudioPushHandler(context);
        }
        return mInstance;
    }

    public void handlePush(Map<String, String> data) {

        if (!SharedPrefrenceUtils.getInstance(context).getOptionsForPushNotification())
            return;

        String pushtype = data.get("push_type");
        Log.d("AnyAudioFirebase", "type " + pushtype + " notification " + notificationPayload);

        switch (pushtype) {
            case Constants.FIREBASE.TOPIC_RECOMMEND:
                handleRecommendations(data);
                break;
            case Constants.FIREBASE.TOPIC_EVE:
                handleEveWisher(data);
                break;

            case Constants.FIREBASE.TOPIC_UPDATE:
                handleNewUpdate(data);
                break;

            default:
                break;

        }
    }

    private void handleNewUpdate(Map<String, String> data) {

        Intent updateIntent = new Intent(context, UpdateThemedActivity.class);
        String newVersionCode = data.get("newVersionCode");
        if(!((Integer.valueOf(newVersionCode)>getCurrentVersion()))) {
            Log.d("PushHandler"," Updated Version");
            return;
        }
        utils.setNewUpdateUrl(data.get("downloadUrl"));
        utils.setNewVersionName(data.get("newVersionName"));
        utils.setNewVersionDescription(data.get("newInThis"));
        utils.setNewVersionAvailibility(true);
        utils.setNewVersionCode(Integer.valueOf(data.get("newVersionCode")));
        updateIntent.putExtra(Constants.EXTRAA_NEW_UPDATE_DESC, data.get("newInThis"));
        updateIntent.putExtra(Constants.KEY_NEW_UPDATE_URL, data.get("downloadUrl"));
        updateIntent.putExtra(Constants.KEY_NEW_ANYAUDIO_VERSION,data.get("newVersionName"));
        updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showNotification("AnyAudio Update!!", "Newer Version of App is Available", updateIntent);
        SharedPrefrenceUtils.getInstance(context).setNotifiedForUpdate(true);

    }

    private void handleEveWisher(Map<String, String> data) {

        String eveMessage = data.get("message");
        String title = data.get("title");

        String sgTitles = data.get("vtlts");
        String sgVids = data.get("vids");
        String sgYids = data.get("yids");
        String sgUploaders = data.get("uploaders");

        Intent eveWishIntent = new Intent(context, EveWisherThemedActivity.class);
        eveWishIntent.putExtra("message", eveMessage);
        eveWishIntent.putExtra("title", title);
        eveWishIntent.putExtra("vtlts",sgTitles);
        eveWishIntent.putExtra("vids",sgVids);
        eveWishIntent.putExtra("yids",sgYids);
        eveWishIntent.putExtra("uploaders",sgUploaders);
        eveWishIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showNotification("AnyAudio", "Wishes For You", eveWishIntent);

    }

    private void handleRecommendations(Map<String, String> data) {

        String searchTerm = data.get("search_term");
        String initial = data.get("initials");
        String artist = data.get("artist");
        String art = data.get("thumbnail");
        String title = data.get("title");

        Intent recomIntent = new Intent(context, RecommendationThemed.class);
        recomIntent.putExtra("recom", searchTerm);
        recomIntent.putExtra("artist", artist);
        recomIntent.putExtra("fixed", initial);
        recomIntent.putExtra("artUrl", art);
        recomIntent.putExtra("title",title);
        recomIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showNotification("AnyAudio", "AnyAudio`s Recommendations", recomIntent);


    }

    public AnyAudioPushHandler setNotificationPayload(RemoteMessage.Notification notification) {
        this.notificationPayload = notification;
        return this;
    }

    private int getCurrentVersion(){
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionCode;
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(9, notification);
    }

    // Playing notification sound
    public void playNotificationSound() {

        if (!SharedPrefrenceUtils.getInstance(context).getOptionsForPushNotificationSound())
            return;

        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String title, String message, Intent intent) {

        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getPackageName() + "/raw/notification");

        final int icon = R.drawable.notification;

        showSmallNotification(mBuilder, icon, title, message, getDateTime(), resultPendingIntent, alarmSound);
        playNotificationSound();

    }

    public String getDateTime() {

        Calendar calendar = Calendar.getInstance();
        String[] am_pm = {"AM", "PM"};
        return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + am_pm[calendar.get(Calendar.AM_PM)];

    }


}
