package any.audio.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.EGLDisplay;
import android.util.Log;

/**
 * Created by Ankit on 11/19/2016.
 */

public class StreamSharedPref {

    private static Context context;
    private static StreamSharedPref mInstance;
    private static final String PREF_NAME = "any_audio_stream";
    private static int MODE = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String KEY_IS_STREAMING = "isStreaming";
    private String KEY_STREAMING_TITLE = "stream_title";
    private String KEY_STREAMING_THUMBNAIL_URL = "streaming_url";
    private String KEY_STREAMING_PROGRESS = "streaming_progress";
    private String KEY_STREAMING_BUFFER = "streaming_buffer";
    private String KEY_IS_STREAMER_PLAYING = "streamer_play_state";
    private String KEY_STREAM_CONTENT_LENGTH = "stream_content_length";
    private String KEY_STREAMING_PLAYING_POSITION = "curr_pos_stream";

    public StreamSharedPref(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, MODE);
        editor = preferences.edit();
    }

    public static StreamSharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamSharedPref(context);
        }
        return mInstance;
    }

    // stream status
    public void setStreamState(boolean isStreaming) {

        Log.d("StreamSharedPref"," setting stream state "+isStreaming);
        editor.putBoolean(KEY_IS_STREAMING, isStreaming);
        editor.commit();
        if(!isStreaming){
            setStreamerPlayState(false);
        }

    }

    public boolean getStreamState() {
        Log.d("StreamSharedPref"," getting stream  state "+ preferences.getBoolean(KEY_IS_STREAMING,false));
        return preferences.getBoolean(KEY_IS_STREAMING, false);
    }

    // stream info
    public void setStreamTitle(String title) {
        //Log.d("StreamSharedPref"," item title "+title);
        editor.putString(KEY_STREAMING_TITLE, title);
        editor.commit();
    }

    public void setStreamThumbnailUrl(String url) {
        //Log.d("StreamSharedPref"," item thumbnail "+url);
        editor.putString(KEY_STREAMING_THUMBNAIL_URL, url);
        editor.commit();
    }

    public void setStreamingProgress(int progress) {
        //Log.d("StreamSharedPref"," stream progress "+progress);
        editor.putInt(KEY_STREAMING_PROGRESS, progress);
        editor.commit();
    }

    public void setStreamigZBuffer(int buffer) {
      //  Log.d("StreamSharedPref"," buffered "+buffer);
        editor.putInt(KEY_STREAMING_BUFFER, buffer);
        editor.commit();
    }

    public int getStreamingProgress() {
        return preferences.getInt(KEY_STREAMING_PROGRESS, 0);
    }

    public int getStreamingBuffer() {
        return preferences.getInt(KEY_STREAMING_BUFFER, 0);
    }

    public void setStreamerPlayState(boolean streamerPlayState) {
    //    Log.d("StreamSharedPref"," play State "+streamerPlayState);
        editor.putBoolean(KEY_IS_STREAMER_PLAYING, streamerPlayState);
        editor.commit();
    }

    public boolean getStreamerPlayState() {
        return preferences.getBoolean(KEY_IS_STREAMER_PLAYING, false);
    }

    public void setStreamContentLength(int length) {
  //      Log.d("StreamSharedPref"," content Len "+length);
        editor.putInt(KEY_STREAM_CONTENT_LENGTH, length);
    }

    public int getStreamContentLength() {

        return preferences.getInt(KEY_STREAM_CONTENT_LENGTH, 0);
    }

    public void setStreamCurrentPlayingPosition(int position) {
//        Log.d("StreamSharedPref"," play position "+position);
        editor.putInt(KEY_STREAMING_PLAYING_POSITION, position);
        editor.commit();
    }

    public int getStreamCurrentPlayingPosition() {
        return preferences.getInt(KEY_STREAMING_PLAYING_POSITION, 0);
    }


    public String getStreamThumbnailUrl() {
        return preferences.getString(KEY_STREAMING_THUMBNAIL_URL, "");
    }

    public String getStreamTitle() {
        return preferences.getString(KEY_STREAMING_TITLE, "");

    }

    public void resetStreamInfo(){

        // title
        setStreamTitle("");
        //url
        setStreamThumbnailUrl("");
        // set play state
        setStreamState(false);
        //current playing

    }
}
