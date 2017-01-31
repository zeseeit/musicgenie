package any.audio.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import any.audio.Config.Constants;
import any.audio.Models.ItemModel;
import any.audio.Models.PlaylistItem;
import any.audio.Network.VolleyUtils;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 1/12/2017.
 */

public class PlaylistGenerator {


    ArrayList<PlaylistItem> playlistItems;
    ArrayList<String> videoIds;
    SharedPrefrenceUtils utils;
    private static Context context;
    private static PlaylistGenerator mInstance;
    private ArrayList<String> videoTitles;
    private ArrayList<String> youtubeIds;
    private ArrayList<String> uploadersList;
    private PlaylistGenerateListener playlistGeneraterListener;


    public PlaylistGenerator(Context context) {
        this.context = context;
        utils = SharedPrefrenceUtils.getInstance(context);
    }

    public static PlaylistGenerator getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlaylistGenerator(context);
        }
        return mInstance;
    }

    public void preparePlaylist(String currentItem) {
        Log.d("PlaylistGen","preparing playlist for "+currentItem);
        resetPlaylist(currentItem);
    }

    public void resetPlaylist(String currentVideoId) {

        sendPreparingFeedback();
        deletePlaylist();
        fetchVideoIds(currentVideoId);   //by default 10 items

    }

    public void deletePlaylist() {

        Log.d("PlaylistGen"," deleting old playlist");
        utils.setPlaylistVideoId("");
        utils.setPlaylistYoutubeId("");
        utils.setPlaylistVideoTitles("");
        utils.setPlaylistUploaders("");

    }

    private void fetchVideoIds(final String currentVideoId) {

        String _url = Constants.SERVER_URL + "/api/v1/suggest?url=" + currentVideoId;
        StringRequest playlistFetchReq = new StringRequest(Request.Method.GET, _url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        parsePlaylist(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        playlistFetchReq.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d("PlaylistGen"," requesting playlist : "+_url);

        VolleyUtils.getInstance().addToRequestQueue(playlistFetchReq, "playlistReq", context);

    }

    private void parsePlaylist(String response) {

        //Log.d("PlaylistGen"," response : "+response);

        playlistItems = new ArrayList<>();

        try {

            JSONObject rootObj = new JSONObject(response);
            int results_count = rootObj.getJSONObject("metadate").getInt("count");

            JSONArray results = rootObj.getJSONArray("results");
            for (int i = 0; i < results_count; i++) {

                String v_id = results.getJSONObject(i).getString("get_url").substring(14);
                String y_id = results.getJSONObject(i).getString("id");
                String _next_title = results.getJSONObject(i).getString("title");
                String uploader = results.getJSONObject(i).getString("uploader");

                playlistItems.add(new PlaylistItem(v_id, y_id, _next_title, uploader));

                String titles = utils.getPlaylistVideoTitles();
                utils.setPlaylistVideoTitles(titles + _next_title + "#");

                String ids = utils.getPlaylistVideoId();
                utils.setPlaylistVideoId(ids + v_id + "#");    // append the ids


                String y_ids = utils.getPlaylistYoutubeId();
                utils.setPlaylistYoutubeId(y_ids + y_id + "#");    // append the ids

                String uploaders = utils.getPlaylistUploaders();
                utils.setPlaylistUploaders(uploaders + uploader + "#");    // append the ids


            }

            Log.d("PlaylistGen", " final youtubeIds:=" + utils.getPlaylistYoutubeId());
            Log.d("PlaylistGen", " final videoIds:=" + utils.getPlaylistVideoId());
            Log.d("PlaylistGen", " final titles:=" + utils.getPlaylistVideoTitles());
            Log.d("PlaylistGen", " final uploaders :=" + utils.getPlaylistUploaders());


            sendGeneratedFeedback();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendPreparingFeedback(){

        if(playlistGeneraterListener!=null){
            playlistGeneraterListener.onPlaylistPreparing();
        }

    }

    private void sendGeneratedFeedback() {

        if(playlistGeneraterListener!=null){
            playlistGeneraterListener.onPlaylistPrepared(getPlaylistItems(false));
        }

    }

    public PlaylistItem getUpNext() {

        return getPlaylistItems(true).get(0);

    }

    public ArrayList<PlaylistItem> getPlaylistItems(boolean refresh) {

        ArrayList<PlaylistItem> playlistItems = new ArrayList<>();

        PlaylistItem upNextItem = null;
        videoTitles = new Segmentor().getParts(utils.getPlaylistVideoTitles(), '#');
        videoIds = new Segmentor().getParts(utils.getPlaylistVideoId(), '#');
        youtubeIds = new Segmentor().getParts(utils.getPlaylistYoutubeId(), '#');
        uploadersList = new Segmentor().getParts(utils.getPlaylistUploaders(), '#');

        for (int i = 0; i < videoIds.size(); i++) {
            upNextItem = new PlaylistItem(videoIds.get(i), youtubeIds.get(i), videoTitles.get(i), uploadersList.get(i));
            playlistItems.add(upNextItem);
        }

        if(refresh)
            resetPlaylist(videoIds.get(0)); // use top item for next refresh

        return playlistItems;
    }

    public void setPlaylistGenerationListener(PlaylistGenerateListener listener){
        this.playlistGeneraterListener = listener;
    }

    public interface PlaylistGenerateListener{

        void onPlaylistPreparing();
        void onPlaylistPrepared(ArrayList<PlaylistItem> items);

    }

}
