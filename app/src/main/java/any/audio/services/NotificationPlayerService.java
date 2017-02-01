package any.audio.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import any.audio.Activity.Home;
import any.audio.Config.Constants;
import any.audio.R;
import any.audio.SharedPreferences.StreamSharedPref;
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
                    if (PLAYING) {
                        PLAYING = false;
                    } else {
                        PLAYING = true;
                    }
                    sendPlayerStateBroadcast();
                    showNotification();

                } else {

                    Log.d("NotificationService", " action from bottom streamsheet");
                    PLAYING = bundle.getBoolean(Constants.PLAYER.EXTRAA_PLAYER_STATE);
                    showNotification();

                }
                break;
            case Constants.ACTIONS.STOP_FOREGROUND_ACTION:

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

        return START_STICKY;
    }

    private void swipeCancel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            stopForeground(false);
            stopSelf();
        }else{

            stopForeground(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        }

        sendStopAction();
    }

    private void sendPlayerStateBroadcast() {

        String action = PLAYING ? Constants.ACTIONS.PAUSE_TO_PLAY : Constants.ACTIONS.PLAY_TO_PAUSE;
        Intent stateIntent = new Intent(action);
        sendBroadcast(stateIntent);

    }

    private void sendStopAction() {

        String action = Constants.ACTIONS.STOP_PLAYER;
        Intent stateIntent = new Intent(action);
        sendBroadcast(stateIntent);

    }

    Notification notification;
    private final String LOG = "NotificationService";

    private void showNotification() {

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification_player_control_handler_small_view);
        RemoteViews bigView = new RemoteViews(getPackageName(), R.layout.notification_player_control_handler_big_view);

        Intent notificationIntent = new Intent(this, Home.class);
        notificationIntent.setAction(Constants.ACTIONS.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent playIntent = new Intent(this, NotificationPlayerService.class);
        playIntent.setAction(Constants.ACTIONS.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent deleteIntent = new Intent(this, NotificationPlayerService.class);
        deleteIntent.setAction(Constants.ACTIONS.SWIPE_TO_CANCEL);
        PendingIntent deletePedingIntent = PendingIntent.getService(this, 0, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent closeIntent = new Intent(this, NotificationPlayerService.class);
        closeIntent.setAction(Constants.ACTIONS.STOP_FOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        String streamThumbnailUrl = StreamSharedPref.getInstance(this).getStreamThumbnailUrl();
        String title = StreamSharedPref.getInstance(this).getStreamTitle();
        String subtitle = StreamSharedPref.getInstance(this).getStreamSubTitle();
        String contentLen = StreamSharedPref.getInstance(this).getStreamingContentLength();

        if (PLAYING) {
            //  show pause btn
            // show title
            // load thumbnail

            //small view
            view.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            view.setOnClickPendingIntent(R.id.notification_player_stopBtn, pcloseIntent);

            view.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_pause);
            view.setImageViewResource(R.id.notification_player_stopBtn, R.drawable.ic_action_stop);
            view.setTextViewText(R.id.notification_player_title, title);
            view.setTextViewText(R.id.notification_player_track_length, contentLen);

            //big view
            bigView.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            bigView.setOnClickPendingIntent(R.id.notification_player_stopBtn, pcloseIntent);

            bigView.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_pause);
            bigView.setImageViewResource(R.id.notification_player_stopBtn, R.drawable.ic_action_stop);
            bigView.setTextViewText(R.id.notification_player_subtitle, subtitle);
            bigView.setTextViewText(R.id.notification_player_title, title);
            bigView.setTextViewText(R.id.notification_player_track_length, contentLen);

        } else {

            view.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            view.setOnClickPendingIntent(R.id.notification_player_stopBtn, pcloseIntent);

            view.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_play);
            view.setImageViewResource(R.id.notification_player_stopBtn, R.drawable.ic_action_stop);
            view.setTextViewText(R.id.notification_player_title, title);

            //big view
            bigView.setOnClickPendingIntent(R.id.notification_player_play_pauseBtn, pplayIntent);
            bigView.setOnClickPendingIntent(R.id.notification_player_stopBtn, pcloseIntent);

            bigView.setImageViewResource(R.id.notification_player_play_pauseBtn, R.drawable.ic_action_play);
            bigView.setImageViewResource(R.id.notification_player_stopBtn, R.drawable.ic_action_stop);
            bigView.setTextViewText(R.id.notification_player_title, title);

        }

        notification = new Notification.Builder(this).build();
        notification.contentView = view;
        notification.bigContentView = bigView;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.icon = R.drawable.notifications_bar_small;
        notification.tickerText = title;
        notification.deleteIntent = deletePedingIntent;
        notification.contentIntent = pendingIntent;

        Picasso.with(this).load(streamThumbnailUrl).transform(new CircularImageTransformer()).into(view, R.id.notification_player_thumbnail, Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        Picasso.with(this).load(streamThumbnailUrl).into(bigView, R.id.notification_player_thumbnail, Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

    }

}
