package simple.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import simple.musicgenie.L;

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
        L.m("MusicGeniePlayer", "setURI " + context);
        this.player = MediaPlayer.create(context, Uri.parse(uri));
        return this;
    }

    public MediaPlayer getPlayer(){
        return this.player;
    }

    public void stopPlayer(){
        if(this.player.isPlaying()){
            this.player.reset();
            this.player.release();
 //           this.player.stop();
        }
    }


}
