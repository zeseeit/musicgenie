package musicgenie.com.musicgenie;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Ankit on 8/7/2016.
 */
public class MediaStreamer {

    private static Context context;
    private static MediaStreamer mInstance;
    private static boolean isPlaying;
    public MediaPlayer mediaPlayer;
    private boolean atInitial = true;

    public MediaStreamer(Context context) {
        this.context = context;
    }

    public static MediaStreamer getInstance(Context context){
        if(mInstance == null){
            mInstance = new MediaStreamer(context);
        }
        return mInstance;
    }

    public void streamAudio(String audio_source){
        if (!isPlaying) {
            //btn.setBackgroundResource(R.drawable.button_pause);
            if (atInitial)
                new Player()
                        .execute(audio_source);
            else {
                if (!mediaPlayer.isPlaying())
                    mediaPlayer.start();
            }
            isPlaying = true;
        } else {
            //btn.setBackgroundResource(R.drawable.button_play);
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void releaseStreamer(){

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        atInitial = true;
                        isPlaying = false;
                        //btn.setBackgroundResource(R.drawable.button_play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Log.d("Prepared", "//" + result);
            mediaPlayer.start();

            atInitial = false;
        }

        public Player() {
            progress = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

}
