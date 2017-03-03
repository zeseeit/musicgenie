package any.audio.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import any.audio.Config.Constants;
import any.audio.Models.PlaylistItem;
import any.audio.Network.ConnectivityUtils;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.L;
import any.audio.helpers.PlaylistGenerator;
import any.audio.helpers.QueueManager;
import any.audio.helpers.StreamUrlFetcher;
import any.audio.helpers.ToastMaker;

/**
 * Created by Ankit on 2/13/2017.
 */

public class AnyAudioStreamService extends Service {

    private static final String TAG = "AnyAudioPlayerService";
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private int UP_NEXT_PREPARE_TIME_OFFSET = 50000;
    private Uri mUri;
    public static ExoPlayer anyPlayer;
    private SharedPrefrenceUtils utils;
    private PhoneStateReceiver phoneReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AnyAudioStreamTest", " onCreate");
        utils = SharedPrefrenceUtils.getInstance(this);
        phoneReceiver = new PhoneStateReceiver();
        registerReceiver(phoneReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));

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

                case Constants.ACTION_STREAM_TO_SERVICE_START:

                    mUri = Uri.parse(intent.getExtras().getString("uri"));

                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            useExoplayer();
                            Looper.loop();
                        }
                    }.start();

                    break;
                case Constants.ACTION_STREAM_TO_SERVICE_RELEASE:

                    resetPlayer();

                    break;

                case Constants.ACTION_STREAM_TO_SERVICE_PLAY_PAUSE:

                    boolean playStatus = intent.getExtras().getBoolean("play");
                    boolean fromNotificationControl = intent.getExtras().getBoolean("imFromNotification");
                    int state = playStatus ? Constants.PLAYER.PLAYER_STATE_PLAYING : Constants.PLAYER.PLAYER_STATE_PAUSED;
                    utils.setPlayerState(state);

                    if (anyPlayer != null) {
                        anyPlayer.setPlayWhenReady(playStatus);
                    }

                    if (fromNotificationControl) {
                        broadcastPlayerStateToBottomPlayer(playStatus);

                    } else {
                        // this is from bottom player control
                        // => update same to notification control
                        notifyNotificationControl(playStatus);
                    }


                    break;

                case Constants.ACTION_STREAM_TO_SERVICE_NEXT:

                    Log.i("NotificationPlayer", " received next action");
                    if (anyPlayer != null) {
                        resetPlayer();
                    }
                    onNextRequested();

                    break;

                case Constants.ACTION_STREAM_TO_SERVICE_SEEK_TO:

                    int seekToPosition = intent.getExtras().getInt("seekTo");
                    anyPlayer.seekTo(seekToPosition);

                    break;
            }

        return START_NOT_STICKY;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        // stop music player if there
        unregisterReceiver(phoneReceiver);

        if (anyPlayer != null) {
            resetPlayer();
        }
        // close the notification
        closeNotification();
        super.onTaskRemoved(rootIntent);

    }

    private void notifyNotificationControl(boolean play) {

        Intent notificationIntent = new Intent(this, NotificationPlayerService.class);
        notificationIntent.setAction(Constants.ACTIONS.PLAY_ACTION);
        notificationIntent.putExtra(Constants.PLAYER.EXTRAA_PLAYER_STATE, play);
        startService(notificationIntent);

    }

    private void broadcastPlayerStateToBottomPlayer(boolean play) {

        Intent stateIntent = new Intent();

        if (play) {
            stateIntent.setAction(Constants.ACTIONS.PAUSE_TO_PLAY);
        } else {
            stateIntent.setAction(Constants.ACTIONS.PLAY_TO_PAUSE);
        }
        Log.i("NotificationPlayerState", " sending action=:" + stateIntent.getAction());
        sendBroadcast(stateIntent);

    }


    private void useExoplayer() {

        L.getInstance(this).dumpLog("Thread -  " + Thread.currentThread().getName() + " starting stream....");

        resetPlayer();
        int lastProgress = -1;
        anyPlayer = ExoPlayer.Factory.newInstance(1);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(this, "AnyAudio");
        DataSource dataSource = new DefaultUriDataSource(this, null, userAgent);

        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                mUri,
                dataSource,
                allocator,
                BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);

        anyPlayer.prepare(audioRenderer);
        anyPlayer.setPlayWhenReady(true);
        utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_PLAYING);

        //start Notification Player
        startNotificationService();

        anyPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == 5) // 5 - > integer code for player end state
                {
                    Log.d("ExoPlayer", " player ends => unregistering Notification control");
                    utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_STOPPED);
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d("ExoPlayer", "any error setting stream state false");
                utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_STOPPED);
            }
        });

        while (anyPlayer != null) {

            int playerCurrentPositon = (int) anyPlayer.getCurrentPosition();
            int playerContentDuration = (int) anyPlayer.getDuration();


            if (playerContentDuration != -1) {
                if (playerCurrentPositon >= playerContentDuration) {
                    Log.d("PlaylistText", " releasing and stoping anyplayer");
                    anyPlayer.setPlayWhenReady(false);
                    anyPlayer.release();
                    anyPlayer.stop();
                    utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_STOPPED);

                    playNext(true);  // true param is flag for refreshing the playlist items

                    break;
                }
            }

            if (anyPlayer.getPlayWhenReady()) {

                if (playerContentDuration != -1) {

                    if (utils.getNextStreamUrl().length() == 0 && !utils.isStreamUrlFetcherInProgress() && (playerContentDuration - playerCurrentPositon) < UP_NEXT_PREPARE_TIME_OFFSET) {

                        L.getInstance(this).dumpLog("Thread -  " + Thread.currentThread().getName() + " play next [cmd] from player");
                        Log.d("DuplicateTest", " fetching Next Url");
                        fetchNextUrl();

                    }
                }

                if (playerCurrentPositon > lastProgress) {

                    broadcastStreamProgresUpdate(
                            String.valueOf(playerCurrentPositon),
                            String.valueOf(playerContentDuration),
                            String.valueOf(anyPlayer.getBufferedPosition())
                    );

                    lastProgress = playerCurrentPositon;
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void resetPlayer() {

        //todo: option for collapsing the notification bar control
        //collapsePlayerNotificationControl();
        L.getInstance(this).dumpLog("Thread -  " + Thread.currentThread().getName() + "input to realease player");
        if (anyPlayer != null) {
            L.getInstance(this).dumpLog("Thread -  " + Thread.currentThread().getName() + " player released");
            anyPlayer.setPlayWhenReady(false);
            anyPlayer.stop();
            anyPlayer.release();
        }

        utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_STOPPED);

    }

    private void onNextRequested() {

        long diff = 9999;

        utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_STOPPED);
        PlaylistItem nxtItem = null;

        L.getInstance(this).dumpLog("Thread -  " + Thread.currentThread().getName() + " next Requested");

        if (utils.getAutoPlayMode()) {

            nxtItem = PlaylistGenerator.getInstance(this).getUpNext();
            // refresh the list w.r.t top item.
            if (nxtItem == null) {
                Toast.makeText(this, "No Item To Play Next.", Toast.LENGTH_LONG).show();
                return;
            }

            PlaylistGenerator.getInstance(this).refreshPlaylist();

        } else {
            nxtItem = QueueManager.getInstance(this).getUpNext();
            if (nxtItem == null) {
                Toast.makeText(this, "No Item To Play Next.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String upNextVid = nxtItem.videoId;
        String upNextTitle = nxtItem.title;
        String upNextThumbnailUrl = getImageUrl(nxtItem.youtubeId);
        String upNextArtist = nxtItem.uploader;

        Log.d("DuplicateTest", "nextVid:=" + upNextVid + " nextTitle:=" + upNextTitle);


        //todo: check for usage
        utils.setNextVId(upNextVid);
        utils.setNextStreamTitle(upNextTitle);

        if (anyPlayer != null)
            diff = anyPlayer.getDuration() - anyPlayer.getCurrentPosition();
        resetPlayer();

        L.getInstance(this).dumpLog("Thread -  "+Thread.currentThread().getName()+" onNext[AnyAudioService] calculating time diff ");

        if (diff > UP_NEXT_PREPARE_TIME_OFFSET) {

            L.getInstance(this).dumpLog("Thread -  "+Thread.currentThread().getName()+"[AnyAudioService] already fetched url");
            // means stream fetcher not in progress
            utils.setCurrentItemStreamUrl(upNextVid);
            utils.setCurrentItemThumbnailUrl(upNextThumbnailUrl);
            utils.setCurrentItemArtist(upNextArtist);
            utils.setCurrentItemTitle(upNextTitle);
            L.getInstance(this).dumpLog("Thread -  "+Thread.currentThread().getName()+" initStream[AnyAudioService]");
            Log.d("DuplicateTest", "starting normal stream..");
            initStream(upNextVid, upNextTitle);


        } else {

            L.getInstance(this).dumpLog("Thread -  "+Thread.currentThread().getName()+" uri not fetched : ");
            // means stream fetcher is in progress or has finished
            boolean isFetcherInProgress = utils.isStreamUrlFetcherInProgress();
            String nextStreamUrl = utils.getNextStreamUrl();

            if (nextStreamUrl.length() > 0) {
                L.getInstance(this).dumpLog("Thread -  "+Thread.currentThread().getName()+" play next [AnyAudioService] with no refresh");
                playNext(false);

            } else {

                if (!isFetcherInProgress) {
                    L.getInstance(this).dumpLog("Thread -  "+Thread.currentThread().getName()+" init Stream [AnyAudioStream] : re-fetching the uri");
                    // some network issue caused the url fetcher to stop its fetching task
                    initStream(upNextVid, upNextTitle);

                } else {
                    // no cases possible
                }
            }
        }

    }

    private void initStream(String video_id, String title) {

        if (ConnectivityUtils.getInstance(this).isConnectedToNet()) {
            resetPlayer();
            utils.setPlayerState(Constants.PLAYER.PLAYER_STATE_PLAYING);

            //update the notification player view
            notifyNotificationControl(true);
            broadcastActionToPrepareBottomPlayer();
        } else {
            // re-init player
            broadcastActionToPrepareBottomPlayer();
        }

        StreamUrlFetcher
                .getInstance(this)
                .setData(video_id, title)
                .setBroadcastMode(false)
                .setOnStreamUriFetchedListener(new StreamUrlFetcher.OnStreamUriFetchedListener() {
                    @Override
                    public void onUriAvailable(String uri) {
                        L.getInstance(AnyAudioStreamService.this).dumpLog("Thread -  "+Thread.currentThread().getName()+" fetched url :"+uri);
                        Log.d("DuplicateTest", "pre-ready:>next uri available " + uri);
                        //this is first time stream url fetch

                        utils.setStreamUrlFetchedStatus(true);
                        utils.setStreamUrlFetcherInProgress(false);
                        utils.setNextStreamUrl(uri);

                        if (uri.equals(Constants.STREAM_PREPARE_FAILED_URL_FLAG)) {
                            ToastMaker.getInstance(AnyAudioStreamService.this).toast("Something is Wrong !! Please Try Again.");
                            return;
                        }

                        playNext(false);

                    }
                })
                .initProcess();

    }

    private void playNext(boolean refresh) {

        broadcastActionToPrepareBottomPlayer();

        String nextStreamUrl = utils.getNextStreamUrl();

        if (nextStreamUrl.length() > 0) {

            Intent selfIntentForStartNewStream = new Intent(this, AnyAudioStreamService.class);
            selfIntentForStartNewStream.setAction(Constants.ACTION_STREAM_TO_SERVICE_START);
            selfIntentForStartNewStream.putExtra("uri", nextStreamUrl);
            startService(selfIntentForStartNewStream);

            utils.setNextStreamUrl("");

            if (refresh) {
                PlaylistGenerator.getInstance(this).refreshPlaylist();
            }

        } else {
            Log.d("PlaylistNext", " No UpNext Item");
        }

    }

    private void startNotificationService() {

        Intent notificationIntent = new Intent(this, NotificationPlayerService.class);
        notificationIntent.setAction(Constants.ACTIONS.START_FOREGROUND_ACTION);
        startService(notificationIntent);

    }

    private void closeNotification() {

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

    }

    public void broadcastStreamProgresUpdate(String playingAt, String contentLen, String bufferedProgress) {

        Intent intent = new Intent(Constants.ACTION_STREAM_PROGRESS_UPDATE_BROADCAST);
        intent.putExtra(Constants.EXTRAA_STREAM_PROGRESS, playingAt);
        intent.putExtra(Constants.EXTRAA_STREAM_CONTENT_LEN, contentLen);
        intent.putExtra(Constants.EXTRAA_STREAM_BUFFERED_PROGRESS, bufferedProgress);
        String trackLen = getTimeFromMillisecond(Integer.valueOf(contentLen));
        intent.putExtra("formatted_time", trackLen);
        sendBroadcast(intent);

    }

    private String getTimeFromMillisecond(int millis) {

        String hr;
        String min;
        String sec;
        String time;
        int i_hr = (millis / 1000) / 3600;
        int i_min = (millis / 1000) / 60;
        int i_sec = (millis / 1000) % 60;

        if (i_hr == 0) {
            min = (String.valueOf(i_min).length() < 2) ? "0" + i_min : String.valueOf(i_min);
            sec = (String.valueOf(i_sec).length() < 2) ? "0" + i_sec : String.valueOf(i_sec);
            time = min + " : " + sec;
        } else {
            hr = (String.valueOf(i_hr).length() < 2) ? "0" + i_hr : String.valueOf(i_hr);
            min = (String.valueOf(i_min).length() < 2) ? "0" + i_min : String.valueOf(i_min);
            sec = (String.valueOf(i_sec).length() < 2) ? "0" + i_sec : String.valueOf(i_sec);
            time = hr + " : " + min + " : " + sec;
        }

        // Log.d("StreamingHome"," time returned "+time);

        return time;


    }


    public void broadcastActionToPrepareBottomPlayer() {

        Intent intent = new Intent(Constants.ACTION_PREPARE_BOTTOM_PLAYER);
        sendBroadcast(intent);

    }

    private void fetchNextUrl() {

        Log.d("DuplicateTest", "fetchNextUrl() - > ");
        utils.setStreamUrlFetcherInProgress(true);
        // get next play details
        PlaylistItem nxtItem = null;

        if (utils.getAutoPlayMode()) {
            nxtItem = PlaylistGenerator.getInstance(this).getUpNext();
        } else {
            nxtItem = QueueManager.getInstance(this).getUpNext();
            if (nxtItem == null) {
                Toast.makeText(this, "No Item To Play Next.", Toast.LENGTH_LONG).show();
                return;
            }

        }

        String nextVid = nxtItem.videoId;
        String nextVidTitle = nxtItem.title;
        String upNextThumbnailUrl = getImageUrl(nxtItem.youtubeId);
        String upNextArtist = nxtItem.uploader;
        utils.setNextVId(nextVid);
        utils.setNextStreamTitle(nextVidTitle);
        // set data ready for notification and bottom sheets
        utils.setCurrentItemStreamUrl(nextVid);
        utils.setCurrentItemThumbnailUrl(upNextThumbnailUrl);
        utils.setCurrentItemArtist(upNextArtist);
        utils.setCurrentItemTitle(nextVidTitle);

        StreamUrlFetcher
                .getInstance(this)
                .setData(nextVid, nextVidTitle)
                .setBroadcastMode(false)
                .setOnStreamUriFetchedListener(new StreamUrlFetcher.OnStreamUriFetchedListener() {
                    @Override
                    public void onUriAvailable(String uri) {
                        Log.d("DuplicateTest", "pre-ready:>next uri available " + uri);
                        //this is first time stream url fetch
                        utils.setStreamUrlFetcherInProgress(false);
                        utils.setNextStreamUrl(uri);
                    }
                })
                .initProcess();

    }

    private String getImageUrl(String vid) {
        //return "https://i.ytimg.com/vi/kVgKfScL5yk/hqdefault.jpg";
        return "https://i.ytimg.com/vi/" + vid + "/hqdefault.jpg";  // additional query params => ?custom=true&w=240&h=256
    }

    public class PhoneStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (anyPlayer != null && anyPlayer.getPlayWhenReady()) {
                anyPlayer.setPlayWhenReady(false);
                broadcastPlayerStateToBottomPlayer(false);
            }

        }
    }

}
