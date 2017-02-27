package any.audio.Managers;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Database.DbHelper;
import any.audio.Models.ItemModel;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.L;
import any.audio.Models.ExploreItemModel;
import any.audio.helpers.Segmentor;
import any.audio.Config.URLS;
import any.audio.Network.VolleyUtils;
import any.audio.helpers.TextFormatter;

public class CloudManager {

    private static final int SERVER_TIMEOUT_LIMIT = 20000;
    private static final String TIME_SINCE_UPLOADED_LEFT_VACCANT = "";
    private static Context context;
    private static CloudManager mInstance;
    private DbHelper dbHelper;
    private boolean doReset;

    public CloudManager(Context context) {
        CloudManager.context = context;
        this.dbHelper = DbHelper.getInstance(context);
    }

    public static CloudManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CloudManager(context);
        }
        return mInstance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Trending Section
    ///////////////////////////////////////////////////////////////////////////

    public void lazyRequestTrending() {

        // clear out back stored data
        dbHelper.resetTrendingList();
        doReset = true;
        requestSupportedPlaylist();

    }

    /**
     * @param type section for trending e.g. pop , rock etc.
     */
    private void requestTrendingType(String type) {
        int count = 25; // max 25
        final String url = URLS.URL_TRENDING_API + "?type=" + type + "&number=" + count + "&offset=0";

        StringRequest trendingReq = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        handleTrending(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        trendingReq.setRetryPolicy(new DefaultRetryPolicy(
                SERVER_TIMEOUT_LIMIT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(trendingReq, "trendingReq", context);

    }

    /**
     * @param response json response for trending items
     */
    private void handleTrending(String response) {

        ExploreItemModel trendingResult = null;
        ItemModel item;

        try {

            JSONObject resObj = new JSONObject(response);
            String sections = resObj.getJSONObject("metadata").getString("type");
            ArrayList<String> playlists = new Segmentor().getParts(sections, ',');
            ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();


            JSONObject resultsSubObject = resObj.getJSONObject("results");

            for (int playlistNum = 0; playlistNum < playlists.size(); playlistNum++) {

                JSONArray typeArray = resultsSubObject.getJSONArray(playlists.get(playlistNum));

                itemModelArrayList = new ArrayList<>();

                for (int j = 0; j < typeArray.length(); j++) {      // j represent current item model inside i`th section model

                    JSONObject songObj = (JSONObject) typeArray.get(j);
                    String enc_v_id = songObj.getString("get_url").substring(14);
                    String _t_url = songObj.getString("thumb");
                    _t_url = _t_url.substring(0,_t_url.length()-6)+Constants.THUMBNAIL_VERSION_MEDIUM;
                    String title = TextFormatter.getInstance(context).reformat(songObj.getString("title"));
                    item = new ItemModel(
                            title,
                            songObj.getString("length"),
                            songObj.getString("uploader"),
                            _t_url,
                            enc_v_id,
                            TIME_SINCE_UPLOADED_LEFT_VACCANT,
                            songObj.getString("views"),
                            playlists.get(playlistNum));
                    itemModelArrayList.add(item);
                }


                trendingResult = new ExploreItemModel(playlists.get(playlistNum), itemModelArrayList);
                dbHelper.addTrendingList(trendingResult, doReset);
                doReset = false;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        L.m("CM(test)", " Trending Type " + trendingResult.sectionTitle);

    }

    private void requestSupportedPlaylist() {

        final String url = URLS.URL_SUPPORTED_PLAYLIST;

        StringRequest playlistReq = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        handleSupportedPlaylists(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        playlistReq.setRetryPolicy(new DefaultRetryPolicy(
                SERVER_TIMEOUT_LIMIT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(playlistReq, "playReq", context);
    }

    private void handleSupportedPlaylists(String response) {

        String playlistsWithComma = "";
        try {
            JSONObject object = new JSONObject(response);

            int playlistCount = Integer.parseInt(object.getJSONObject("metadata").getString("count"));

            JSONArray playlistJsonArray = object.getJSONArray("results");

            for (int i = 0; i < playlistCount; i++) {

                if (i == 0) {
                    playlistsWithComma += playlistJsonArray.getJSONObject(i).getString("playlist");
                } else {
                    playlistsWithComma += "," + playlistJsonArray.getJSONObject(i).getString("playlist");
                }

            }

            requestTrendingType(playlistsWithComma);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //L.m("CM(test) ","Supported Playlist");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Results Section
    ///////////////////////////////////////////////////////////////////////////

    public void requestSearch(String term) {

        String url = URLS.URL_SEARCH_RESULT + "q=" + URLEncoder.encode(term);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleResultResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                //          L.m("CM", "error " + volleyError);

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(SERVER_TIMEOUT_LIMIT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(request, "resultReq", context);
    }

    private void handleResultResponse(String response) {

       // L.m("CM", "got result " + response);
        ArrayList<ItemModel> songs = new ArrayList<>();

        try {
            JSONObject rootObj = new JSONObject(response);
            int results_count = rootObj.getJSONObject("metadata").getInt("count");

            JSONArray results = rootObj.getJSONArray("results");
            for (int i = 0; i < results_count; i++) {
                String enc_v_id = results.getJSONObject(i).getString("get_url").substring(14);
                String _t_url = results.getJSONObject(i).getString("thumb");
                _t_url = _t_url.substring(0,_t_url.length()-6)+ Constants.THUMBNAIL_VERSION_MEDIUM;
                String title = TextFormatter.getInstance(context).reformat(results.getJSONObject(i).getString("title"));
                songs.add(new ItemModel(title,
                        results.getJSONObject(i).getString("length"),
                        results.getJSONObject(i).getString("uploader"),
                        _t_url,
                        enc_v_id,
                        results.getJSONObject(i).getString("time"),
                        results.getJSONObject(i).getString("views")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // database write test
        SharedPrefrenceUtils.getInstance(context).newSearch(false);
        dbHelper.addResultsList(new ExploreItemModel("Results", songs));

    }

}
