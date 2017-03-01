package any.audio.helpers;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import any.audio.Config.Constants;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

import static any.audio.Adapters.DownloadedItemsAdapter.isPlaying;
import static any.audio.services.AnyAudioStreamService.anyPlayer;

/**
 * Created by Ankit on 2/12/2017.
 */

public class AnyAudioMediaPlayer {

    private static AnyAudioMediaPlayer mInstance;
    private static TextView btn;
    private final Context context;
    private String playBtnString = "\uE039";
    private String audioPath;
    private static MediaPlayer mp;

    public AnyAudioMediaPlayer(Context context) {
        mp = new MediaPlayer();
        this.context = context;
    }

    public static AnyAudioMediaPlayer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AnyAudioMediaPlayer(context);
        }else {

            isPlaying = false;
            btn.setText("\uE039");
            if(mp!=null)
                mp.stop();
        }
        return mInstance;
    }

    public AnyAudioMediaPlayer setViewCallback(TextView viewCallback){
        btn = viewCallback;
        return this;
    }

    public AnyAudioMediaPlayer setAudioPath(String path){
        this.audioPath = path;
        Log.d("AnyAudioPlayer"," path "+audioPath);
        return this;
    }

    public void stopPlaying(){

        if(mp!=null){
            isPlaying = false;
            mp.stop();
        }
        mp=null;
        System.gc();
    }


    private void updateBottomPlayerState() {

        Intent stateIntent = new Intent();
        stateIntent.setAction(Constants.ACTIONS.PLAY_TO_PAUSE);
        Log.i("NotificationPlayerState", " sending action=:" + stateIntent.getAction());
        context.sendBroadcast(stateIntent);

    }

    public void startPlay(){

        if(anyPlayer!=null){
            SharedPrefrenceUtils.getInstance(context).setPlayerState(Constants.PLAYER.PLAYER_STATE_PAUSED);
            anyPlayer.setPlayWhenReady(false);
            updateBottomPlayerState();
        }

        mp=new MediaPlayer();
        try{
            mp.setDataSource(audioPath);//Write your location here

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    btn.setText(playBtnString);
                    isPlaying = false;
                    return true;
                }
            });

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    isPlaying = false;
                    btn.setText(playBtnString);
                }
            });

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("AnyAudioPlayer"," prepared");
                    mp.start();
                }
            });

            mp.prepareAsync();

        }catch(Exception e){e.printStackTrace();}

    }

}
