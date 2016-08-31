package musicgenie.com.musicgenie;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.security.MessageDigest;

/**
 * Created by Ankit on 8/30/2016.
 */
public class MusicGenieMediaPlayer extends MediaPlayer {

    private static final String TAG = "MusicGenieMediaPlayer";
    private static Context context;
    private static MusicGenieMediaPlayer mInstance;
    private static MediaPlayer player;

    public MusicGenieMediaPlayer(Context context) {
        this.context = context;
    }

    public static MusicGenieMediaPlayer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MusicGenieMediaPlayer(context);
        }
        return mInstance;
    }

    public MusicGenieMediaPlayer setURI(String uri){
        Log.d(TAG, "setURI " + context);
        this.player = MediaPlayer.create(context, Uri.parse(uri));
        return this;
    }

    public MediaPlayer getPlayer(){
        return this.player;
    }

    public void stopPlayer(){
        if(player.isPlaying()){
            player.reset();
            player.stop();
        }
    }


}
