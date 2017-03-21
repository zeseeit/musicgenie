package any.audio.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import any.audio.Activity.AnyAudioActivity;
import any.audio.Config.Constants;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.CircularImageTransformer;

/**
 * Created by Ankit on 12/4/2016.
 */

public class NotificationPlayerService extends Service {

    private boolean PLAYING = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null)
            switch (intent.getAction()) {

                case Constants.ACTIONS.START_FOREGROUND_ACTION:

                    PLAYING = true;
                    showNotification();
                    Log.d(LOG, "started foreground");

                    break;
                case Constants.ACTIONS.PLAY_ACTION:
                    Log.d(LOG, "clicked play/pause");

                    Bundle bundle = intent.getExtras();

                    if (bundle == null) {
                        Log.d("NotificationService", " action from notification bar buttons");
                        PLAYING = !PLAYING;
                        sendPlayerStateBroadcast();
                        showNotification();

                    } else {

                        Log.d("NotificationService", " action from bottom streamsheet");
                        PLAYING = bundle.getBoolean(Constants.PLAYER.EXTRAA_PLAYER_STATE);
                        showNotification();

                    }
                    break;

                case Constants.ACTIONS.NEXT_ACTION:

                    sendNextAction();

                    break;

                case Constants.ACTIONS.STOP_FOREGROUND_ACTION:
                    Log.i("NotificationPlayer", " Sending Stop Action");
                    sendStopAction();

                    break;
                case Constants.ACTIONS.SWIPE_TO_CANCEL:
                    swipeCancel();
                    break;
                case Constants.ACTIONS.STOP_FOREGROUND_ACTION_BY_STREAMSHEET:

                    Log.d(LOG, "stopped foreground service from user");
                    stopForeground(true);
                    stopSelf();

                    break;

                default:
                    break;
            }

        return START_NOT_STICKY;
    }

    private void sendNextAction() {

        String action = Constants.ACTION_STREAM_TO_SERVICE_NEXT;
        Intent stateIntent = new Intent(this, AnyAudioStreamService.class);
        Log.i("NotificationPlayer", " sending next action");
        stateIntent.setAction(action);
        startService(stateIntent);
    }

    private void swipeCancel() {

        Log.i("NotificationPlayer", "SwipeAction");
        sendStopAction();

    }

    private void sendPlayerStateBroadcast() {

        Log.i("NotificationPlayer", " sending play pause action");
        Intent stateIntent = new Intent(this, AnyAudioStreamService.class);
        stateIntent.setAction(Constants.ACTION_STREAM_TO_SERVICE_PLAY_PAUSE);
        stateIntent.putExtra("play", PLAYING);
        stateIntent.putExtra("imFromNotification", true);
        startService(stateIntent);

    }

    private void sendStopAction() {

        String action = Constants.ACTION_STREAM_TO_SERVICE_RELEASE;
        Intent stateIntent = new Intent(this, AnyAudioStreamService.class);
        stateIntent.setAction(action);
        Log.i("NotificationPlayer", " sending stop action");
        startService(stateIntent);
        stopForeground(true);

    }

    Notification notification;
    private final String LOG = "NotificationService";

    private void showNotification() {

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification_player_control_handler_small_view);
        RemoteViews bigView = new RemoteViews(getPackageName(), R.layout.notification_player_control_handler_big_view);

        Intent notificationIntent = new Intent(this, AnyAudioActivity.class);
        notificationIntent.setAction(Constants.ACTIONS.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent playIntent = new Intent(this, NotificationPlayerService.class);
        playIntent.setAction(Constants.ACTIONS.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextPlayIntent = new Intent(this, NotificationPlayerService.class);
        nextPlayIntent.setAction(Constants.ACTIONS.NEXT_ACTION);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextPlayIntent, 0);

        Intent deleteIntent = new Intent(this, NotificationPlayerService.class);
        deleteIntent.setAction(Constants.ACTIONS.SWIPE_TO_CANCEL);
        PendingIntent deletePedingIntent = PendingIntent.getService(this, 0, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent closeIntent = new Intent(this, NotificationPlayerService.class);
        closeIntent.setAction(Constants.ACTIONS.STOP_FOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        Intent closeIntent_dull = new Intent(this, NotificationPlayerService.class);
        closeIntent_dull.setAction(Constants.ACTIONS.DN);
        PendingIntent pcloseIntent_dull = PendingIntent.getService(this, 0, closeIntent_dull, 0);

        String streamThumbnailUrl = SharedPrefrenceUtils.getInstance(this).getLastItemThumbnail();
        String title = SharedPrefrenceUtils.getInstance(this).getLastItemTitle();
        String subtitle = SharedPrefrenceUtils.getInstance(this).getLastItemArtist();

        if (PLAYING) {

            //small view
            view.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            view.setOnClickPendingIntent(R.id.notification_player_nextBtn, nextPendingIntent);

            view.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_pause);
            view.setImageViewResource(R.id.notification_player_nextBtn, R.drawable.ic_action_next);
            view.setTextViewText(R.id.notification_player_title, title);
            view.setTextViewText(R.id.notification_player_subtitle, subtitle);

            //big view
            bigView.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            bigView.setOnClickPendingIntent(R.id.notification_player_nextBtn, nextPendingIntent);

            bigView.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_pause);
            bigView.setImageViewResource(R.id.notification_player_nextBtn, R.drawable.ic_action_next);

            bigView.setTextViewText(R.id.notification_player_subtitle, subtitle);
            bigView.setTextViewText(R.id.notification_player_title, title);

        } else {

            //small view
            view.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            view.setOnClickPendingIntent(R.id.notification_player_nextBtn, nextPendingIntent);

            view.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_play);
            view.setImageViewResource(R.id.notification_player_nextBtn, R.drawable.ic_action_next);
            view.setTextViewText(R.id.notification_player_title, title);
            view.setTextViewText(R.id.notification_player_subtitle, subtitle);

            //big view
            bigView.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            bigView.setOnClickPendingIntent(R.id.notification_player_nextBtn, nextPendingIntent);

            bigView.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_play);
            bigView.setImageViewResource(R.id.notification_player_nextBtn, R.drawable.ic_action_next);

            bigView.setTextViewText(R.id.notification_player_subtitle, subtitle);
            bigView.setTextViewText(R.id.notification_player_title, title);

        }


        notification = new Notification.Builder(this).build();
        notification.contentView = view;
        notification.bigContentView = bigView;
        notification.flags = PLAYING ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL;
        notification.icon = R.mipmap.inotify;
        notification.tickerText = title;
        notification.deleteIntent = deletePedingIntent;
        notification.contentIntent = pendingIntent;

        Picasso.with(this).load(streamThumbnailUrl).transform(new CircularImageTransformer()).into(view, R.id.notification_player_thumbnail, Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        Picasso.with(this).load(streamThumbnailUrl).into(bigView, R.id.notification_player_thumbnail, Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        ((NotificationManager) this.getSystemService(NOTIFICATION_SERVICE)).notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);


    }

}
