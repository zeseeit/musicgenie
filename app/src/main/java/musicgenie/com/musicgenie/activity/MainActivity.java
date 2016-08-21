package musicgenie.com.musicgenie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;

import musicgenie.com.musicgenie.adapters.TrendingRecyclerViewAdapter;
import musicgenie.com.musicgenie.interfaces.TaskAddListener;
import musicgenie.com.musicgenie.notification.AlertDialogManager;
import musicgenie.com.musicgenie.utilities.AppConfig;
import musicgenie.com.musicgenie.utilities.ConnectivityUtils;
import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.SearchResultListAdapter;
import musicgenie.com.musicgenie.utilities.Segmentor;
import musicgenie.com.musicgenie.utilities.SharedPrefrenceUtils;
import musicgenie.com.musicgenie.utilities.SoftInputManager;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.utilities.VolleyUtils;

//TODO: add activity transition on swipe
public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ListView resultListView;
    SearchResultListAdapter adapter;
    FloatingSearchView searchView = null;
    DrawerLayout mDrawerLayout;
    private static final String TAG = "MainActivity";
    private boolean mFloatingSearchViewSet;
    private FloatingActionButton fab;
    private TrendingRecyclerViewAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    // private ConnectivityBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // inits views
        init();
        // make dirs
        configure();
        // load trending on preferences
        loadTrendingSongs(savedInstanceState);
        // reload previous loaded
        reload(savedInstanceState);
        // generate alert for pending downloads , options for re-download or remove them at all
        checkPendings();
        // sets SearchView in action with event-listeners
        setSearchView();
        // floating action button
        //TODO: FAB can be removed
       // pinFAB();

    }

    private void reload(Bundle savedInstanceState) {
        //TODO: reload data from parcelable source - If Available
    }

    private void init() {

        if (isPortrait(getOrientation())) {
            setUpRecycler(3);
        } else {
            setUpRecycler(4);
        }

    }

    private void configure() {
        AppConfig.getInstance(this).configureDevice();
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
                log("query changed from " + s + " to " + s1);
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
                    Intent i = new Intent(MainActivity.this,SectionedListViewTest.class);
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
        String url = AppConfig.SERVER_URL + "/search?q=" + URLEncoder.encode(term);
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
        ArrayList<Song> songs = new ArrayList<>();
        // parse youtube results
        try {
            JSONArray results = new JSONArray(response);
            for (int i = 0; i < results.length(); i++) {
                String enc_v_id = results.getJSONObject(i).getString("get_url").substring(3);
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

        // results are submitted from here
        //
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        subscribeToTaskAddListener();
        // add songs
        mRecyclerAdapter.addSongs(songs, "Results");
        // set adapter
        plugAdapter();
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
        progressDialog.dismiss();

    }

    private void setUpRecycler(int mxCols) {
        mRecyclerView = (RecyclerView) findViewById(R.id.trendingRecylerView);
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        layoutManager = new StaggeredGridLayoutManager(mxCols, 1);
        mRecyclerView.setLayoutManager(layoutManager);

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
            return 0;
        } else {
            return 1;
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

    private void makeSnake(String msg) {
        Snackbar.make(resultListView, msg, Snackbar.LENGTH_LONG).show();
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void log(String _lg) {
        Log.d(TAG, _lg);
    }

}
