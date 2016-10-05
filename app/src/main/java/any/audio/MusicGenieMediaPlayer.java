package any.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

public class MusicGenieMediaPlayer extends Thread {

    private static final String TAG = "MusicGenieMediaPlayer";
    private static Context context;
    private static MusicGenieMediaPlayer mInstance;
    private static MediaPlayer player;
    private ExoPlayer exoPlayer;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private MediaCodecAudioTrackRenderer audioRenderer;
    private Uri mUri;
    private Handler mHandler;
    private Handler mUIHandler;

    public MusicGenieMediaPlayer(Context context,String uri , Handler handler) {
        MusicGenieMediaPlayer.context = context;
        mUri = Uri.parse(uri);
        mUIHandler = handler;
    }

    public Handler getPlayerThreadHandler(){        // handler to stop player
        return mHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

            }
        };

        exoPlayer = ExoPlayer.Factory.newInstance(1);
        // Settings for exoPlayer
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(context, "AnyAudio");
        DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);

        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                mUri,
                dataSource,
                allocator,
                BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);

        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        // Prepare ExoPlayer
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean b, int i) {
                L.m("ExoPlayer","state "+i);
            }

            @Override
            public void onPlayWhenReadyCommitted() {
                L.m("ExoPlayer","commited ");
            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {
                L.m("ExoPlayer","error "+e);
            }
        });
        while(exoPlayer!=null && exoPlayer.getPlayWhenReady()){
            L.m("ExoPlayer"," playing "+exoPlayer.getCurrentPosition());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




    }

    public void stopPlayer(){
        if (player.isPlaying()) {
            player.reset();
            player.release();
        }
    }


}
