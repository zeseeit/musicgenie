package any.audio.helpers;

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

import any.audio.Config.Constants;
import any.audio.Models.StreamMessageObjectModel;

public class MusicGenieMediaPlayer extends Thread {

    private static final String TAG = "MusicGenieMediaPlayer";
    private static Context context;
    private static MusicGenieMediaPlayer mInstance;
    private static MediaPlayer player;
    private ExoPlayer exoPlayer;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private Uri mUri;
    private Handler mUIHandler;

    public MusicGenieMediaPlayer(Context context,String uri , Handler handler) {
        MusicGenieMediaPlayer.context = context;
        mUri = Uri.parse(uri);
        mUIHandler = handler;

    }

    @Override
    public void run() {
        Looper.prepare();
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.arg1 == Constants.FLAG_STOP_MEDIA_PLAYER) {
                    L.m("ExoPlayer", "flag to cancel stream");
                    stopPlayer();
                } else if (msg.arg1 == Constants.FLAG_CANCEL_STREAM) {
                    L.m("ExoPlayer", "flag to cancel stream");
                } else {
                    L.m("ExoPlayer", "seek Order to " + msg.arg1);
                    seekTo((long) msg.arg1);
                }

            }
        };

        //send Handler

        Message handlerPass = Message.obtain();
        handlerPass.arg1=Constants.FLAG_PASSING_HANDLER_REF;
        handlerPass.obj = mHandler;
        mUIHandler.sendMessage(handlerPass);

        useExoplayer();
    }

    private void useNativeMediaPlayer(){

        Uri mUri = Uri.parse("https://redirector.googlevideo.com/videoplayback?ip=2405%3A204%3Aa108%3Ad941%3Ac1a6%3Aee19%3Ab91e%3A2212&requiressl=yes&lmt=1475255327003268&itag=43&id=o-AEwHFbPb9W4VvmStnHurqdnMuVo-XQif-0oAXbXuVoed&dur=0.000&pcm2cms=yes&source=youtube&upn=gA6OYhdD4fs&mime=video%2Fwebm&ratebypass=yes&ipbits=0&initcwndbps=320000&expire=1475706999&gcr=in&sparams=dur%2Cei%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&key=yt6&mn=sn-gwpa-qxae&mm=31&ms=au&ei=Fyz1V4KYDImEoAPRwLjwCQ&pl=36&mv=m&mt=1475684495&signature=A8B6FD2BC32B05B17E0C62DA1E36967B72E84E3A.3515AE79C436E6A1B1A42BC9E3E14892C5C2C95A&title=%E0%A4%B2%E0%A4%B2%E0%A4%95%E0%A5%80+%E0%A4%9A%E0%A5%81%E0%A4%A8%E0%A4%B0%E0%A4%BF%E0%A4%AF%E0%A4%BE+%E0%A4%93%E0%A5%9D+%E0%A4%95%E0%A5%87+-+Pawan+Singh+%26+Akshara+Singh+-+Dular+Devi+Maiya+Ke+-+Bhojpuri+Devi+Geet+2016");
        MediaPlayer mediaPlayer = MediaPlayer.create(context,mUri);
        mediaPlayer.start();

        if(mediaPlayer !=null){
            while(mediaPlayer.isPlaying()){

                StreamMessageObjectModel objectModel = new StreamMessageObjectModel(
                        mediaPlayer.getCurrentPosition(),
                        mediaPlayer.getDuration(),
                        0);

                Message msg = Message.obtain();
                msg.obj = objectModel;
                mUIHandler.sendMessage(msg);

            }
        }

    }

    private void useExoplayer(){

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

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        // Prepare ExoPlayer
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean b, int i) {
                L.m("ExoPlayer", "state " + i);
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

            StreamMessageObjectModel objectModel = new StreamMessageObjectModel(
                    exoPlayer.getCurrentPosition(),
                    exoPlayer.getDuration(),
                    0);

            Message msg = Message.obtain();
            msg.obj = objectModel;
            mUIHandler.sendMessage(msg);


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlayer(){

        exoPlayer.stop();
        exoPlayer.release();
    }

    private void seekTo(long seekToPosition){

        exoPlayer.seekTo(seekToPosition);

    }

}
