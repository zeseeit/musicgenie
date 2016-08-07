package musicgenie.com.musicgenie;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Ankit on 8/5/2016.
 */
public class SearchResultListAdapter extends ArrayAdapter<Song> {

    private static final String TAG = "ListAdapter";
    private static SearchResultListAdapter mInstance;
    private Context context;
    private ArrayList<Song> songs;

    public SearchResultListAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    public static SearchResultListAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SearchResultListAdapter(context);
        }
        return mInstance;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tempView = convertView;
        if (tempView == null) {
            tempView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        }
        // decs
        TextView track_title, track_duration, uploaded_by, extras;
        ImageView thumbnail;
        final LinearLayout panel;
        // ins
        track_title = (TextView) tempView.findViewById(R.id.song_title);
        track_duration = (TextView) tempView.findViewById(R.id.content_time_length);
        uploaded_by = (TextView) tempView.findViewById(R.id.song_uploader);
        extras = (TextView) tempView.findViewById(R.id.song_extras);
        thumbnail = (ImageView) tempView.findViewById(R.id.thumbnail_image);
        panel = (LinearLayout) tempView.findViewById(R.id.control_panel);
        // bind
        final Song song = songs.get(position);
        track_title.setText(song.Title);
        track_duration.setText(song.TrackDuration);
        uploaded_by.setText(song.UploadedBy);
        extras.setText(song.TimeSinceUploaded + " . " + song.UserViews + " Views");

        Picasso.with(context)
                .load(song.Thumbnail_url)
                .into(thumbnail);
        //requestAndSetThumbnail(thumbnail, song.Thumbnail_url);


        final String v_id = song.Video_id;
        final String file_name = song.Title.substring(0, 15);
        tempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: request download url
                requestDownloadUrl(v_id, file_name);
                // panel.setVisibility(View.VISIBLE);
                //        log("setting to vis");
            }
        });
        return tempView;
    }

    private void requestDownloadUrl(final String v_id, final String file_name) {

        String url = App_Config.SERVER_URL + "/g/" + v_id;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                log("got url resp " + response);
                handleResponse(response, file_name);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("Error While searching :" + volleyError);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, context);

    }

    private void handleResponse(String response, String file_name) {
        //TODO: parse the response for download_url and start Downloading in background using asyncTask
        String download_url = App_Config.SERVER_URL;
        int status = -1;
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getInt("status") == 0) {
                download_url += obj.getString("url");
                log("downloading url > " + download_url);
                sendForDownload(download_url, file_name);
            } else {
                //TODO: some customized error can be handled
                handleErrors();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleErrors() {
        log("some error occured");
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    public void requestAndSetThumbnail(final ImageView thumbnail, String thumbnail_url) {

        ImageRequest request = new ImageRequest(thumbnail_url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        thumbnail.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        log("Error While Loading Thumbmail :" + error);
                    }
                });

        VolleyUtils.getInstance().addToRequestQueue(request, "_thumb_imgReq", context);
    }

    public void log(String _lg) {
        Log.d(TAG, _lg);
    }

    private void sendForDownload(final String download_url, String file_name) {

        new DownLoadFile(download_url, file_name).execute();
    }

    public class DownLoadFile extends AsyncTask<String, Integer, String> {

        private Context context;
        private String songURL;
        private String filename;

        public DownLoadFile() {
        }

        public DownLoadFile(String uri, String filename) {
            this.songURL = uri;
            this.filename = filename;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("download", "in doinBack");
            int count;
            //songPath="http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";
            try {
                URL url = new URL(songURL);
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(20000);
                connection.connect();

                int fileLength = connection.getContentLength();
                log("content len "+fileLength);
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/Musicgenie/Audio");

                File file = new File(dir, filename.trim() + ".mp3");
                log("writing to "+file.getAbsolutePath().toString());
                // download file
                InputStream inputStream = new BufferedInputStream(url.openStream());
                OutputStream outputStream = new FileOutputStream(file);


                byte data[] = new byte[1024];
                long total = 0;
                //Log.e("dd",""+inputStream.read());

                while ((count = inputStream.read(data)) != -1) {
                    log("still..");
                    total += count;
                    publishProgress((int) total * 100 / fileLength);
                    outputStream.write(data, 0, count);
                    log("downloaded size:" + ((int) total * 100 / fileLength) + " %");
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            log("all done !!!");

        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            log("done.."+ values[0] + " %");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            log("starting download");
        }

    }


}
