package any.audio.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import any.audio.Config.Constants;
import any.audio.Config.URLS;
import any.audio.Network.VolleyUtils;

/**
 * Created by Ankit on 8/26/2016.
 */
public class StreamUrlFetcher {

    private static final String TAG = "StreamUrlFetcherer";
    private static final int SOCKET_CONNECT_TIMEOUT = 60 * 1000; // 1 min
    private static Context context;
    private static StreamUrlFetcher mInstance;
    private String vid;
    private String file;
    private OnStreamUriFetchedListener onStreamUriFetchedListener;
    private int SERVER_TIMEOUT_LIMIT = 60 * 1000;       // 1 min
    private String STREAM_URL_REQUEST_TAG_VOLLEY = "volley_request_tag";
    private boolean doBroadcast = false;


    public StreamUrlFetcher(Context context) {
        StreamUrlFetcher.context = context;
    }

    public static StreamUrlFetcher getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamUrlFetcher(context);
        }
        return mInstance;
    }

    public StreamUrlFetcher setOnStreamUriFetchedListener(OnStreamUriFetchedListener listener) {
        this.onStreamUriFetchedListener = listener;
        return this;
    }

    public StreamUrlFetcher setData(String v_id, String file_name) {
        this.vid = v_id;
        this.file = file_name;
        return this;
    }

    public void initProcess() {

        requestStreamUrlUsingVolley(this.vid);
        L.m("DuplicateTest", " Thread Started: stream uri fetch");

    }

    private void broadcastURI(String t_url, String file) {

        Intent intent = new Intent(Constants.ACTION_STREAM_URL_FETCHED);
        intent.putExtra(Constants.EXTRAA_URI, t_url);
        intent.putExtra(Constants.EXTRAA_STREAM_FILE, file);
        context.sendBroadcast(intent);

    }

    private void log(String s) {
        Log.d(TAG, "log " + s);
    }

    public StreamUrlFetcher setBroadcastMode(boolean shouldBroadcast) {
        this.doBroadcast = shouldBroadcast;
        return this;
    }

    public interface OnStreamUriFetchedListener {
        void onUriAvailable(String uri);
    }

    private void requestStreamUrlUsingVolley(final String v_id) {

        try {
            VolleyUtils.getInstance().cancelPendingRequests(STREAM_URL_REQUEST_TAG_VOLLEY);
            Log.d("DuplicateTest", " Cancellig Pending Volley Requests For Stream Url");

        } catch (Exception e) {
            Log.d("DuplicateTest", " Attempt To Cancel NoRequests");
        }

        final String streaming_url_pref = URLS.URL_SERVER_ROOT;
        String url = URLS.URL_SERVER_ROOT + "api/v1/stream?url=" + v_id;
        Log.d("DuplicateTest"," requesting url for stream on:"+url) ;
        StringRequest updateCheckReq = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        Log.d("DuplicateTest"," response "+result) ;

                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(result);
                            if (obj.getInt("status") == 200) {

                                if(onStreamUriFetchedListener!=null){   // For Auto-next
                                    onStreamUriFetchedListener.onUriAvailable(streaming_url_pref + obj.getString("url"));
                                }

                                if(doBroadcast)         // for Manual play
                                   broadcastURI(streaming_url_pref + obj.getString("url"), file);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            broadcastURI(Constants.STREAM_PREPARE_FAILED_URL_FLAG, file);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("DuplicateTest", " VolleyError " + volleyError);
                        broadcastURI(Constants.STREAM_PREPARE_FAILED_URL_FLAG, file);
                    }
                });

        updateCheckReq.setRetryPolicy(new DefaultRetryPolicy(
                SERVER_TIMEOUT_LIMIT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(updateCheckReq, STREAM_URL_REQUEST_TAG_VOLLEY, context);

    }

}
