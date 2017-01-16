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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import any.audio.Activity.UpdateThemedActivity;
import any.audio.Config.Constants;
import any.audio.Network.VolleyUtils;

/**
 * Created by Ankit on 8/26/2016.
 */
public class MusicStreamer {

    private static final String TAG = "MusicStreamer";
    private static final int SOCKET_CONNECT_TIMEOUT = 1 * 60 * 1000; // 1 min
    private static Context context;
    private static MusicStreamer mInstance;
    private String vid;
    private String file;
    private OnStreamUriFetchedListener onStreamUriFetchedListener;
    private int SERVER_TIMEOUT_LIMIT = 1 * 60 * 1000;       // 1 min
    private String STREAM_URL_REQUEST_TAG_VOLLEY = "volley_request_tag";
    private boolean doBroadcast = false;


    public MusicStreamer(Context context) {
        MusicStreamer.context = context;
    }

    public static MusicStreamer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MusicStreamer(context);
        }
        return mInstance;
    }

    public MusicStreamer setOnStreamUriFetchedListener(OnStreamUriFetchedListener listener) {
        this.onStreamUriFetchedListener = listener;
        return this;
    }

    public MusicStreamer setData(String v_id, String file_name) {
        this.vid = v_id;
        this.file = file_name;
        return this;
    }

    public void initProcess() {
        //new StreamThread(this.vid, this.file).start();
        requestStreamUrlUsingVolley(this.vid);
        L.m("MusicStream", " Thread Started: stream uri fetch");
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

    public MusicStreamer setBroadcastMode(boolean shouldBroadcast) {
        this.doBroadcast = shouldBroadcast;
        return this;
    }

    public interface OnStreamUriFetchedListener {
        void onUriAvailable(String uri);
    }

//    private class StreamThread extends Thread {
//
//        private String v_id;
//        private String file;
//
//        public StreamThread(String v_id, String file) {
//            this.v_id = v_id;
//            this.file = file;
//        }
//
//        @Override
//        public void run() {
//
//            final String t_v_id = this.v_id;
//            String streaming_url_pref = Constants.SERVER_URL;
//
//            try {
//
//                String _url = Constants.SERVER_URL + "/api/v1/stream?url=" + t_v_id;
//                L.m("MusicStream", " Requesting for stream url - req on -" + _url);
//                URL u = new URL(_url);
//                URLConnection dconnection = u.openConnection();
//                dconnection.setReadTimeout(SOCKET_CONNECT_TIMEOUT);
//                dconnection.setConnectTimeout(SOCKET_CONNECT_TIMEOUT);
//                dconnection.connect();
//                StringBuilder result = new StringBuilder();
//                InputStream in = new BufferedInputStream(dconnection.getInputStream());
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//                try {
//                    JSONObject obj = new JSONObject(result.toString());
//                    if (obj.getInt("status") == 200) {
//                        streaming_url_pref += obj.getString("url");
//                        L.m("MusicStream", "stream Url " + streaming_url_pref);
//                        onStreamUriFetchedListener.onUriAvailable(streaming_url_pref);
//
//                        if(doBroadcast)
//                            broadcastURI(streaming_url_pref, file);
//
//                    } else {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } catch (MalformedURLException e) {
//                //                   log("URL exception " + e);
//                L.m("MusicStream", "URL exc");
//            } catch (IOException e) {
//                L.m("MusicStream", "IO exc " + e.toString());
//                broadcastURI(Constants.STREAM_PREPARE_FAILED_URL_FLAG, file);
//            }
//        }
//    }

    private void requestStreamUrlUsingVolley(final String v_id) {

        try {
            VolleyUtils.getInstance().cancelPendingRequests(STREAM_URL_REQUEST_TAG_VOLLEY);
            Log.d("MusicStreamer", " Cancellig Pending Volley Requests For Stream Url");

        } catch (Exception e) {
            Log.d("MusicStreamer", " Attempt To Cancel NoRequests");
        }

        final String t_v_id = v_id;
        final String streaming_url_pref = Constants.SERVER_URL;
        String url = Constants.SERVER_URL + "/api/v1/stream?url=" + t_v_id;
        Log.d("MusicStream"," requesting url for stream on:"+url) ;
        StringRequest updateCheckReq = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(result.toString());
                            if (obj.getInt("status") == 200) {

                                L.m("MusicStream", "stream Url " + streaming_url_pref);
                                onStreamUriFetchedListener.onUriAvailable(streaming_url_pref+obj.getString("url"));

                                if(doBroadcast)
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
                        Log.d("MusicStreamer", " VolleyError " + volleyError);
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
