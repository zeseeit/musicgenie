package any.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MusicGenieMediaPlayer extends MediaPlayer {

    private static final String TAG = "MusicGenieMediaPlayer";
    private static Context context;
    private static MusicGenieMediaPlayer mInstance;
    private static MediaPlayer player;

    public MusicGenieMediaPlayer(Context context) {
        MusicGenieMediaPlayer.context = context;
    }

    public static MusicGenieMediaPlayer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MusicGenieMediaPlayer(context);
        }
        return mInstance;
    }

    public MusicGenieMediaPlayer setURI(String uri){
        L.m("MusicGeniePlayer", "setURI " + context);
        player = MediaPlayer.create(context, Uri.parse(uri));
        return this;
    }

    public MediaPlayer getPlayer(){
        return player;
    }

    public void stopPlayer(){
        if (player.isPlaying()) {
            player.reset();
            player.release();
 //           this.player.stop();
        }
    }


}
