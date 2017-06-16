package any.audio.control;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import any.audio.data.DataServer;
import any.audio.interfaces.PlaylistCallback;
import any.audio.interfaces.StreamUriCallback;
import any.audio.models.AnyAudioMedia;
import any.audio.models.AutoPlaylistModel;
import any.audio.preference.AnyAudioPreferenceManager;

/**
 * Created by Ankit on 13-06-2017.
 */

public class PlaylistManager implements PlaylistCallback, StreamUriCallback {

    public static final int STREAM_URI_MODE_PRE_READY = 101;
    public static final int STREAM_URI_MODE_IMMEDIATE = 102;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STREAM_URI_MODE_PRE_READY,STREAM_URI_MODE_IMMEDIATE})
    @interface StreamUriMode{}

    private boolean mLastPlaylistFetchSuccess = false;
    private static PlaylistManager mInstance;
    private String mPreReadyNextStreamUri = "";
    private String mNextHashedUrl = "";
    private AnyAudioPreferenceManager preferenceManager;
    private List<AnyAudioMedia> playlist;

    @StreamUriMode
    int mStreamUriMode = STREAM_URI_MODE_PRE_READY;

    /*
    *   PlaylistManager contains two type of NextItems
    *   ImmediateNextStreamUri - for auto-next play
    *   NextVideoId - for next item when user presses Next>
    * */

    public static PlaylistManager getInstance() {
        if (mInstance == null) {
            mInstance = new PlaylistManager();
        }
        return mInstance;
    }

    public PlaylistManager() {

    }

    public void init() {
        /*
        *   Initializes the playlist from saved cache or empty list
        *
        * */

        preferenceManager = AnyAudioPreferenceManager.getInstance();
        playlist = preferenceManager.getPlaylist();

    }

    public void preparePlaylistFor(String currentHashedUrl) {
        /*
        *   It fetches the playlist from server based on current 'videoId'
        *
        * */

        DataServer.requestPlaylist(currentHashedUrl,this);

    }

    public void preparePreReadyNextStreamUri(String hashedUrl){
        mStreamUriMode = STREAM_URI_MODE_PRE_READY;
        DataServer.requestStreamUri(hashedUrl,this);
    }

    public void prepareImmediateNextStreamUri(String hashedUrl){
        mStreamUriMode = STREAM_URI_MODE_IMMEDIATE;
        DataServer.requestStreamUri(hashedUrl,this);
    }

    public String getImmediateNextStreamUri(){
        return mPreReadyNextStreamUri;
    }

    private void savePlaylistToCache(List<AnyAudioMedia> anyAudioMedias) {
        preferenceManager.savePlaylist(anyAudioMedias);
    }

    private ArrayList<AnyAudioMedia> getPlaylistFromCache() {
        return preferenceManager.getPlaylist();
    }

    @Override
    public void onPlaylistFetchError() {
        mLastPlaylistFetchSuccess = false;
        //todo: notify the control section for playlist fetch error
    }

    @Override
    public void onPlaylistFetched(AutoPlaylistModel model) {
        mLastPlaylistFetchSuccess = true;
        playlist = model.getResults();
        savePlaylistToCache(playlist);
    }


    @Override
    public void onStreamUriFetchFailed() {
        //todo: notify the control panel for failed uri fetch
    }

    @Override
    public void onStreamUriFetched(String uri) {
        if(mStreamUriMode==STREAM_URI_MODE_IMMEDIATE){
            //todo: notify the control with the uri  received
        }else{
            // save the uri to var for future request
            mPreReadyNextStreamUri = uri;
        }
    }
}
