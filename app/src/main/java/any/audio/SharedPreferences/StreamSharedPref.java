package any.audio.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.EGLDisplay;

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
    private String KEY_STREAMING_PLAYING_POSITION ="curr_pos_stream";

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
        editor.putBoolean(KEY_IS_STREAMING, isStreaming);
        editor.commit();
    }

    public boolean getStreamState() {
        return preferences.getBoolean(KEY_IS_STREAMING, false);
    }

    // stream info
    public void setStreamTitle(String title) {
        editor.putString(KEY_STREAMING_TITLE, title);
        editor.commit();
    }

    public void setStreamThumbnailUrl(String url) {
        editor.putString(KEY_STREAMING_THUMBNAIL_URL, url);
        editor.commit();
    }

    public void setStreamingProgress(int progress) {
        editor.putInt(KEY_STREAMING_PROGRESS, progress);
        editor.commit();
    }

    public void setStreamigZBuffer(int buffer) {
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
        editor.putBoolean(KEY_IS_STREAMER_PLAYING, streamerPlayState);
        editor.commit();
    }

    public boolean getStreamerPlayState() {
        return preferences.getBoolean(KEY_IS_STREAMER_PLAYING, false);
    }

    public void setStreamContentLength(int length){
        editor.putInt(KEY_STREAM_CONTENT_LENGTH,length);
    }

    public int getStreamContentLength(){
        return preferences.getInt(KEY_STREAM_CONTENT_LENGTH,0);
    }

    public void setStreamCurrentPlayingPosition(int position){
        editor.putInt(KEY_STREAMING_PLAYING_POSITION,position);
        editor.commit();
    }

    public int getStreamCurrentPlayingPosition(){
        return preferences.getInt(KEY_STREAMING_PLAYING_POSITION,0);
    }


}
