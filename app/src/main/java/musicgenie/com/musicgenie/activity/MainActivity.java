package musicgenie.com.musicgenie.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import musicgenie.com.musicgenie.adapters.TrendingRecyclerViewAdapter;
import musicgenie.com.musicgenie.interfaces.TaskAddListener;
import musicgenie.com.musicgenie.notification.AlertDialogManager;
import musicgenie.com.musicgenie.utilities.AppConfig;
import musicgenie.com.musicgenie.utilities.ConnectivityUtils;
import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.SearchResultListAdapter;
import musicgenie.com.musicgenie.utilities.FontManager;
import musicgenie.com.musicgenie.utilities.Segmentor;
import musicgenie.com.musicgenie.utilities.SharedPrefrenceUtils;
import musicgenie.com.musicgenie.utilities.SoftInputManager;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.utilities.VolleyUtils;

//TODO: add activity transition on swipe
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ProgressDialog progressDialog;
    MediaPlayer mPlayer;
    SeekBar streamSeeker;
    TextView currentTrackPosition;
    TextView totalTrackPosition;
    TextView playPauseBtn;
    TextView streamingItemTitle;
    Boolean prepared = false;
    ListView resultListView;
    SearchResultListAdapter adapter;
    FloatingSearchView searchView = null;
    DrawerLayout mDrawerLayout;
    private boolean isStreaming;
    private boolean zygoteStreamer;
    private boolean mFloatingSearchViewSet;
    private FloatingActionButton fab;
    private HashMap<String, ArrayList<Song>> songMap;
    private TrendingRecyclerViewAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private StreamUriBroadcastReceiver receiver;
    private boolean mReceiverRegistered;
    private boolean musicIntentLaunched;
    // private ConnectivityBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // make dirs
        configure();
        // load trending on preferences
        loadTrendingSongs(savedInstanceState);
        // reload previous loaded
        reload(savedInstanceState);
        // sets SearchView in action with event-listeners
        setSearchView();
        // floating action button
        //TODO: FAB can be removed
       // pinFAB();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        log("back Pressed Player " + isStreaming);
        if(isStreaming){
            mPlayer.stop();
            mPlayer.reset();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        log("saving back map");
        outState.putSerializable("mapSong", songMap);
    }

    private void reload(Bundle savedInstanceState) {
        if(savedInstanceState==null){
            // means it is first time app load
            log("its first load");
            checkPendings();
        }else{
            // means it is reload/orientation change
            // load previous data if any
            if(savedInstanceState.getSerializable("mapSong")!=null){
                //  get map and iterate through it and grab songs
                // add to adapter and plug it
                mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
                mRecyclerAdapter.setSongs(null, "");
                init();
                subscribeToTaskAddListener();
                HashMap<String,ArrayList<Song>> map = (HashMap<String, ArrayList<Song>>) savedInstanceState.getSerializable("mapSong");
                Iterator iterator = map.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    log("reading hashmap for key "+pair.getKey().toString());
                    log("adding songs");
                    songMap.put(pair.getKey().toString(),map.get(pair.getKey()));
                    mRecyclerAdapter.appendSongs(map.get(pair.getKey()), pair.getKey().toString());
                }
            }


        }


    }

    private void init() {

        songMap = new HashMap<>();

        if (isPortrait(getOrientation())) {
            if(screenMode()==AppConfig.SCREEN_MODE_MOBILE){
                setUpRecycler(2);
            }else{
                setUpRecycler(3);
            }
        } else {
            setUpRecycler(4);
        }

    }

    private void configure() {
        log("making dirs");
        AppConfig.getInstance(this).configureDevice();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        reload(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mFloatingSearchViewSet)setSearchView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeToTaskAddListener();
        //unRegisterBroadcast();
    }

    public void checkPendings(){
        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
        if(utils.getCurrentDownloadsCount()==0){ // check for on-going download process
            int pendingCount = new Segmentor().getParts(utils.getTaskSequence(),'#').size();
            if(pendingCount>0){
                AlertDialogManager.getInstance(this).popAlertForPendings(pendingCount);
            }
        }
    }

    public void setSearchView() {
        mFloatingSearchViewSet = true;
        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String s, String s1) {
               // log("query changed from " + s + " to " + s1);
            }
        });


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                log("suggestion clicked");
            }

            @Override
            public void onSearchAction(String s) {
                log("search action clicked");
                fireSearch(s);
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                log("onFocusChange()");
            }
        });
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_settings) {
                    Intent i = new Intent(MainActivity.this, UserPreferenceSetting.class);
                    startActivity(i);
                }
                if (id == R.id.action_downloads) {
                    Intent i = new Intent(MainActivity.this, DowloadsActivity.class);
                    startActivity(i);
                }

                if (id == R.id.testPage) {
                    Intent i = new Intent(MainActivity.this, SectionedListViewTest.class);
                    startActivity(i);
                }

            }
        });



    }

    public void AddSuggestionToSharedPreferences(String suggestion) {

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
        String currStack = utils.getSuggestionList();
        currStack += suggestion + "#";
        currStack = currStack.substring(0, currStack.length());
        SharedPrefrenceUtils.getInstance(this).setSuggestionsList(currStack);

    }

    public ArrayList<String> getSuggestionList(){
        ArrayList<String> suggs;
        String _s = SharedPrefrenceUtils.getInstance(this).getSuggestionList();
        suggs = new Segmentor().getParts(_s, '#');
        return suggs;
    }

    private void fireSearch(String term) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialog_msg));
        progressDialog.show();

        if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {

            getCurrentFocus().clearFocus();
            SoftInputManager.getInstance(this).hideKeyboard(searchView);
            progressDialog.dismiss();
            makeSnake("No Internet Connection !! ");
            return;
        }
        AddSuggestionToSharedPreferences(term);
        String url = AppConfig.SERVER_URL + "/api/v1/search?q=" + URLEncoder.encode(term);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ;
                parseSearchResults(response);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("[fireSearch()] Error While searching :" + volleyError);
            }
        });

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, this);

    }

    private void parseSearchResults(String response) {

        log("got result " + response);
        ArrayList<Song> songs = new ArrayList<>();
        // parse youtube results


        try {
            JSONObject rootObj = new JSONObject(response);
            int results_count = rootObj.getJSONObject("metadata").getInt("count");

            JSONArray results = rootObj.getJSONArray("results");
            for (int i = 0; i < results_count; i++) {
                String enc_v_id = results.getJSONObject(i).getString("get_url").substring(14);
               // log("===? v id >" + enc_v_id);
                songs.add(new Song(results.getJSONObject(i).getString("title"),
                        results.getJSONObject(i).getString("length"),
                        results.getJSONObject(i).getString("uploader"),
                        results.getJSONObject(i).getString("thumb"),
                        enc_v_id,
                        results.getJSONObject(i).getString("time"),
                        results.getJSONObject(i).getString("views")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        init();
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        subscribeToTaskAddListener();
        songMap.put("Results", songs);
        log("adding results to adapter");
        plugAdapter();
        mRecyclerAdapter.setSongs(songs, "Results");

    }

    private void loadTrendingSongs(Bundle saved) {
        if (!SharedPrefrenceUtils.getInstance(this).getOptionForTrendingAudio() || saved!=null)return; // user denied for trending load
        // saved!=null  means it is back-navigation from another activity and its not the first time.

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Trending Songs...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //load trending items
        // then dismiss progressDialog
        requestPlaylist();


    }

    private void requestTrending(final String type, final boolean isLast) {

        if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {

            getCurrentFocus().clearFocus();
            SoftInputManager.getInstance(this).hideKeyboard(searchView);
            progressDialog.dismiss();
            makeSnake("No Internet Connection !! ");
            return;
        }
        //AddSuggestionToSharedPreferences(term);
        String url = AppConfig.SERVER_URL + "/api/v1/trending?number=6&type=" + type;// + URLEncoder.encode(term);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ;
                parseTrendingResults(response, isLast);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("[fireSearch()] Error While searching :" + volleyError);
            }
        });

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, this);

    }

    private void parseTrendingResults(String response,boolean lastItemLoaded) {

        ArrayList<Song> songs = new ArrayList<>();
        String type = "Results";
        try {
            JSONObject rootObj = new JSONObject(response);
            int results_count = rootObj.getJSONObject("metadata").getInt("count");
            type = rootObj.getJSONObject("metadata").getString("type");
            JSONArray results = rootObj.getJSONArray("results");
            for (int i = 0; i < results_count; i++) {
                String enc_v_id = results.getJSONObject(i).getString("get_url").substring(14);
                songs.add(new Song(results.getJSONObject(i).getString("title"),
                        results.getJSONObject(i).getString("length"),
                        results.getJSONObject(i).getString("uploader"),
                        results.getJSONObject(i).getString("thumb"),
                        enc_v_id,
                        "missing",
                        results.getJSONObject(i).getString("views")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // what to do

        // trending results are submitted from here

        init();
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        subscribeToTaskAddListener();
        // add songs
        plugAdapter();
        mRecyclerAdapter.appendSongs(songs, type);

        //songMap.put(type,songs);
        // set adapter
        // plug adapter once all songs has been loaded
        songMap.put(type,songs);
        if(lastItemLoaded){
            plugAdapter();
            mRecyclerAdapter.appendSongs(songs, type);
            progressDialog.dismiss();
        }

    }

    private void requestPlaylist() {

        if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {

            getCurrentFocus().clearFocus();
            SoftInputManager.getInstance(this).hideKeyboard(searchView);
            progressDialog.dismiss();
            makeSnake("No Internet Connection !! ");
            return;
        }
        //AddSuggestionToSharedPreferences(term);
        String url = AppConfig.SERVER_URL + "/api/v1/playlists";// + URLEncoder.encode(term);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ;
                parsePlaylistsResults(response);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("[fireSearch()] Error While searching :" + volleyError);
            }
        });

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, this);

    }

    private void parsePlaylistsResults(String response) {

//        ArrayList<String> playlist = new ArrayList<>();
        try {
            JSONObject rootPlaylistObj = new JSONObject(response);
            int typeCount = rootPlaylistObj.getJSONObject("metadata").getInt("count");
            JSONArray types = rootPlaylistObj.getJSONArray("results");
            for (int i = 0; i < typeCount; i++) {
                JSONObject typeObj = (JSONObject) types.get(i);
                log("requesting trending of type " + typeObj.getString("playlist"));
                boolean last = (i == (typeCount - 1)) ? true : false;
                requestTrending(typeObj.getString("playlist"), last);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setUpRecycler(int mxCols) {
        mRecyclerView = (RecyclerView) findViewById(R.id.trendingRecylerView);
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        layoutManager = new StaggeredGridLayoutManager(mxCols, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        plugAdapter();
    }
//
//    private void puffRecyclerWithData(){
////        mRecyclerAdapter.addSongs(list, "Pop");
////        mRecyclerAdapter.addSongs(list,"Rock");
//          plugAdapter();
//    }
    private void plugAdapter() {
        mRecyclerAdapter.setOrientation(getOrientation());
        mRecyclerAdapter.setScreenMode(screenMode());
        mRecyclerView.setAdapter(mRecyclerAdapter);
        registerForBroadcastListen(this);
        subscribeForStreamOption(mRecyclerAdapter);
    }

    private void subscribeForStreamOption(TrendingRecyclerViewAdapter adapter) {
        adapter.setOnStreamingSourceAvailable(new TrendingRecyclerViewAdapter.OnStreamingSourceAvailableListener() {
            @Override
            public void onPrepared(String uri) {


            }

            @Override
            public void optioned() {

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Requesting Audio For You....");
                progressDialog.show();

            }
        });
    }

    private int getOrientation() {
        return getWindowManager().getDefaultDisplay().getOrientation();
    }

    private int screenMode() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;

        double diagonal = Math.sqrt(yInches * yInches + xInches * xInches);
        if (diagonal > 6.5) {
            return AppConfig.SCREEN_MODE_TABLET;
        } else {
            return AppConfig.SCREEN_MODE_MOBILE;
        }
    }

    private boolean isPortrait(int orientation) {
        return orientation % 2 == 0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        setContentView(R.layout.sectioned_view);
        mRecyclerAdapter.setOrientation(newConfig.orientation);
        Log.e(TAG, " nConfigurationChanged to" + newConfig.orientation);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        super.onConfigurationChanged(newConfig);
    }

    private void subscribeToTaskAddListener() {
        TrendingRecyclerViewAdapter.getInstance(this).setOnTaskAddListener(new TaskAddListener() {
            @Override
            public void onTaskTapped() {
                log("callback: task tapped");
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Requesting Your Stuff..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onTaskAddedToQueue(String task_info) {
                log("callback: task added to download queue");
                progressDialog.dismiss();
                makeToast(task_info + " Added To Download");
                //TODO: navigate to DownloadsActivity
            }
        });
    }

    private void unsubscribeToTaskAddListener() {
        TrendingRecyclerViewAdapter.getInstance(this).setOnTaskAddListener(null);
    }

    private void prepareStreaming(String uri , String file_name){

       // progressDialog.hide();
       // progressDialog.dismiss();

        makeStreamingDialog(file_name);

        final String uriToStream = uri;
        isStreaming = true;
//        playPauseBtn.setText(getResources().getString(R.string.pauseStreamFontText));
        new Player().execute(uriToStream);
        log("playBtn "+playPauseBtn);

        streamSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {

                if (fromUser)
                    mPlayer.seekTo(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void makeStreamingDialog(String file_name) {

        Dialog streamDialog = new Dialog(this);

        streamDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                progressDialog.dismiss();

                if(isStreaming){
                    mPlayer.stop();
                    mPlayer.reset();
                }
            }
        });

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.stream_layout, (ViewGroup)findViewById(R.id.dialogParent));


        streamingItemTitle = (TextView) layout.findViewById(R.id.streamItemTitle);
        // playPauseBtn = (TextView) layout.findViewById(R.id.playPauseBtn);
        streamSeeker = (SeekBar) layout.findViewById(R.id.streaming_audio_seekbar);
        //streamSeeker.setEnabled(false);
        Typeface tf = FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        currentTrackPosition = (TextView) layout.findViewById(R.id.currentTrackPositionText);
        totalTrackPosition = (TextView) layout.findViewById(R.id.totalTrackLengthText);
        currentTrackPosition.setTypeface(tf);
        totalTrackPosition.setTypeface(tf);

        streamSeeker.getProgressDrawable().setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
        streamSeeker.getThumb().setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);

//        mPlayer = new MediaPlayer();
//        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        streamingItemTitle.setText(file_name);
        // playPauseBtn.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_AWESOME));
        streamDialog.setTitle("Streaming...");
        streamDialog.setContentView(layout);
        streamDialog.show();
    }

    private void registerForBroadcastListen(Context context) {
        receiver = new StreamUriBroadcastReceiver();
        if(!mReceiverRegistered) {
            context.registerReceiver(receiver, new IntentFilter(AppConfig.ACTION_STREAM_URL_FETCHED));
            mReceiverRegistered = true;
        }
    }

    private void unRegisterBroadcast() {
        if(mReceiverRegistered){
            this.unregisterReceiver(receiver);
            mReceiverRegistered = false;
        }
    }

    private void makeSnake(String msg) {
        Snackbar.make(resultListView, msg, Snackbar.LENGTH_LONG).show();
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void log(String _lg) {
        Log.d(TAG, _lg);
    }

    class Player extends AsyncTask<String,Void,Boolean>{

        Context context;
        private ProgressDialog progressDialog;

        public Player(Context context) {
            this.context = context;
        }
        public Player() {
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                 mPlayer =  MediaPlayer.create(MainActivity.this, Uri.parse(strings[0]));
                 mPlayer.setScreenOnWhilePlaying(true);
                 log("playing");
                 mPlayer.start();

                while(mPlayer.isPlaying()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int currentPos = mPlayer.getCurrentPosition();
                            int total = mPlayer.getDuration();

                            streamSeeker.setMax(total);
                            log("currently at " + currentPos);
                            currentTrackPosition.setText(getTimeFromMillisecond(currentPos));
                            totalTrackPosition.setText(getTimeFromMillisecond(total));
                            streamSeeker.setProgress(currentPos);
                        }
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                 mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                     @Override
                     public void onCompletion(MediaPlayer mediaPlayer) {
                         isStreaming = false;
                     }
                 });
                mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        isStreaming = false;
                        return true;
                    }
                });


            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }


            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            Log.d("Prepared", "//" + result);
            zygoteStreamer = false;
            isStreaming = false;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            this.progressDialog.setMessage("Buffering...");
