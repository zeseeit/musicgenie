package musicgenie.com.musicgenie.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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

import java.util.ArrayList;

import musicgenie.com.musicgenie.utilities.App_Config;
import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.utilities.VolleyUtils;

/**
 * Created by Ankit on 8/5/2016.
 */
/*
*
* */

public class SearchResultListAdapter extends ArrayAdapter<Song> {

    ProgressDialog progressDialog;

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
        //panel = (LinearLayout) tempView.findViewById(R.id.control_panel);
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
        int limit = (song.Title.length()>15)?15:song.Title.length();
        final String file_name = song.Title.substring(0,limit);
        tempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("req for download "+file_name);
                addDownloadTask(v_id,file_name);
                //requestDownloadUrl(v_id, file_name);
            }
        });
        return tempView;
    }

    private void handleErrors() {
        log("some error occured");
        //TODO: handle error during link fetch
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

    private void addDownloadTask(final String video_id, final String file_name) {

                TaskHandler
                        .getInstance(context)
                        .addTask(file_name,video_id);

    }

    public void log(String _lg) {
        Log.d(TAG, _lg);
    }

}
