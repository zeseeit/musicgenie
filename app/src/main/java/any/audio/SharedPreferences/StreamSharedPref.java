package any.audio.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import any.audio.Config.Constants;

/**
 * Created by Ankit on 11/19/2016.
 */

public class StreamSharedPref {

    private static final String PREF_NAME = "any_audio_stream";
    private static Context context;
    private static StreamSharedPref mInstance;
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
    private String KEY_IS_STREAM_URL_FETCHED = "stream_url_fetched";
    private String lastStreamThumbnailUrl;
    private String lastStreamTitle;

    public StreamSharedPref(Context context) {
        StreamSharedPref.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, MODE);
        editor = preferences.edit();
    }

    public static StreamSharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamSharedPref(context);
        }
        return mInstance;
    }

    public boolean getStreamState() {
        Log.d("StreamSharedPref", " getting stream  state " + preferences.getBoolean(KEY_IS_STREAMING, false));
        return preferences.getBoolean(KEY_IS_STREAMING, false);
    }

    // stream status
    public void setStreamState(boolean isStreaming) {

        Log.d("StreamSharedPref", " setting stream state " + isStreaming);
        editor.putBoolean(KEY_IS_STREAMING, isStreaming);
        editor.commit();

        if (!isStreaming) {
            setStreamerPlayState(false);
        }

    }

    public boolean getStreamUrlFetchedStatus() {
        Log.d("StreamSharedPref", " getting fetched state " + preferences.getBoolean(KEY_IS_STREAM_URL_FETCHED, false));
        return preferences.getBoolean(KEY_IS_STREAM_URL_FETCHED, false);
    }

    // stream status
    public void setStreamUrlFetchedStatus(boolean fetched) {

        Log.d("StreamSharedPref", " setting fetched state " + fetched);
        editor.putBoolean(KEY_IS_STREAM_URL_FETCHED, fetched);
        editor.commit();

    }

    public int getStreamingProgress() {
        return preferences.getInt(KEY_STREAMING_PROGRESS, 0);
    }

    public int getStreamingBuffer() {
        return preferences.getInt(KEY_STREAMING_BUFFER, 0);
    }

    public boolean getStreamerPlayState() {
        return preferences.getBoolean(KEY_IS_STREAMER_PLAYING, false);
    }

    public void setStreamerPlayState(boolean streamerPlayState) {
        //    Log.d("StreamSharedPref"," play State "+streamerPlayState);
        editor.putBoolean(KEY_IS_STREAMER_PLAYING, streamerPlayState);
        editor.commit();
    }

    public int getStreamContentLength() {

        return preferences.getInt(KEY_STREAM_CONTENT_LENGTH, 0);
    }

    public int getStreamCurrentPlayingPosition() {
        return preferences.getInt(KEY_STREAMING_PLAYING_POSITION, 0);
    }

    public String getStreamThumbnailUrl() {
        return preferences.getString(KEY_STREAMING_THUMBNAIL_URL, "");
    }

    public void setStreamThumbnailUrl(String url) {
        //Log.d("StreamSharedPref"," item thumbnail "+url);
        editor.putString(KEY_STREAMING_THUMBNAIL_URL, url);
        editor.commit();
    }

    public String getStreamTitle() {
        return preferences.getString(KEY_STREAMING_TITLE, "");

    }

    // stream info
    public void setStreamTitle(String title) {
        //Log.d("StreamSharedPref"," item title "+title);
        editor.putString(KEY_STREAMING_TITLE, title);
        editor.commit();
    }

    public void resetStreamInfo() {

        // title
        setStreamTitle("");
        //url
        setStreamThumbnailUrl("");
        // set play state
        setStreamState(false);
        //current playing

    }

    public void setStreamSubTitle(String subTitle) {
        editor.putString(Constants.PLAYER.AUDIO_SUBTITLE, subTitle);
        editor.commit();
    }

    public String getStreamSubTitle() {
        return preferences.getString(Constants.PLAYER.AUDIO_SUBTITLE, "");
    }

    public void setStreamContentLength(String trackLen) {
        editor.putString(Constants.KEY_STREAM_CONTENT_LEN,trackLen);
        editor.commit();
    }

    public String getStreamingContentLength(){
        return preferences.getString(Constants.KEY_STREAM_CONTENT_LEN,"00:00");
    }

    public void setLastStreamThumbnailUrl(String lastStreamThumbnailUrl) {
        editor.putString("lastStreamThumb",lastStreamThumbnailUrl);
        editor.commit();
    }

    public void setLastStreamTitle(String lastStreamTitle) {
        editor.putString("lastStreamTitle",lastStreamTitle);
        editor.commit();
    }

    public String getLastStreamThumbnailUrl(){
        return preferences.getString("lastStremThumb","");
    }

    public String getLastStreamTitle(){
        return preferences.getString("lastStreamTitle","");
    }

    public void setLastStreamVid(String v_id) {
        editor.putString("lstsvid",v_id);
        editor.commit();
    }

    public String getLastStreamVid() {
        return preferences.getString("lstsvid","");
    }

}
