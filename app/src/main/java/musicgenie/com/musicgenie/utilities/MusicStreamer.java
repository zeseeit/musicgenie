package musicgenie.com.musicgenie.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import musicgenie.com.musicgenie.adapters.LiveDownloadListAdapter;
import musicgenie.com.musicgenie.interfaces.DownloadCancelListener;
import musicgenie.com.musicgenie.interfaces.DownloadListener;
import musicgenie.com.musicgenie.notification.LocalNotificationManager;
import musicgenie.com.musicgenie.regulators.PermissionManager;

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
        this.context = context;
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
        log("started fetching uri");
    }

    private void broadcastURI(String t_url,String file) {

            Intent intent = new Intent(AppConfig.ACTION_STREAM_URL_FETCHED);
            intent.putExtra(AppConfig.EXTRAA_URI, t_url);
        intent.putExtra(AppConfig.EXTRAA_STREAM_FILE, file);
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
            String t_url = AppConfig.SERVER_URL+ "/api/v1/stream?";

                try {
    // getting download url
                    String _url = AppConfig.SERVER_URL + "/api/v1/g?url=" + t_v_id;
                    log("for dwnd url requesting on "+_url);
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
                            t_url += obj.getString("url").substring(10);
                            log("download url:" + t_url);
                            onStreamUriFetchedListener.onUriAvailable(t_url);
                            broadcastURI(t_url, file);
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    log("URL exception " + e);
                } catch (IOException e) {
                    log("IO exception " + e);
                }
            }
    }


}
