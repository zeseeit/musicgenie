package any.audio.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

import any.audio.Config.Constants;

/**
 * Created by Ankit on 8/26/2016.
 */
public class MusicStreamer {

    private static final String TAG = "MusicStreamer";
    private static Context context;
    private static MusicStreamer mInstance;
    private String vid;
    private String file;
    private OnStreamUriFetchedListener onStreamUriFetchedListener;


    public MusicStreamer(Context context) {
        MusicStreamer.context = context;
    }

    public static MusicStreamer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MusicStreamer(context);
        }
        return mInstance;
    }

    public MusicStreamer setOnStreamUriFetchedListener(OnStreamUriFetchedListener listener){
        this.onStreamUriFetchedListener = listener;
        return this;
    }

    public MusicStreamer setData(String v_id,String file_name){
       this.vid = v_id;
        this.file = file_name;
        return this;
    }

    public void initProcess(){
        new StreamThread(this.vid,this.file).start();
        L.m("MusicStream"," Thread Started: stream uri fetch");
    }

    private void broadcastURI(String t_url,String file) {

            Intent intent = new Intent(Constants.ACTION_STREAM_URL_FETCHED);
            intent.putExtra(Constants.EXTRAA_URI, t_url);
            intent.putExtra(Constants.EXTRAA_STREAM_FILE, file);
            context.sendBroadcast(intent);
        }

    private void log(String s) {
        Log.d(TAG, "log " + s);
    }

    public interface OnStreamUriFetchedListener{
        void onUriAvailable(String uri);
    }

    private class StreamThread extends Thread{

        private String v_id;
        private String file;
        public StreamThread(String v_id, String file) {
            this.v_id = v_id;
            this.file = file;
        }

        @Override
        public void run() {

            final String t_v_id = this.v_id;
            String streaming_url_pref = Constants.SERVER_URL;

                try {

                    String _url = Constants.SERVER_URL + "/api/v1/stream?url=" + t_v_id;
                    L.m("MusicStream"," Requesting for stream url - req on -"+_url);
                    URL u = new URL(_url);
                    URLConnection dconnection = u.openConnection();
                    dconnection.setReadTimeout(20000);
                    dconnection.setConnectTimeout(20000);
                    dconnection.connect();
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(dconnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    try {
                        JSONObject obj = new JSONObject(result.toString());
                        if(obj.getInt("status")==200){
                            streaming_url_pref += obj.getString("url");
                            L.m("MusicStream","stream Url "+streaming_url_pref);
                            onStreamUriFetchedListener.onUriAvailable(streaming_url_pref);
                            broadcastURI(streaming_url_pref, file);
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
 //                   log("URL exception " + e);
                    L.m("MusicStream","URL exc");
                } catch (IOException e) {
                    L.m("MusicStream", "IO exc");
                    broadcastURI(Constants.STREAM_PREPARE_FAILED_URL_FLAG, file);
                }
            }
    }


}