//            this.progressDialog.show();

        }

        private String getTimeFromMillisecond(int millis){
            String hr = "";
            String min="";
            String  sec = "";
            String time="";
            int i_hr = (millis/1000)/3600;
            int i_min = (millis/1000)/60;
            int i_sec = (millis / 1000) % 60;

            if(i_hr==0){
                min = (String.valueOf(i_min).length()<2)?"0"+i_min:String.valueOf(i_min);
                sec = (String.valueOf(i_sec).length()<2)?"0"+i_sec:String.valueOf(i_sec);
                time = min +" : "+sec;
            }else{
                hr = (String.valueOf(i_hr).length()<2)?"0"+i_hr:String.valueOf(i_hr);
                min = (String.valueOf(i_min).length()<2)?"0"+i_min:String.valueOf(i_min);
                sec = (String.valueOf(i_sec).length()<2)?"0"+i_sec:String.valueOf(i_sec);
                time =hr+" : "+ min +" : "+sec;
            }

            return time;
        }

    }

    public class StreamUriBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(AppConfig.ACTION_STREAM_URL_FETCHED)) {


                log("update via broadcast: streaming uri fetched " + intent.getStringExtra(AppConfig.EXTRAA_URI));
//                if(!musicIntentLaunched) {
//                    musicIntentLaunched = true;
//                    Intent mIntent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse(intent.getStringExtra(AppConfig.EXTRAA_URI)), "audio/m4a");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(mIntent);
//                }

                if(!isStreaming)
                prepareStreaming(intent.getStringExtra(AppConfig.EXTRAA_URI) , intent.getStringExtra(AppConfig.EXTRAA_STREAM_FILE));
            }
        }

    }
}
